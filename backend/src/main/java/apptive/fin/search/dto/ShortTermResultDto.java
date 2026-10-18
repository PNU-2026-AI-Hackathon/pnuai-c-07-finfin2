package apptive.fin.search.dto;

import lombok.Builder;

import java.util.List;

/**
 * 단기예치 대분류 검색 결과.
 * - parkingProducts: 파킹통장 탭 (최고금리순, 비로그인 허용)
 * - depositSavingsProducts: 예적금 탭 (세후 실수령액순, 로그인 필요)
 */
@Builder
public record ShortTermResultDto(
        TabAvailabilityDto tabs,
        List<ParkingProductDto> parkingProducts,
        List<ProductRateDto> depositSavingsProducts,
        List<ProductCardSummaryDto> productCardSummaries,
        Long eligibleProductCount
) {
}
