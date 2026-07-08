package apptive.fin.search.dto;

import apptive.fin.search.ContributionType;

// 정부(ONTONG) 상세 수익 지표. 잠금 시 응답에서 null.
public record GovernmentDetailDto(
        Double annualizedYield,          // 기여금 연 환산 수익률(%)
        Long expectedTotalContribution,  // 예상 만기 기여금 총액(원)
        ContributionType contributionType,
        Long effectiveMonthlyDeposit,    // 실효 본인 월납입액(min(희망월납입, 한도))
        Integer contributionPeriodMonths
) {
}
