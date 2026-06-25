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
        String subscriptionNote
) {
}
