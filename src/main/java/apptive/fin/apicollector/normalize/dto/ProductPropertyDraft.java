package apptive.fin.apicollector.normalize.dto;

import apptive.fin.apicollector.product.KeywordValueEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
public record ProductPropertyDraft(
        String providerCode,
        String providerName,
        String intrRateType,
        String intrRateTypeName,
        Integer saveTerm,
        BigDecimal baseRate,
        BigDecimal maxRate,
        BigDecimal govContributionRate,
        Long minMonthlyLimit,
        Long maxMonthlyLimit,
        Integer minAge,
        Integer maxAge,
        Long earnMaxAmt,
        Integer earnPercent,
        Integer minTenureMonths,
        Boolean requiresHomeless,
        Boolean requiresHouseholder,
        String applyUrl,
        List<KeywordValueEnum> keywords
) {
    public ProductPropertyDraft {
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
        requiresHomeless = requiresHomeless != null && requiresHomeless;
        requiresHouseholder = requiresHouseholder != null && requiresHouseholder;
    }
}
