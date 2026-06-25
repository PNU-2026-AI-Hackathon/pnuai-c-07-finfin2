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
        List<String> neverUsedBanks,
        List<String> maturedSavingBanks,
        List<PreferentialInterestRateOption> selectedInterestRateOptions
) {
    public DetailedOptionsDto(
            LocalDate birthdate,
            Long annualIncome,
            Integer householdSize,
            Integer householdIncomePercent,
            Integer tenureMonths,
            Boolean isFirstJob,
            Boolean isHomeless,
            Boolean isHouseholder,
            Long monthlySavingsGoal,
            List<String> mainBanks,
            List<PreferentialInterestRateOption> selectedInterestRateOptions
    ) {
        this(
                birthdate,
                annualIncome,
                householdSize,
                householdIncomePercent,
                tenureMonths,
                isFirstJob,
                isHomeless,
                isHouseholder,
                monthlySavingsGoal,
                mainBanks,
                null,
                null,
                selectedInterestRateOptions
        );
    }
}
