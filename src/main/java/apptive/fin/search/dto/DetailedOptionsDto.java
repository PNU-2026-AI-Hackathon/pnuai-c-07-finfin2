package apptive.fin.search.dto;

import java.time.LocalDate;
import java.util.List;

public record DetailedOptionsDto(
        LocalDate birthdate,
        Long annualIncome,
        Integer householdSize,
        Integer householdIncomePercent,
        Integer tenureMonths,
        Boolean isFirstJob,
        Boolean isHomeless,
        Boolean isHouseholder, // 세대주 여부
        Long monthlySavingsGoal,
        List<String> mainBanks,
        List<PreferentialInterestRateOption> selectedInterestRateOptions
) {
}
