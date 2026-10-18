package apptive.fin.search.dto;

import lombok.Builder;

@Builder
public record ProductRateDto(
        Long productId,
        Long productPropertyId,
        String productName,
        String providerName,
        String source,
        double baseRate,
        double achievableRate,
        boolean rateComparable,
        boolean isSubscription,
        String subscriptionNote,

        // 세후 실수령액 관련 (PRD 개정)
        Long netReturn,           // 세후 실수령액 (원)
        Long principal,           // 원금 (원)
        Integer saveTrm,          // 저축기간 (개월)
        String productType,       // 상품유형 (DEPOSIT/SAVING)

        // 정부상품 기여금 (탭A 동점/탭B 정렬용)
        Long expectedTotalContribution  // 예상 만기 기여금 총액 (원), 은행상품은 null
) {
}
