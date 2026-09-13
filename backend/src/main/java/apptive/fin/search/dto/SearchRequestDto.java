package apptive.fin.search.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record SearchRequestDto(
        @NotNull List<@Valid OptionRequestDto> options,
        @NotNull DetailedOptionsDto detailedOptions
) {
    // 상세옵션 널세이프 위임 접근자. detailedOptions가 없으면 null.
    public Integer age(LocalDate today) {
        return detailedOptions != null ? detailedOptions.age(today) : null;
    }

    public Integer age() {
        return detailedOptions != null ? detailedOptions.age() : null;
    }

    public Long monthlySavingsGoal() {
        return detailedOptions != null ? detailedOptions.monthlySavingsGoal() : null;
    }

    public List<String> neverUsedBanks() {
        return detailedOptions != null ? detailedOptions.neverUsedBanks() : null;
    }

    public List<String> maturedSavingBanks() {
        return detailedOptions != null ? detailedOptions.maturedSavingBanks() : null;
    }

    public boolean hasTransactionHistory() {
        return detailedOptions != null
                && detailedOptions.neverUsedBanks() != null
                && detailedOptions.maturedSavingBanks() != null;
    }

    // === 신규 필드 접근자 (PRD 개정) ===

    public Long depositAmount() {
        return detailedOptions != null ? detailedOptions.depositAmount() : null;
    }

    public Integer saveTrmExact() {
        return detailedOptions != null ? detailedOptions.saveTrmExact() : null;
    }

    public boolean isShortTerm() {
        return detailedOptions != null && detailedOptions.isShortTerm();
    }

    public boolean isLongTerm() {
        return detailedOptions != null && detailedOptions.isLongTerm();
    }

    /**
     * 대분류에 따른 유효 금액 반환.
     */
    public Long effectiveAmount() {
        return detailedOptions != null ? detailedOptions.effectiveAmount() : null;
    }

    /**
     * 정규화된 월 납입액 (적금 계산용).
     */
    public Long normalizedMonthlyDeposit() {
        return detailedOptions != null ? detailedOptions.normalizedMonthlyDeposit() : null;
    }

    /**
     * 정규화된 예치 원금 (예금 계산용).
     */
    public Long normalizedDepositPrincipal() {
        return detailedOptions != null ? detailedOptions.normalizedDepositPrincipal() : null;
    }
}
