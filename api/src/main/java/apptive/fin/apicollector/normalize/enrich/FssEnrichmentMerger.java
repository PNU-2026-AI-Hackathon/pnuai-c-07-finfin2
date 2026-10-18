package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.global.util.JsonNodes;
import apptive.fin.apicollector.global.util.TextMatch;
import apptive.fin.apicollector.llm.LlmProductEnrichment;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.normalize.extractor.PreferentialRateReducer;
import apptive.fin.apicollector.normalize.extractor.keywords.TermKeywords;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * LLM enrichment 결과를 정규화 draft에 병합한다(기존 값 우선, 소득/우대금리/필수키워드 규칙 적용).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FssEnrichmentMerger {

    private static final String[] INCOME_IRRELEVANT_PHRASES = {
            "소득공제", "소득세", "금융소득종합과세", "소득이체"
    };
    private static final String[] INCOME_TOKENS = {"소득", "총급여", "연봉"};

    private final ObjectMapper objectMapper;

    public ProductDraft merge(ProductRaw rawProduct, ProductDraft draft, LlmProductEnrichment enrichment) {
        List<ProductPropertyDraft> properties = new ArrayList<>();
        String eligibilityText = eligibilityText(rawProduct);
        boolean incomeMentioned = mentionsIncome(draft.content(), eligibilityText);
        for (ProductPropertyDraft property : draft.properties()) {
            properties.add(merge(property, enrichment, eligibilityText, draft.type(), incomeMentioned));
        }

        return draft.toBuilder()
                .contentSummary(JsonNodes.blankToNull(enrichment.summaryContent()) == null
                        ? draft.contentSummary()
                        : enrichment.summaryContent().trim())
                .properties(properties)
                .build();
    }

    // earnMaxAmt(연소득 상한)·earnPercent(소득기준 %)는 원문에 소득 요건 언급이 있을 때만 LLM 값을 신뢰한다.
    // LLM이 가입금액/예치한도 등을 소득 상한으로 착각해 채우는 오류를 원천 차단하기 위한 보수적 가드.
    // 1단계(공백 정규화): 원문이 "총 급여액", "소득 공제"처럼 띄어써도 토큰("총급여")·무관문구("소득공제")와
    //   일관되게 매칭되도록 공백을 모두 제거한다(부분문자열 매칭의 띄어쓰기 취약점 제거).
    // 2단계(무관문구 제거): "소득공제", "소득세", "금융소득종합과세", "소득이체" 등 소득요건과 무관한 문구를
    //   제거해 오수용(false positive)을 차단한다 - 이 문구만 있고 실제 소득요건이 없는데 통과하는 것을 방지.
    // 3단계: 남은 텍스트에 "소득", "총급여", "연봉" 중 하나라도 남아 있으면 소득요건 언급으로 인정한다
    //   (오거부 방지 - 총급여/연봉 표현도 소득요건으로 수용).
    private boolean mentionsIncome(String content, String eligibilityText) {
        return mentionsIncomeToken(content) || mentionsIncomeToken(eligibilityText);
    }

    private boolean mentionsIncomeToken(String value) {
        return TextMatch.containsAny(normalizeForIncomeMatch(value), INCOME_TOKENS);
    }

    private String normalizeForIncomeMatch(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.replaceAll("\\s+", "");
        for (String phrase : INCOME_IRRELEVANT_PHRASES) {
            normalized = normalized.replace(phrase, "");
        }
        return normalized;
    }

    private ProductPropertyDraft merge(
            ProductPropertyDraft property,
            LlmProductEnrichment enrichment,
            String eligibilityText,
            ProductType type,
            boolean incomeMentioned
    ) {
        // 정기예금(DEPOSIT)은 월 납입 개념이 없다. min/maxMonthlyLimit은 월 납입액 전용 필드이므로
        // 일시납 가입금액이 잘못 채워지지 않도록 null로 강제한다(LLM 준수 여부와 무관하게 보장).
        boolean isDeposit = type == ProductType.DEPOSIT;
        return property.toBuilder()
                .minMonthlyLimit(isDeposit ? null : firstNonNull(property.minMonthlyLimit(), enrichment.minMonthlyLimit()))
                .maxMonthlyLimit(isDeposit ? null : firstNonNull(property.maxMonthlyLimit(), enrichment.maxMonthlyLimit()))
                .minAge(firstNonNull(property.minAge(), enrichment.minAge()))
                .maxAge(firstNonNull(property.maxAge(), enrichment.maxAge()))
                .earnMaxAmt(firstNonNull(property.earnMaxAmt(), incomeMentioned ? enrichment.earnMaxAmt() : null))
                .earnPercent(firstNonNull(property.earnPercent(), incomeMentioned ? enrichment.earnPercent() : null))
                .govContributionRate(firstNonNull(property.govContributionRate(), enrichment.govContributionRate()))
                .govContributionType(firstNonNull(property.govContributionType(), enrichment.govContributionType()))
                .govMatchingRatio(firstNonNull(property.govMatchingRatio(), enrichment.govMatchingRatio()))
                .govMonthlyFixedContribution(firstNonNull(
                        property.govMonthlyFixedContribution(),
                        enrichment.govMonthlyFixedContribution()
                ))
                .govContributionPeriodMonths(firstNonNull(
                        property.govContributionPeriodMonths(),
                        enrichment.govContributionPeriodMonths()
                ))
                .excludeFromRateComparison(firstTrue(property.excludeFromRateComparison(), enrichment.excludeFromRateComparison()))
                .allowsMilitaryAgeExtension(firstTrue(
                        property.allowsMilitaryAgeExtension(),
                        enrichment.allowsMilitaryAgeExtension()
                ))
                .militaryMaxAge(firstNonNull(property.militaryMaxAge(), enrichment.militaryMaxAge()))
                .requiresHomeless(firstTrue(property.requiresHomeless(), enrichment.requiresHomeless()))
                .requiresHouseholder(firstTrue(property.requiresHouseholder(), enrichment.requiresHouseholder()))
                .keywords(mergeKeywords(property, enrichment))
                .requiredKeywords(mergeRequiredKeywords(
                        property.requiredKeywords(),
                        filteredRequiredKeywords(enrichment.requiredKeywords(), eligibilityText)
                ))
                .preferentialRates(mergePreferentialRates(property.preferentialRates(), enrichment.preferentialRates()))
                .build();
    }

    private List<KeywordValueEnum> mergeKeywords(ProductPropertyDraft property, LlmProductEnrichment enrichment) {
        Set<KeywordValueEnum> keywords = EnumSet.noneOf(KeywordValueEnum.class);
        keywords.addAll(property.keywords());
        for (String keyword : enrichment.keywords()) {
            KeywordValueEnum keywordValue = KeywordValueEnum.from(keyword);
            // TERM_*는 saveTerm으로 별도 산출하고, BENEFIT_MAX_INTEREST는 정적 태깅하지 않는다
            // (최고이율은 검색 시점 동적 판정, PRD A-2). LLM이 넣어도 무시.
            if (keywordValue != null
                    && !keywordValue.name().startsWith("TERM_")
                    && keywordValue != KeywordValueEnum.BENEFIT_MAX_INTEREST) {
                keywords.add(keywordValue);
            }
        }

        if (property.saveTerm() != null) {
            keywords.add(TermKeywords.bucket(property.saveTerm()));
        }

        return List.copyOf(keywords);
    }

    private List<RequiredKeywordDraft> mergeRequiredKeywords(
            List<RequiredKeywordDraft> existing,
            List<RequiredKeywordDraft> enrichment
    ) {
        Map<String, RequiredKeywordDraft> merged = new LinkedHashMap<>();
        for (RequiredKeywordDraft draft : existing) {
            merged.put(requiredKeywordKey(draft), draft);
        }
        for (RequiredKeywordDraft draft : enrichment) {
            merged.put(requiredKeywordKey(draft), draft);
        }
        return List.copyOf(merged.values());
    }

    private List<RequiredKeywordDraft> filteredRequiredKeywords(
            List<RequiredKeywordDraft> enrichment,
            String eligibilityText
    ) {
        List<RequiredKeywordDraft> result = new ArrayList<>();
        for (RequiredKeywordDraft draft : enrichment) {
            if (draft.confidence() != ExtractionConfidence.HIGH) {
                log.info("Dropping low-confidence LLM required keyword. keyword={}", draft);
                continue;
            }
            if (!matchesRequiredKeyword(draft, eligibilityText)) {
                log.info("Dropping unsupported LLM required keyword. keyword={}, eligibilityText={}", draft, eligibilityText);
                continue;
            }
            result.add(draft);
        }
        return List.copyOf(result);
    }

    private boolean matchesRequiredKeyword(RequiredKeywordDraft draft, String eligibilityText) {
        if (eligibilityText == null || eligibilityText.isBlank()) {
            return false;
        }
        if (draft.effect() == RequiredKeywordEffect.EXCLUDE && !hasExcludeExpression(eligibilityText)) {
            return false;
        }

        return switch (draft.keywordCode()) {
            case STATUS_SME_WORKER -> TextMatch.containsAny(
                    eligibilityText,
                    "중소기업근로자",
                    "중기근로자",
                    "중소기업 재직",
                    "중소기업 재직자",
                    "중소기업 근로자",
                    "중소기업에 재직",
                    "중소기업 취업",
                    "중소기업 청년"
            );
            case STATUS_MILITARY -> TextMatch.containsAny(
                    eligibilityText,
                    "군인",
                    "장병",
                    "군 복무",
                    "군복무",
                    "병역복무자",
                    "군 복무자",
                    "군복무자"
            ) && !TextMatch.containsAny(eligibilityText, "병역 이행 기간", "병역이행기간", "나이 연장", "연령 연장");
            case STATUS_UNEMPLOYED -> TextMatch.containsAny(eligibilityText, "무직", "미취업", "구직자", "실업");
            case STATUS_PART_TIME -> TextMatch.containsAny(eligibilityText, "파트타임", "시간제", "단시간 근로", "단시간근로");
            default -> false;
        };
    }

    private boolean hasExcludeExpression(String value) {
        return TextMatch.containsAny(value, "제외", "가입 불가", "가입불가", "대상 아님", "대상아님", "불가능");
    }

    private String eligibilityText(ProductRaw rawProduct) {
        try {
            JsonNode base = objectMapper.readTree(rawProduct.getRawJson()).path("base");
            List<String> parts = new ArrayList<>();
            addIfNotBlank(parts, JsonNodes.text(base, "join_member"));
            addIfNotBlank(parts, JsonNodes.text(base, "etc_note"));
            return String.join(" ", parts);
        }
        catch (Exception e) {
            log.debug("Failed to parse FSS eligibility text. rawId={}", rawProduct.getId(), e);
            return "";
        }
    }

    private void addIfNotBlank(List<String> values, String value) {
        String normalized = JsonNodes.blankToNull(value);
        if (normalized != null) {
            values.add(normalized);
        }
    }

    private String requiredKeywordKey(RequiredKeywordDraft draft) {
        return draft.keywordCode() + ":" + draft.effect();
    }

    private List<PreferentialRateDraft> mergePreferentialRates(
            List<PreferentialRateDraft> existing,
            List<PreferentialRateDraft> enrichment
    ) {
        // LLM이 우대금리를 하나라도 제시하면 LLM 결과를 authoritative로 사용한다(규칙추출과 union하지 않음).
        // 규칙·LLM이 같은 조건을 다른 문자열/코드로 내놓아 중복·이중분류(예: 마케팅동의가 BANK_MARKETING과
        // BANK_ONLINE_JOIN 양쪽에)되던 문제를 원천 제거. LLM이 하나도 못 뽑았을 때만 규칙추출(existing)을
        // 폴백으로 유지해 max_rate 정합성 safety net을 남긴다.
        List<PreferentialRateDraft> source = enrichment.isEmpty() ? existing : enrichment;
        return PreferentialRateReducer.reduce(source);
    }

    private <T> T firstNonNull(T existing, T enrichment) {
        return existing != null ? existing : enrichment;
    }

    private boolean firstTrue(Boolean existing, Boolean enrichment) {
        return Boolean.TRUE.equals(existing) || Boolean.TRUE.equals(enrichment);
    }
}
