package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.SearchErrorCode;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.enums.ProductCategoryEnum;
import org.springframework.stereotype.Component;

@Component
public class SearchRequestPolicy {

    // ===== 목돈만들기 (LONG_TERM) 정책 =====

    public void validateForRecommendation(SearchRequestDto request, ResolvedKeywords keywords) {
        if (request == null || request.monthlySavingsGoal() == null) {
            throw new BusinessException(SearchErrorCode.MONTHLY_SAVINGS_GOAL_REQUIRED);
        }
        if (keywords == null || keywords.savingPeriod() == null) {
            throw new BusinessException(SearchErrorCode.SAVING_PERIOD_REQUIRED);
        }
        if (keywords.bankConditions() == null || keywords.bankConditions().isEmpty()) {
            throw new BusinessException(SearchErrorCode.BANK_CONDITION_REQUIRED);
        }
    }

    /** 거래 이력은 빈 목록이면 "없음"으로 응답한 것이고, null이면 미입력한 것으로 본다. */
    public boolean canUsePersonalization(
            SearchRequestDto request,
            ResolvedKeywords keywords,
            AuthUserDetails userDetails
    ) {
        if (userDetails == null
                || !userDetails.getRole().canUseRecommendation()
                || !isStep1Complete(request, keywords)) {
            return false;
        }

        DetailedOptionsDto detail = request.detailedOptions();
        return detail != null
                && detail.birthdate() != null
                && detail.annualIncome() != null
                && detail.householdSize() != null
                && detail.householdIncomePercent() != null
                && detail.neverUsedBanks() != null
                && detail.maturedSavingBanks() != null;
    }

    private boolean isStep1Complete(SearchRequestDto request, ResolvedKeywords keywords) {
        return request != null
                && request.monthlySavingsGoal() != null
                && keywords != null
                && keywords.savingPeriod() != null
                && keywords.bankConditions() != null
                && !keywords.bankConditions().isEmpty();
    }

    // ===== 단기예치 (SHORT_TERM) 정책 =====

    /**
     * 단기예치 검색 요청 검증.
     * - 예치액(depositAmount) 필수
     * - 저축기간(saveTrmExact) 필수
     * - 은행조건 선택 불필요 (파킹통장 탭은 비로그인 허용)
     */
    public void validateForShortTerm(SearchRequestDto request) {
        if (request == null) {
            throw new BusinessException(SearchErrorCode.INVALID_REQUEST);
        }
        if (request.depositAmount() == null || request.depositAmount() <= 0) {
            throw new BusinessException(SearchErrorCode.DEPOSIT_AMOUNT_REQUIRED);
        }
        if (request.saveTrmExact() == null) {
            throw new BusinessException(SearchErrorCode.SAVING_PERIOD_REQUIRED);
        }
    }

    /**
     * 단기예치 예적금 탭(tabB) 활성화 여부.
     * - 로그인 필수
     * - 예치액 입력 필수
     * - 상세 정보(생년월일 등) 입력 필수
     */
    public boolean canUseShortTermPersonalization(
            SearchRequestDto request,
            AuthUserDetails userDetails
    ) {
        // 비로그인이면 불가
        if (userDetails == null || !userDetails.getRole().canUseRecommendation()) {
            return false;
        }

        // 예치액 입력 확인
        if (request == null || request.depositAmount() == null || request.depositAmount() <= 0) {
            return false;
        }

        // 저축기간 입력 확인
        if (request.saveTrmExact() == null) {
            return false;
        }

        // 상세 정보 입력 확인 (세후 실수령액 계산에 필요)
        DetailedOptionsDto detail = request.detailedOptions();
        return detail != null
                && detail.birthdate() != null;
    }

    // ===== 통합 검색 정책 =====

    /**
     * 대분류별 검색 요청 검증.
     */
    public void validateForCategory(SearchRequestDto request, ResolvedKeywords keywords, ProductCategoryEnum category) {
        if (category == ProductCategoryEnum.SHORT_TERM) {
            validateForShortTerm(request);
        } else {
            validateForRecommendation(request, keywords);
        }
    }

    /**
     * 대분류별 개인화(tabB) 활성화 여부.
     */
    public boolean canUsePersonalization(
            SearchRequestDto request,
            ResolvedKeywords keywords,
            AuthUserDetails userDetails,
            ProductCategoryEnum category
    ) {
        if (category == ProductCategoryEnum.SHORT_TERM) {
            return canUseShortTermPersonalization(request, userDetails);
        } else {
            return canUsePersonalization(request, keywords, userDetails);
        }
    }
}
