package apptive.fin.apicollector.llm;

import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ContributionType;

import java.math.BigDecimal;
import java.util.List;

public record LlmProductEnrichment(
        String summaryContent,
        List<String> keywords,
        Long minMonthlyLimit,
        Long maxMonthlyLimit,
        Integer minAge,
        Integer maxAge,
        Long earnMaxAmt,
        Integer earnPercent,
        Boolean requiresHomeless,
        Boolean requiresHouseholder,
        BigDecimal govContributionRate,
        ContributionType govContributionType,
        BigDecimal govMatchingRatio,
        Long govMonthlyFixedContribution,
        Integer govContributionPeriodMonths,
        Boolean excludeFromRateComparison,
        Boolean allowsMilitaryAgeExtension,
        Integer militaryMaxAge,
        List<RequiredKeywordDraft> requiredKeywords,
        List<PreferentialRateDraft> preferentialRates
) {
    public LlmProductEnrichment {
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
        requiredKeywords = requiredKeywords == null ? List.of() : List.copyOf(requiredKeywords);
        preferentialRates = preferentialRates == null ? List.of() : List.copyOf(preferentialRates);
        excludeFromRateComparison = excludeFromRateComparison != null && excludeFromRateComparison;
        allowsMilitaryAgeExtension = allowsMilitaryAgeExtension != null && allowsMilitaryAgeExtension;
    }
}
