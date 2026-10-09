package apptive.fin.search.dto;

import lombok.Builder;

/**
 * 파킹통장 상품 정보.
 * 최고금리 + 적용 한도 표시, 최고금리순 정렬.
 */
@Builder
public record ParkingProductDto(
        Long productId,
        Long productPropertyId,
        String productName,
        String providerName,
        String providerCode,

        // 금리 정보
        Double baseRate,           // 기본금리
        Double maxRate,            // 최고금리 (우대 포함)
        Long applicableLimit,      // 최고금리 적용 한도 (원)

        // 상품 속성
        String interestPaymentMethod,  // 이자지급방식 (매일, 매월 등)
        Boolean depositProtection,     // 예금자보호 여부

        // 아웃링크
        String applyUrl,
        String officialChannelUrl,
        String officialChannelName
) {
}
