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
        String productType        // 상품유형 (DEPOSIT/SAVING)
) {
    // 레거시 호환용 빌더 패턴 지원을 위한 기본값 처리
    public ProductRateDto {
        if (netReturn == null) netReturn = null;
        if (principal == null) principal = null;
        if (saveTrm == null) saveTrm = null;
        if (productType == null) productType = null;
    }
}
