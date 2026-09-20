package apptive.fin.user.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * 정보 입력(Y3-1) 저장 요청. 11개 입력값 + 선택 옵션 ID 리스트.
 * 정보 입력 화면의 저장(PUT /user/me/profile) 시 전량 덮어쓰기.
 */
public record UserProfileRequestDto(
        LocalDate birthdate,
        Long annualIncome,
        Integer householdSize,
        Integer householdIncomePercent,
        Integer tenureMonths,
        Boolean isFirstJob,
        Boolean isHomeless,
        Boolean isHouseholder,
        Long monthlySavingsGoal,
        List<String> neverUsedBanks,
        List<String> maturedSavingBanks,
        List<Long> selectedOptionIds
) {
}
