package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.llm.*;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FssLlmProductDraftEnricher implements ProductDraftEnricher {

    private final CollectorProperties properties;
    private final List<LlmProviderClient> providerClients;
    private final LlmEnrichmentCacheRepository cacheRepository;
    private final ObjectMapper objectMapper;

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
        String requestHash = sha256(prompt);
        LlmEnrichmentCache cache = cache(rawProduct, requestHash);

        if (cache.getStatus() == LlmEnrichmentCacheStatus.SUCCESS && cache.getResponseJson() != null) {
            return fromCache(cache, draft);
        }

        try {
            LlmProductEnrichment enrichment = providerClient.enrich(new LlmProductEnrichmentRequest(
                    properties.llm().model(),
                    prompt,
                    properties.llm().schemaVersion()
            ));
            validate(enrichment);

            cache.markSuccess(requestHash, objectMapper.writeValueAsString(enrichment));
            cacheRepository.save(cache);
            return merge(draft, enrichment);
        }
        catch (Exception e) {
            log.warn("FSS LLM enrichment failed. rawId={}, externalId={}", rawProduct.getId(), rawProduct.getExternalId(), e);
            cache.markFailed(requestHash, truncate(e.getMessage()));
            cacheRepository.save(cache);
            return draft;
        }
    }

    private ProductDraft fromCache(LlmEnrichmentCache cache, ProductDraft draft) {
        try {
            LlmProductEnrichment enrichment = objectMapper.readValue(cache.getResponseJson(), LlmProductEnrichment.class);
            validate(enrichment);
            return merge(draft, enrichment);
        }
        catch (Exception e) {
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
                - 제한 없음, 한도 없음은 maxMonthlyLimit=null로 둔다.
                - minMonthlyLimit은 최소 가입금액 또는 최소 월 납입액이 명시된 경우만 채운다.
                - maxMonthlyLimit은 유한한 최대 가입한도 또는 월 납입한도가 명시된 경우만 채운다.
                - keywords에는 기간 키워드(TERM_*)를 넣지 않는다.
                - summaryContent는 마케팅 문구 없이 가입방법, 우대조건, 가입대상, 유의사항을 짧게 정리한다.
                - requiredKeywords에는 가입 가능 여부를 제한하는 STATUS_* 필수/제외 조건만 넣는다.
                - preferentialRates에는 조건별 가산금리가 명시된 경우만 넣는다. 최고/최대 우대금리 총합만 있으면 빈 배열로 둔다.
                - FSS 원문에 정부기여금/병역연장/비교제외가 명시되지 않았으면 관련 필드는 null 또는 false로 둔다.

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

        for (String keyword : enrichment.keywords()) {
            KeywordValueEnum keywordValue = KeywordValueEnum.from(keyword);
            if (keywordValue == null || keywordValue.name().startsWith("TERM_")) {
                throw new IllegalArgumentException("Unsupported LLM keyword: " + keyword);
            }
        }
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
            if (preferentialRate.keywordCode() == null || preferentialRate.keywordCode().name().startsWith("TERM_")) {
                throw new IllegalArgumentException("Unsupported LLM preferential keyword: " + preferentialRate);
            }
            if (preferentialRate.minAge() != null && preferentialRate.maxAge() != null
                    && preferentialRate.maxAge() < preferentialRate.minAge()) {
                throw new IllegalArgumentException("preferentialRate maxAge is smaller than minAge");
            }
        }
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

    private ProductDraft merge(ProductDraft draft, LlmProductEnrichment enrichment) {
        List<ProductPropertyDraft> properties = new ArrayList<>();
        for (ProductPropertyDraft property : draft.properties()) {
            properties.add(merge(property, enrichment));
        }

        return draft.toBuilder()
                .contentSummary(blankToNull(enrichment.summaryContent()) == null ? draft.contentSummary() : enrichment.summaryContent().trim())
                .properties(properties)
                .build();
    }

    private ProductPropertyDraft merge(ProductPropertyDraft property, LlmProductEnrichment enrichment) {
        return property.toBuilder()
                .minMonthlyLimit(firstNonNull(property.minMonthlyLimit(), enrichment.minMonthlyLimit()))
                .maxMonthlyLimit(firstNonNull(property.maxMonthlyLimit(), enrichment.maxMonthlyLimit()))
                .minAge(enrichment.minAge())
                .maxAge(enrichment.maxAge())
                .earnMaxAmt(enrichment.earnMaxAmt())
                .earnPercent(enrichment.earnPercent())
                .govContributionRate(enrichment.govContributionRate())
                .govContributionType(enrichment.govContributionType())
                .govMatchingRatio(enrichment.govMatchingRatio())
                .govMonthlyFixedContribution(enrichment.govMonthlyFixedContribution())
                .govContributionPeriodMonths(enrichment.govContributionPeriodMonths())
                .excludeFromRateComparison(enrichment.excludeFromRateComparison())
                .allowsMilitaryAgeExtension(enrichment.allowsMilitaryAgeExtension())
                .militaryMaxAge(enrichment.militaryMaxAge())
                .requiresHomeless(Boolean.TRUE.equals(enrichment.requiresHomeless()))
                .requiresHouseholder(Boolean.TRUE.equals(enrichment.requiresHouseholder()))
                .keywords(mergeKeywords(property, enrichment))
                .requiredKeywords(mergeRequiredKeywords(property.requiredKeywords(), enrichment.requiredKeywords()))
                .preferentialRates(mergePreferentialRates(property.preferentialRates(), enrichment.preferentialRates()))
                .build();
    }

    private List<KeywordValueEnum> mergeKeywords(ProductPropertyDraft property, LlmProductEnrichment enrichment) {
        Set<KeywordValueEnum> keywords = EnumSet.noneOf(KeywordValueEnum.class);
        for (String keyword : enrichment.keywords()) {
            KeywordValueEnum keywordValue = KeywordValueEnum.from(keyword);
            if (keywordValue != null && !keywordValue.name().startsWith("TERM_")) {
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

    private String requiredKeywordKey(RequiredKeywordDraft draft) {
        return draft.keywordCode() + ":" + draft.effect();
    }

    private List<PreferentialRateDraft> mergePreferentialRates(
            List<PreferentialRateDraft> existing,
            List<PreferentialRateDraft> enrichment
    ) {
        Map<KeywordValueEnum, PreferentialRateDraft> merged = new LinkedHashMap<>();
        for (PreferentialRateDraft draft : existing) {
            keepHighest(merged, draft);
        }
        for (PreferentialRateDraft draft : enrichment) {
            keepHighest(merged, draft);
        }
        return List.copyOf(merged.values());
    }

    private void keepHighest(Map<KeywordValueEnum, PreferentialRateDraft> merged, PreferentialRateDraft draft) {
        PreferentialRateDraft existing = merged.get(draft.keywordCode());
        if (existing == null || draft.rate().compareTo(existing.rate()) > 0) {
            merged.put(draft.keywordCode(), draft);
        }
    }

    private <T> T firstNonNull(T existing, T enrichment) {
        return existing != null ? existing : enrichment;
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to calculate request hash", e);
        }
    }

    private String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() <= 500 ? value : value.substring(0, 500);
    }
}
