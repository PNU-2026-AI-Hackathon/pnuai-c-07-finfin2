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

    /**
     * 저축기간 조회 (키워드에서만 추출).
     * TERM_*_MONTH 키워드가 {1,3,6,12,24,36}만 허용하므로 별도 검증 불필요.
     */
    public Integer getSaveTrm(ResolvedKeywords keywords) {
        if (keywords != null && keywords.savingPeriod() != null) {
            return keywords.savingPeriod().toSaveTrm();
        }
        return null;
    }

    /**
     * 단기예치 여부 (키워드 기반 판정).
     */
    public boolean isShortTerm(ResolvedKeywords keywords) {
        Integer saveTrm = getSaveTrm(keywords);
        return saveTrm != null && (saveTrm == 1 || saveTrm == 3);
    }

    /**
     * 목돈만들기 여부 (키워드 기반 판정).
     */
    public boolean isLongTerm(ResolvedKeywords keywords) {
        Integer saveTrm = getSaveTrm(keywords);
        return saveTrm != null && saveTrm >= 6;
    }

    /**
     * 대분류에 따른 유효 금액 반환.
     */
    public Long effectiveAmount(ResolvedKeywords keywords) {
        if (detailedOptions == null) return null;
        return detailedOptions.effectiveAmount(isShortTerm(keywords));
    }

    /**
     * 정규화된 월 납입액 (적금 계산용).
     */
    public Long normalizedMonthlyDeposit(ResolvedKeywords keywords) {
        if (detailedOptions == null) return null;
        return detailedOptions.normalizedMonthlyDeposit(getSaveTrm(keywords));
    }

    /**
     * 정규화된 예치 원금 (예금 계산용).
     */
    public Long normalizedDepositPrincipal(ResolvedKeywords keywords) {
        if (detailedOptions == null) return null;
        return detailedOptions.normalizedDepositPrincipal(getSaveTrm(keywords));
    }
}
