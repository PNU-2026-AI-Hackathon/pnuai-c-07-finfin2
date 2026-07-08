package apptive.fin.apicollector.normalize.dto;

import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ContributionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record ProductPropertyDraft(
        String providerCode,
        String providerName,
        String intrRateType,
        String intrRateTypeName,
        String installmentType,
        Integer saveTerm,
        BigDecimal baseRate,
        BigDecimal maxRate,
        BigDecimal govContributionRate,
        ContributionType govContributionType,
        BigDecimal govMatchingRatio,
        Long govMonthlyFixedContribution,
        Integer govContributionPeriodMonths,
        Boolean excludeFromRateComparison,
        Long minMonthlyLimit,
        Long maxMonthlyLimit,
        Integer minAge,
        Integer maxAge,
        Boolean allowsMilitaryAgeExtension,
        Integer militaryMaxAge,
        Long earnMaxAmt,
        Integer earnPercent,
        Integer minTenureMonths,
        Boolean requiresHomeless,
        Boolean requiresHouseholder,
        String applyUrl,
        List<KeywordValueEnum> keywords,
        List<RequiredKeywordDraft> requiredKeywords,
        List<PreferentialRateDraft> preferentialRates
) {
    public ProductPropertyDraft {
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
        requiredKeywords = requiredKeywords == null ? List.of() : List.copyOf(requiredKeywords);
        preferentialRates = preferentialRates == null ? List.of() : List.copyOf(preferentialRates);
        excludeFromRateComparison = excludeFromRateComparison != null && excludeFromRateComparison;
        allowsMilitaryAgeExtension = allowsMilitaryAgeExtension != null && allowsMilitaryAgeExtension;
        requiresHomeless = requiresHomeless != null && requiresHomeless;
        requiresHouseholder = requiresHouseholder != null && requiresHouseholder;
    }
}
