package apptive.fin.search.dto;

import apptive.fin.search.enums.ProductCategoryEnum;
import lombok.Builder;

import java.util.List;

/**
 * 통합 검색 결과 (대분류 라우팅 지원).
 * category 필드로 현재 대분류를 표시하고, 해당 대분류의 결과만 채워짐.
 */
@Builder
public record UnifiedSearchResultDto(
        // 현재 대분류
        ProductCategoryEnum category,

        // 탭 활성화 상태
        TabAvailabilityDto tabs,

        // === 목돈만들기 (LONG_TERM) 결과 ===
        List<ProductMatchDto> governmentRanked,      // 탭A 정부상품 (적합도순)
        List<ProductMatchDto> bankRanked,            // 탭A 은행상품 (적합도순)
        List<ProductRateDto> governmentRateRanked,   // 탭B 정부상품 (실수령액순)
        List<ProductRateDto> bankRateRanked,         // 탭B 은행상품 (실수령액순)
        List<ProductRateDto> subscriptionProducts,   // 청약상품

        // === 단기예치 (SHORT_TERM) 결과 ===
        List<ParkingProductDto> parkingProducts,     // 파킹통장 탭 (최고금리순)
        List<ProductRateDto> depositSavingsProducts, // 예적금 탭 (실수령액순)

        // === 공통 ===
        List<ProductCardSummaryDto> productCardSummaries,
        Long eligibleProductCount,

        // 대분류 전환 시 필요한 금액 입력 여부
        Boolean needsDepositAmount,      // 단기예치 전환 시 예치액 필요
        Boolean needsMonthlySavingsGoal  // 목돈만들기 전환 시 월저축액 필요
) {
    /**
     * 기존 ProductSearchResultDto에서 변환 (목돈만들기 결과).
     */
    public static UnifiedSearchResultDto fromLongTerm(ProductSearchResultDto result) {
        return UnifiedSearchResultDto.builder()
                .category(ProductCategoryEnum.LONG_TERM)
                .tabs(result.tabs())
                .governmentRanked(result.governmentRanked())
                .bankRanked(result.bankRanked())
                .governmentRateRanked(result.governmentRateRanked())
                .bankRateRanked(result.bankRateRanked())
                .subscriptionProducts(result.subscriptionProducts())
                .productCardSummaries(result.productCardSummaries())
                .eligibleProductCount(result.eligibleProductCount())
                .needsDepositAmount(false)
                .needsMonthlySavingsGoal(false)
                .build();
    }

    /**
     * 단기예치 결과 생성.
     */
    public static UnifiedSearchResultDto fromShortTerm(ShortTermResultDto result) {
        return UnifiedSearchResultDto.builder()
                .category(ProductCategoryEnum.SHORT_TERM)
                .tabs(result.tabs())
                .parkingProducts(result.parkingProducts())
                .depositSavingsProducts(result.depositSavingsProducts())
                .productCardSummaries(result.productCardSummaries())
                .eligibleProductCount(result.eligibleProductCount())
                .needsDepositAmount(false)
                .needsMonthlySavingsGoal(false)
                .build();
    }
}
