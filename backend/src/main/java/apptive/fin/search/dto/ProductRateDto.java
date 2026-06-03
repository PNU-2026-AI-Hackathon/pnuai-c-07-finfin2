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
        boolean isSubscription,
        String subscriptionNote // 금리가 없는 청약 상품용 안내 문구
) {
}
