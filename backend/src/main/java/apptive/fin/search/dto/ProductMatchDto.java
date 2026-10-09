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
        double bankCondScore,

        // 동점 규칙용: 정부=기여금총액, 은행=예상실수령액 (null이면 0으로 처리)
        Long tieBreaker

) {}
