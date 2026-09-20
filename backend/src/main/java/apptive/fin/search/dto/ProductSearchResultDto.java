package apptive.fin.search.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductSearchResultDto(
        TabAvailabilityDto tabs,
        List<ProductMatchDto> governmentRanked,
        List<ProductMatchDto> bankRanked,
        List<ProductRateDto> governmentRateRanked,
        List<ProductRateDto> bankRateRanked,
        List<ProductRateDto> subscriptionProducts,
        List<ProductCardSummaryDto> productCardSummaries,
        Long eligibleProductCount
) {
}
