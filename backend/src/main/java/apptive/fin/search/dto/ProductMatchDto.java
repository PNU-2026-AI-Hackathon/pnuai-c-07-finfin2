package apptive.fin.search.dto;

import lombok.Builder;

@Builder
public record ProductMatchDto(
        Long productId,
        Long productPropertyId,
        String productName,
        String providerName,
        String source, // government 또는 bank
        double totalScore,

        double benefitScore,
        double periodScore,
        double identityScore,
        double depositScore,
        double bankCondScore

) {}
