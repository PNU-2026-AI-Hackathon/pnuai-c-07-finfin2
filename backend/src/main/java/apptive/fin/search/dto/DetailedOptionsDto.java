package apptive.fin.search.dto;

import java.time.LocalDate;
import java.time.Period;
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

    // 기준일(today) 시점의 만 나이. 생일 미입력 시 null.
    public Integer age(LocalDate today) {
        return birthdate != null ? Period.between(birthdate, today).getYears() : null;
    }
}
