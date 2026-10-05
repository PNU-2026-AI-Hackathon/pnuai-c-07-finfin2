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

    /**
     * 통합 저축기간 조회 (키워드 우선, saveTrmExact fallback).
     * 키워드와 saveTrmExact를 일원화하여 불일치 문제 방지.
     */
    public Integer getSaveTrm(ResolvedKeywords keywords) {
        // 1순위: 키워드에서 추출 (단일 기준)
        if (keywords != null && keywords.savingPeriod() != null) {
            return keywords.savingPeriod().toSaveTrm();
        }
        // 2순위: saveTrmExact (레거시 호환)
        return saveTrmExact();
    }

    /**
     * 단기예치 여부 (키워드 기반 통합 판정).
     */
    public boolean isShortTerm(ResolvedKeywords keywords) {
        Integer saveTrm = getSaveTrm(keywords);
        return saveTrm != null && (saveTrm == 1 || saveTrm == 3);
    }

    /**
     * 단기예치 여부 (saveTrmExact만 사용, 레거시 호환).
     */
    public boolean isShortTerm() {
        return detailedOptions != null && detailedOptions.isShortTerm();
    }

    /**
     * 목돈만들기 여부 (키워드 기반 통합 판정).
     */
    public boolean isLongTerm(ResolvedKeywords keywords) {
        Integer saveTrm = getSaveTrm(keywords);
        return saveTrm != null && saveTrm >= 6;
    }

    /**
     * 목돈만들기 여부 (saveTrmExact만 사용, 레거시 호환).
     */
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
