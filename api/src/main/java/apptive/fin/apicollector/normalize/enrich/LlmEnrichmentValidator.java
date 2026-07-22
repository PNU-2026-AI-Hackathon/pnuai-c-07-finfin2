package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.llm.LlmProductEnrichment;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** LLM enrichment 응답의 값 범위·키워드 유효성을 검증한다. 위반 시 예외를 던진다. */
@Component
public class LlmEnrichmentValidator {

    public void validate(LlmProductEnrichment enrichment) {
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
}
