package apptive.fin.search.dto;

import java.util.List;

// 은행(FSS) 상세 수익 지표. 잠금 시 응답에서 null.
public record BankDetailDto(
        Double baseRate,        // 기본금리
        Double maxRate,         // 최고금리
        Double achievableRate,  // 내가 받을 수 있는 금리(실질금리)
        List<PreferentialConditionDto> metConditions,   // 충족 우대조건
        List<PreferentialConditionDto> unmetConditions  // 미충족 우대조건
) {
}
