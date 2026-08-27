package apptive.fin.search.dto;

import java.util.List;

// 기본/최고금리는 공개 정보. 개인화 잠금 시 달성 가능 금리는 null이고 조건 목록은 비어 있다.
public record BankDetailDto(
        Double baseRate,        // 기본금리
        Double maxRate,         // 최고금리
        Double achievableRate,  // 내가 받을 수 있는 금리(실질금리)
        List<PreferentialConditionDto> metConditions,   // 충족 우대조건
        List<PreferentialConditionDto> unmetConditions  // 미충족 우대조건
) {
}
