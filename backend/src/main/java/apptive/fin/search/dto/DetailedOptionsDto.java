package apptive.fin.search.dto;

import apptive.fin.global.util.AgeUtil;

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
        Long monthlySavingsGoal,       // 월 저축 가능액 (목돈만들기용, 단위: 원)
        Long depositAmount,            // 예치 희망액 (단기예치용, 단위: 원)
        List<String> neverUsedBanks,
        List<String> maturedSavingBanks,
        List<PreferentialInterestRateOption> selectedInterestRateOptions
) {
    // 레거시 호환용 생성자 (거래이력 없음)
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
                null,  // depositAmount
                null,  // neverUsedBanks
                null,  // maturedSavingBanks
                selectedInterestRateOptions
        );
    }

    // 레거시 호환용 생성자 (거래이력 포함, 신규 필드 없음)
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
            List<String> neverUsedBanks,
            List<String> maturedSavingBanks,
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
                null,  // depositAmount
                neverUsedBanks,
                maturedSavingBanks,
                selectedInterestRateOptions
        );
    }

    // 기준일(today) 시점의 만 나이. 생일 미입력 시 null.
    public Integer age(LocalDate today) {
        return AgeUtil.age(birthdate, today);
    }

    public Integer age() {
        return AgeUtil.age(birthdate);
    }

    /**
     * 대분류에 따른 유효 금액 반환.
     * @param isShortTerm 단기예치 여부 (키워드 기반으로 판정)
     * @return 단기예치면 depositAmount, 목돈만들기면 monthlySavingsGoal
     */
    public Long effectiveAmount(boolean isShortTerm) {
        if (isShortTerm) {
            return depositAmount;
        }
        return monthlySavingsGoal;
    }

    /**
     * 정규화된 월 납입액 계산.
     * @param saveTrm 저축기간 (키워드에서 추출)
     * @return 단기예치(1,3개월)면 예치액÷기간, 목돈만들기면 monthlySavingsGoal
     */
    public Long normalizedMonthlyDeposit(Integer saveTrm) {
        boolean isShortTerm = saveTrm != null && (saveTrm == 1 || saveTrm == 3);
        if (isShortTerm && depositAmount != null && saveTrm > 0) {
            return depositAmount / saveTrm;
        }
        return monthlySavingsGoal;
    }

    /**
     * 정규화된 예치 원금 계산.
     * @param saveTrm 저축기간 (키워드에서 추출)
     * @return 목돈만들기(6개월 이상)면 월저축액×기간, 단기예치면 depositAmount
     */
    public Long normalizedDepositPrincipal(Integer saveTrm) {
        boolean isLongTerm = saveTrm != null && saveTrm >= 6;
        if (isLongTerm && monthlySavingsGoal != null) {
            return monthlySavingsGoal * saveTrm;
        }
        return depositAmount;
    }
}
