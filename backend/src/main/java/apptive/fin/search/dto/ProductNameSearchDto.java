package apptive.fin.search.dto;

import lombok.Builder;

@Builder
public record ProductNameSearchDto(
        Long productId,
        String productName,
        String source,
        String providerName,
        Double baseRate,
        Double maxRate
) {
}
