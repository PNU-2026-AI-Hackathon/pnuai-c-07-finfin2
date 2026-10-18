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
        Long eligibleProductCount,

        // TOP3 균등 배점 (PRD: 3축 33/33/34)
        List<ProductMatchDto> governmentTop3,
        List<ProductMatchDto> bankTop3
) {
}
