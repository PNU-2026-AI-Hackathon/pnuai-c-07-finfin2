package apptive.fin.user.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * 마이페이지(Y5-2) 프로필 조회 응답.
 * - 최상위: 원본 저장값(정보 입력 화면 프리필용 11개).
 * - display: 서버 계산 표시값(마이페이지 표시 6개용). 만 나이·가구소득 금액 가이드·라벨/은행명 해소.
 * 프로필 미저장 시 {@link #notFound()} (hasProfile=false, 나머지 null).
 */
public record UserProfileResponseDto(
        boolean hasProfile,

        // ── 원본 저장값 (프리필용) ──
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
        List<Long> selectedOptionIds,

        // ── 서버 계산 표시값 ──
        Display display
) {
    /** 마이페이지 표시 6개용 서버 계산값. */
    public record Display(
            Integer age,                          // 만 나이 (조회 시점 계산)
            Integer householdIncomeGuide,         // 가구소득 금액 가이드 (만원, householdSize×percent 기준)
            String region,                        // 거주 지역 시도명
            List<String> preferentialConditions,  // 우대조건(선호) 라벨
            TransactionHistory transactionHistory
    ) {
    }

    /** 거래 이력 — 은행명 단위(개수 집계 없이 이름 리스트 그대로). */
    public record TransactionHistory(
            List<String> firstTransactionBanks,   // 첫거래 대상 은행명 (neverUsedBanks)
            List<String> redepositBanks           // 재예치 대상 은행명 (maturedSavingBanks)
    ) {
    }

    public static UserProfileResponseDto notFound() {
        return new UserProfileResponseDto(
                false,
                null, null, null, null, null, null, null, null, null,
                null, null, null,
                null
        );
    }
}
