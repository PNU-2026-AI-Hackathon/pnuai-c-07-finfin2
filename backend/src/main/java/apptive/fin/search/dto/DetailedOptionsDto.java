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
        Long monthlySavingsGoal,       // 월 저축 가능액 (목돈만들기용, 단위: 만원)
        Long depositAmount,            // 예치 희망액 (단기예치용, 단위: 만원)
        Integer saveTrmExact,          // 정확한 저축기간 (1, 3, 6, 12, 24, 36 개월)
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
                null,  // saveTrmExact
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
                null,  // saveTrmExact
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
     * 단기예치 대분류인지 판별 (saveTrmExact가 1 또는 3개월).
     */
    public boolean isShortTerm() {
        return saveTrmExact != null && (saveTrmExact == 1 || saveTrmExact == 3);
    }

    /**
     * 목돈만들기 대분류인지 판별 (saveTrmExact가 6개월 이상).
     */
    public boolean isLongTerm() {
        return saveTrmExact != null && saveTrmExact >= 6;
    }

    /**
     * 대분류에 따른 유효 금액 반환.
     * - 단기예치: depositAmount (예치 희망액)
     * - 목돈만들기: monthlySavingsGoal (월 저축 가능액)
     * - 미지정: monthlySavingsGoal (레거시 호환)
     */
    public Long effectiveAmount() {
        if (isShortTerm()) {
            return depositAmount;
        }
        return monthlySavingsGoal;
    }

    /**
     * 정규화된 월 납입액 계산.
     * - 단기예치: 예치액 ÷ 기간 (적금용)
     * - 목돈만들기: monthlySavingsGoal 그대로
     */
    public Long normalizedMonthlyDeposit() {
        if (isShortTerm() && depositAmount != null && saveTrmExact != null && saveTrmExact > 0) {
            return depositAmount / saveTrmExact;
        }
        return monthlySavingsGoal;
    }

    /**
     * 정규화된 예치 원금 계산.
     * - 단기예치: depositAmount 그대로
     * - 목돈만들기: 월저축액 × 기간 (예금용)
     */
    public Long normalizedDepositPrincipal() {
        if (isLongTerm() && monthlySavingsGoal != null && saveTrmExact != null) {
            return monthlySavingsGoal * saveTrmExact;
        }
        return depositAmount;
    }
}
