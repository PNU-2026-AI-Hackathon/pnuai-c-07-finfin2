package apptive.fin.search.dto;

import lombok.Builder;

@Builder
public record TabAvailabilityDto(
        // 목돈만들기 탭
        boolean tabAEnabled,          // 추천순
        boolean tabBEnabled,          // 금리순
        String tabBDisabledReason,

        // 단기예치 탭
        boolean tabCEnabled,          // 예적금 (실수령액순)
        String tabCDisabledReason,
        boolean tabDEnabled           // 파킹통장 (최고금리순)
) {
}
