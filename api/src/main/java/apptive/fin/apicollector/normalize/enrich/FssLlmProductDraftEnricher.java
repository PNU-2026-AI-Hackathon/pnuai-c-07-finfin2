package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.llm.*;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import apptive.fin.apicollector.raw.ProductRaw;
import apptive.fin.apicollector.util.JsonNodes;
import apptive.fin.apicollector.util.Sha256;
import apptive.fin.apicollector.util.TextMatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class FssLlmProductDraftEnricher implements ProductDraftEnricher, StepExecutionListener {

    private static final Duration FAILED_RETRY_COOLDOWN = Duration.ofHours(6);

    private final CollectorProperties properties;
    private final List<LlmProviderClient> providerClients;
    private final LlmEnrichmentCacheRepository cacheRepository;
    private final ObjectMapper objectMapper;

    private final AtomicInteger cacheHits = new AtomicInteger();
    private final AtomicInteger llmCalls = new AtomicInteger();
    private final AtomicInteger llmFailures = new AtomicInteger();
    private final AtomicInteger cooldownSkips = new AtomicInteger();
    private final AtomicInteger invalidCacheEntries = new AtomicInteger();

    @Override
    public void beforeStep(StepExecution stepExecution) {
        cacheHits.set(0);
        llmCalls.set(0);
        llmFailures.set(0);
        cooldownSkips.set(0);
        invalidCacheEntries.set(0);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info(
                "FSS LLM enrichment summary. cacheHits={}, llmCalls={}, llmFailures={}, cooldownSkips={}, invalidCache={}",
                cacheHits.get(),
                llmCalls.get(),
                llmFailures.get(),
                cooldownSkips.get(),
                invalidCacheEntries.get()
        );
        return null;
    }

    @Override
    public boolean supports(Source source) {
        return source == Source.FSS;
    }

    @Override
    public ProductDraft enrich(ProductRaw rawProduct, ProductDraft draft) {
        if (!enabled() || !draft.shouldSaveProduct()) {
            return draft;
        }

        LlmProviderClient providerClient = providerClient();
        String prompt = prompt(rawProduct, draft);
        String requestHash = Sha256.hex(prompt);
        LlmEnrichmentCache cache = cache(rawProduct, requestHash);

        if (cache.getStatus() == LlmEnrichmentCacheStatus.SUCCESS
                && cache.getResponseJson() != null
                && requestHash.equals(cache.getRequestHash())) {
            cacheHits.incrementAndGet();
            return fromCache(cache, rawProduct, draft);
        }
        if (cache.isFailedRetryBlocked(Instant.now(), FAILED_RETRY_COOLDOWN)) {
            cooldownSkips.incrementAndGet();
            log.debug(
                    "Skipping FSS LLM enrichment during failed retry cooldown. rawId={}, externalId={}, failureCount={}",
                    rawProduct.getId(),
                    rawProduct.getExternalId(),
                    cache.getFailureCount()
            );
            return draft;
        }

        try {
            llmCalls.incrementAndGet();
            LlmProductEnrichment enrichment = providerClient.enrich(new LlmProductEnrichmentRequest(
                    properties.llm().model(),
                    prompt,
                    properties.llm().schemaVersion()
            ));
            validate(enrichment);

            cache.markSuccess(requestHash, objectMapper.writeValueAsString(enrichment));
            cacheRepository.save(cache);
            return merge(rawProduct, draft, enrichment);
        }
        catch (Exception e) {
            llmFailures.incrementAndGet();
            log.warn("FSS LLM enrichment failed. rawId={}, externalId={}", rawProduct.getId(), rawProduct.getExternalId(), e);
            cache.markFailed(requestHash, truncate(e.getMessage()));
            cacheRepository.save(cache);
            return draft;
        }
    }

    private ProductDraft fromCache(LlmEnrichmentCache cache, ProductRaw rawProduct, ProductDraft draft) {
        try {
            LlmProductEnrichment enrichment = objectMapper.readValue(cache.getResponseJson(), LlmProductEnrichment.class);
            validate(enrichment);
            return merge(rawProduct, draft, enrichment);
        }
        catch (Exception e) {
            invalidCacheEntries.incrementAndGet();
            log.warn("FSS LLM enrichment cache is invalid. cacheId={}", cache.getId(), e);
            return draft;
        }
    }

    private boolean enabled() {
        return properties.llm() != null
                && properties.llm().enabled()
                && properties.llm().apiKey() != null
                && !properties.llm().apiKey().isBlank();
    }

    private LlmProviderClient providerClient() {
        return providerClients.stream()
                .filter(client -> client.supports(properties.llm().provider()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unsupported LLM provider: " + properties.llm().provider()));
    }

    private LlmEnrichmentCache cache(ProductRaw rawProduct, String requestHash) {
        return cacheRepository
                .findBySourceAndExternalIdAndContentHashAndProviderAndModelAndPromptVersionAndSchemaVersion(
                        rawProduct.getSource(),
                        rawProduct.getExternalId(),
                        rawProduct.getContentHash(),
                        properties.llm().provider(),
                        properties.llm().model(),
                        properties.llm().promptVersion(),
                        properties.llm().schemaVersion()
                )
                .orElseGet(() -> LlmEnrichmentCache.create(
                        rawProduct.getSource(),
                        rawProduct.getExternalId(),
                        rawProduct.getContentHash(),
                        properties.llm().provider(),
                        properties.llm().model(),
                        properties.llm().promptVersion(),
                        properties.llm().schemaVersion(),
                        requestHash
                ));
    }

    private String prompt(ProductRaw rawProduct, ProductDraft draft) {
        return """
                금융감독원 FSS 금융상품 원문 JSON을 보고 사용자 화면에 필요한 보강값만 추출해라.

                규칙:
                - 응답은 schema에 맞는 JSON만 반환한다.
                - 원문에 명시되지 않은 값은 null 또는 false로 둔다.
                - 금리, 기간, 은행명, 상품명, 상품코드, 신청 URL은 생성하지 않는다.
                - minMonthlyLimit, maxMonthlyLimit은 월 납입액(적금의 월 정기 납입) 전용 필드이다.
                - productType이 SAVING(적금)일 때만, 최소/최대 월 납입액이 명시된 경우 채운다. 없거나 제한 없음이면 null로 둔다.
                - productType이 DEPOSIT(정기예금)이면 minMonthlyLimit, maxMonthlyLimit은 항상 null로 둔다. 일시납 가입금액·가입한도는 여기에 넣지 않는다.
                - keywords에는 기간 키워드(TERM_*)를 넣지 않는다.
                - summaryContent는 마케팅 문구 없이 가입방법, 우대조건, 가입대상, 유의사항을 짧게 정리한다.
                - requiredKeywords에는 가입 가능 여부를 제한하는 STATUS_* 필수/제외 조건만 넣는다.
                - requiredKeywords는 가입대상 문구에 신분 조건이 명시된 경우만 넣는다. 상품명, 은행명, 우대금리 조건, 급여이체 조건, 카드 실적 조건에서 추론하지 않는다.
                - "실명의 개인", "개인", "개인사업자 포함", "개인사업자 제외", "만 N세 이상" 같은 일반 가입 조건은 STATUS_*로 매핑하지 않는다.
                - "직장인", "급여", "급여이체"는 STATUS_SME_WORKER가 아니다.
                - "아이", "우리아이", "자녀", "미성년"은 STATUS_PART_TIME 또는 STATUS_UNEMPLOYED가 아니다.
                - "병역 이행 기간만큼 나이 연장"은 STATUS_MILITARY requiredKeyword가 아니다.
                - requiredKeywords의 confidence가 HIGH가 아닐 정도로 불확실하면 항목을 만들지 말고 빈 배열로 둔다.
                - EXCLUDE는 "가입 불가", "제외", "대상 아님" 같은 배제 표현과 해당 신분이 같은 가입대상 문맥에 명시된 경우만 넣는다.
                - preferentialRates에는 조건별 가산금리가 명시된 경우만 넣는다. 최고/최대 우대금리 총합만 있으면 빈 배열로 둔다.
                - preferentialRates의 keywordCode는 원문의 우대조건 의미와 정확히 일치할 때만 선택한다. 비슷해 보인다는 이유로 끼워맞추지 않는다.
                - 허용되는 preferentialRates 매핑:
                  * BANK_CARD_USAGE: 카드 보유/사용/결제실적/전월결제 조건
                  * BANK_SALARY_TRANSFER: 급여/월급 이체 조건
                  * BANK_AUTO_TRANSFER: 자동이체 조건
                  * BANK_MARKETING: 마케팅/상품서비스/개인정보 수집이용 동의 조건
                  * BANK_FIRST_TRANSACTION: 첫거래/최초거래/신규고객 조건
                  * BANK_REDEPOSIT: 재예치/재가입 조건
                  * BANK_ONLINE_JOIN: 인터넷/모바일/비대면/온라인 가입 조건. 모바일메시지/알림 수신동의는 온라인 가입이 아니다.
                  * BANK_AGE: 나이/연령 조건
                  * BANK_ETC: 위 조건 중 어디에도 정확히 해당하지 않지만 조건별 가산금리가 명시된 우대금리(기타)
                - 위 매핑으로 정확히 표현할 수 없는 우대금리는 BANK_ETC로 매핑한다. (단, 최고/최대 우대금리 총합만 있으면 여전히 제외)
                - 재예치/재가입이라는 단어가 있어도 조건의 핵심이 가입금액, 가입잔액, 요구불평잔, 평균잔액이면 BANK_REDEPOSIT에 매핑하지 않는다.
                - 예: 요구불평잔, 평균잔액, 가입금액, 예금/적금 보유, 특정 상품 만기/해지 고객, 추천/쿠폰/이벤트, 앱 로그인, 알림 수신 등은 억지로 BANK_*에 매핑하지 않는다.
                - FSS 원문에 정부기여금/병역연장/비교제외가 명시되지 않았으면 관련 필드는 null 또는 false로 둔다.
                - earnMaxAmt는 가입자격의 연소득 상한(소득요건)이 원문에 명시된 경우에만 채운다. 가입금액·예치한도·최고한도·월 납입한도 등 금액 한도는 earnMaxAmt에 절대 넣지 않는다.
                - earnPercent는 소득기준(예: 기준중위소득 대비 %%)이 원문에 명시된 경우에만 채운다.
                - 반드시 아래 JSON skeleton의 모든 top-level key를 포함한다. 모르는 값은 null, false, [] 중 schema에 맞는 기본값으로 둔다.

                JSON skeleton:
                {
                  "summaryContent": null,
                  "keywords": [],
                  "minMonthlyLimit": null,
                  "maxMonthlyLimit": null,
                  "minAge": null,
                  "maxAge": null,
                  "earnMaxAmt": null,
                  "earnPercent": null,
                  "requiresHomeless": false,
                  "requiresHouseholder": false,
                  "govContributionRate": null,
                  "govContributionType": null,
                  "govMatchingRatio": null,
                  "govMonthlyFixedContribution": null,
                  "govContributionPeriodMonths": null,
                  "excludeFromRateComparison": false,
                  "allowsMilitaryAgeExtension": false,
                  "militaryMaxAge": null,
                  "requiredKeywords": [],
                  "preferentialRates": []
                }

                현재 정규화 결과:
                productName=%s
                productType=%s
                content=%s

                FSS raw JSON:
                %s
                """.formatted(
                draft.productName(),
                draft.type(),
                draft.content(),
                rawProduct.getRawJson()
        );
    }

    private void validate(LlmProductEnrichment enrichment) {
        if (enrichment == null) {
            throw new IllegalArgumentException("LLM enrichment is null");
        }

        validateAmount(enrichment.minMonthlyLimit(), "minMonthlyLimit");
        validateAmount(enrichment.maxMonthlyLimit(), "maxMonthlyLimit");
        validateAmount(enrichment.earnMaxAmt(), "earnMaxAmt");
        validateRange(enrichment.minAge(), 0, 100, "minAge");
        validateRange(enrichment.maxAge(), 0, 100, "maxAge");
        validateRange(enrichment.earnPercent(), 0, 1000, "earnPercent");
        validateRate(enrichment.govContributionRate(), "govContributionRate");
        validateRate(enrichment.govMatchingRatio(), "govMatchingRatio");
        validateAmount(enrichment.govMonthlyFixedContribution(), "govMonthlyFixedContribution");
        validateRange(enrichment.govContributionPeriodMonths(), 0, 1200, "govContributionPeriodMonths");
        validateRange(enrichment.militaryMaxAge(), 0, 100, "militaryMaxAge");

        if (enrichment.minMonthlyLimit() != null
                && enrichment.maxMonthlyLimit() != null
                && enrichment.maxMonthlyLimit() < enrichment.minMonthlyLimit()) {
            throw new IllegalArgumentException("maxMonthlyLimit is smaller than minMonthlyLimit");
        }
        if (enrichment.minAge() != null && enrichment.maxAge() != null && enrichment.maxAge() < enrichment.minAge()) {
            throw new IllegalArgumentException("maxAge is smaller than minAge");
        }

        // 미지원/TERM_ 키워드는 예외 대신 조용히 무시한다(실제 필터링은 mergeKeywords가 담당).
        // stray 키워드 하나가 상품 enrichment 전체를 실패시키지 않도록 한다.
        // requiredKeywords·preferentialRates 검증은 의미가 있으므로 아래에서 그대로 유지한다.
        for (RequiredKeywordDraft requiredKeyword : enrichment.requiredKeywords()) {
            if (requiredKeyword.keywordCode() == null
                    || !requiredKeyword.keywordCode().name().startsWith("STATUS_")
                    || requiredKeyword.effect() == null
                    || requiredKeyword.confidence() == null) {
                throw new IllegalArgumentException("Unsupported LLM required keyword: " + requiredKeyword);
            }
        }
        for (PreferentialRateDraft preferentialRate : enrichment.preferentialRates()) {
            validateRate(preferentialRate.rate(), "preferentialRate.rate");
            if (!isPreferentialRateKeyword(preferentialRate.keywordCode())) {
                throw new IllegalArgumentException("Unsupported LLM preferential keyword: " + preferentialRate);
            }
            if (preferentialRate.minAge() != null && preferentialRate.maxAge() != null
                    && preferentialRate.maxAge() < preferentialRate.minAge()) {
                throw new IllegalArgumentException("preferentialRate maxAge is smaller than minAge");
            }
            if (!preferentialRate.matchesKeywordCondition()) {
                throw new IllegalArgumentException("Unsupported LLM preferential condition: " + preferentialRate);
            }
        }
    }

    private boolean isPreferentialRateKeyword(KeywordValueEnum keyword) {
        return keyword != null && keyword.name().startsWith("BANK_");
    }

    private boolean containsAny(String value, String... tokens) {
        return TextMatch.containsAny(value, tokens);
    }

    private void validateAmount(Long value, String fieldName) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    private void validateRange(Integer value, int min, int max, String fieldName) {
        if (value != null && (value < min || value > max)) {
            throw new IllegalArgumentException(fieldName + " is out of range");
        }
    }

    private void validateRate(BigDecimal value, String fieldName) {
        if (value != null
                && (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException(fieldName + " is out of range");
        }
    }

    private ProductDraft merge(ProductRaw rawProduct, ProductDraft draft, LlmProductEnrichment enrichment) {
        List<ProductPropertyDraft> properties = new ArrayList<>();
        String eligibilityText = eligibilityText(rawProduct);
        boolean incomeMentioned = mentionsIncome(draft.content(), eligibilityText);
        for (ProductPropertyDraft property : draft.properties()) {
            properties.add(merge(property, enrichment, eligibilityText, draft.type(), incomeMentioned));
        }

        return draft.toBuilder()
                .contentSummary(blankToNull(enrichment.summaryContent()) == null ? draft.contentSummary() : enrichment.summaryContent().trim())
                .properties(properties)
                .build();
    }

    // earnMaxAmt(연소득 상한)·earnPercent(소득기준 %)는 원문에 소득 요건 언급이 있을 때만 LLM 값을 신뢰한다.
    // LLM이 가입금액/예치한도 등을 소득 상한으로 착각해 채우는 오류를 원천 차단하기 위한 보수적 가드.
    // 1단계(sanitize): "소득공제", "소득세", "금융소득종합과세", "소득이체", "소득 이체" 등 소득요건과 무관한 문구를
    //   먼저 제거해 오수용(false positive)을 차단한다 - 이 문구만 있고 실제 소득요건이 없는데 통과하는 것을 방지.
    // 2단계: 남은 텍스트에 "소득", "총급여", "연봉" 중 하나라도 남아 있으면 소득요건 언급으로 인정한다
    //   (오거부 방지 - 총급여/연봉 표현도 소득요건으로 수용).
    private boolean mentionsIncome(String content, String eligibilityText) {
        return mentionsIncomeToken(content) || mentionsIncomeToken(eligibilityText);
    }

    private static final String[] INCOME_IRRELEVANT_PHRASES = {
            "소득공제", "소득세", "금융소득종합과세", "소득이체", "소득 이체"
    };
    private static final String[] INCOME_TOKENS = {"소득", "총급여", "연봉"};

    private boolean mentionsIncomeToken(String value) {
        return containsAny(sanitizeIncomeIrrelevantPhrases(value), INCOME_TOKENS);
    }

    private String sanitizeIncomeIrrelevantPhrases(String value) {
        if (value == null) {
            return null;
        }
        String sanitized = value;
        for (String phrase : INCOME_IRRELEVANT_PHRASES) {
            sanitized = sanitized.replace(phrase, "");
        }
        return sanitized;
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
            if (property.saveTerm() < 24) {
                keywords.add(KeywordValueEnum.TERM_AROUND_1_YEAR);
            }
            else if (property.saveTerm() < 37) {
                keywords.add(KeywordValueEnum.TERM_2_TO_3_YEARS);
            }
            else {
                keywords.add(KeywordValueEnum.TERM_OVER_3_YEARS);
            }
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
            case STATUS_SME_WORKER -> containsAny(
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
            case STATUS_MILITARY -> containsAny(
                    eligibilityText,
                    "군인",
                    "장병",
                    "군 복무",
                    "군복무",
                    "병역복무자",
                    "군 복무자",
                    "군복무자"
            ) && !containsAny(eligibilityText, "병역 이행 기간", "병역이행기간", "나이 연장", "연령 연장");
            case STATUS_UNEMPLOYED -> containsAny(eligibilityText, "무직", "미취업", "구직자", "실업");
            case STATUS_PART_TIME -> containsAny(eligibilityText, "파트타임", "시간제", "단시간 근로", "단시간근로");
            default -> false;
        };
    }

    private boolean hasExcludeExpression(String value) {
        return containsAny(value, "제외", "가입 불가", "가입불가", "대상 아님", "대상아님", "불가능");
    }

    private String eligibilityText(ProductRaw rawProduct) {
        try {
            JsonNode base = objectMapper.readTree(rawProduct.getRawJson()).path("base");
            List<String> parts = new ArrayList<>();
            addIfNotBlank(parts, text(base, "join_member"));
            addIfNotBlank(parts, text(base, "etc_note"));
            return String.join(" ", parts);
        }
        catch (Exception e) {
            log.debug("Failed to parse FSS eligibility text. rawId={}", rawProduct.getId(), e);
            return "";
        }
    }

    private String text(JsonNode node, String fieldName) {
        return JsonNodes.text(node, fieldName);
    }

    private void addIfNotBlank(List<String> values, String value) {
        String normalized = blankToNull(value);
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
        Map<KeywordValueEnum, PreferentialRateDraft> merged = new LinkedHashMap<>();
        // 기타(BANK_ETC)는 keyword당 1건으로 합치지 않고 라인별로 보존. 완전 중복(동일 description)만 최고금리로 정리.
        Map<String, PreferentialRateDraft> etcByDescription = new LinkedHashMap<>();
        for (PreferentialRateDraft draft : existing) {
            keepHighest(merged, etcByDescription, draft);
        }
        for (PreferentialRateDraft draft : enrichment) {
            keepHighest(merged, etcByDescription, draft);
        }

        List<PreferentialRateDraft> result = new ArrayList<>(merged.values());
        result.addAll(etcByDescription.values());
        return List.copyOf(result);
    }

    private void keepHighest(
            Map<KeywordValueEnum, PreferentialRateDraft> merged,
            Map<String, PreferentialRateDraft> etcByDescription,
            PreferentialRateDraft draft
    ) {
        if (draft.keywordCode() == KeywordValueEnum.BANK_ETC) {
            PreferentialRateDraft existing = etcByDescription.get(draft.description());
            if (existing == null || draft.rate().compareTo(existing.rate()) > 0) {
                etcByDescription.put(draft.description(), draft);
            }
            return;
        }
        PreferentialRateDraft existing = merged.get(draft.keywordCode());
        if (existing == null || draft.rate().compareTo(existing.rate()) > 0) {
            merged.put(draft.keywordCode(), draft);
        }
    }

    private <T> T firstNonNull(T existing, T enrichment) {
        return existing != null ? existing : enrichment;
    }

    private boolean firstTrue(Boolean existing, Boolean enrichment) {
        return Boolean.TRUE.equals(existing) || Boolean.TRUE.equals(enrichment);
    }

    private String blankToNull(String value) {
        return JsonNodes.blankToNull(value);
    }

    private String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() <= 500 ? value : value.substring(0, 500);
    }
}
