package apptive.fin.search.dto;

import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductApplyStatus;
import apptive.fin.search.enums.ProductType;
import apptive.fin.search.enums.ReserveType;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductDetailResponseDto(
        // 헤더/요약
        Long productId,
        // 이 상세가 어느 옵션(property) 기준인지. 추천 응답의 리스트 카드와 상세를 짝지을 때 쓴다.
        Long productPropertyId,
        ProductType productType,
        String sourceCode,
        String productName,
        String providerName,
        List<KeywordValueEnum> keywords,
        String content,
        String contentSummary,
        List<Integer> saveTrms,

        // 적합도(리스트 탭A totalScore와 동일 스케일 0~100). 잠금과 무관, property/옵션 없으면 null
        Double matchScore,

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
        ReserveType reserveType,
        String reserveTypeName,

        // CTA (공개 정보 — 잠금과 무관하게 항상 반환)
        ProductApplyStatus applyStatus,
        // 상품 자체 신청 URL. 직접 신청 링크가 없으면 null이고 아래 officialChannelUrl로 대체 안내. applyUrl과 상호배타.
        String applyUrl,
        // applyUrl이 없을 때 안내할 기관 공식 채널 URL. 버튼 문구용 기관명은 위 providerName을 그대로 쓴다
        // (상세에선 채널명 = providerName이라 별도 필드를 두지 않음. 정부 채널명 구분은 실제 데이터 생기면 도입).
        String officialChannelUrl,

        // 수익 지표 잠금 (property 미지정 또는 로그인/1·2단계 필수정보 미완료 시 true)
        boolean metricsLocked,
        String lockMessage,

        // 정부 개인화 지표는 미잠금 시에만, 은행 기본/최고금리와 금리표는 공개
        GovernmentDetailDto government,
        BankDetailDto bank,
        List<RateTableRowDto> rateTable
) {
}
