package apptive.fin.search.dto;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.ProductType;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductDetailResponseDto(
        // 헤더/요약
        Long productId,
        ProductType productType,
        String sourceCode,
        String productName,
        String providerName,
        List<KeywordValueEnum> keywords,
        String content,
        String contentSummary,
        List<Integer> saveTrms,

        // 상품 안내 (선택 property 기준)
        Integer minAge,
        Integer maxAge,
        Long minMonthlyLimit,
        Long maxMonthlyLimit,
        Boolean requiresHomeless,
        Boolean requiresHouseholder,
        String joinMethod,
        String eligibilityText,
        String cautionText,
        String recruitmentPeriod,
        String installmentType,

        // CTA (공개 정보 — 잠금과 무관하게 항상 반환, null = 비활성)
        String applyUrl,

        // 수익 지표 잠금 (비로그인 시 true → 아래 지표/금리표 전부 null)
        boolean metricsLocked,
        String lockMessage,

        // 유형별 (미잠금 시에만)
        GovernmentDetailDto government,
        BankDetailDto bank,
        List<RateTableRowDto> rateTable
) {
}
