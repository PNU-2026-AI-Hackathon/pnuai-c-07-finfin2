package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.SearchErrorCode;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.enums.ProductCategoryEnum;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SearchRequestPolicy {

    /** 명세상 허용되는 저축기간 (개월) */
    private static final Set<Integer> VALID_SAVE_TERMS = Set.of(1, 3, 6, 12, 24, 36);

    // ===== 목돈만들기 (LONG_TERM) 정책 =====

    public void validateForRecommendation(SearchRequestDto request, ResolvedKeywords keywords) {
        if (request == null || request.monthlySavingsGoal() == null) {
            throw new BusinessException(SearchErrorCode.MONTHLY_SAVINGS_GOAL_REQUIRED);
        }
        // 저축기간 검증 (키워드에서만 추출)
        validateSaveTrm(request.getSaveTrm(keywords));
        if (keywords == null || keywords.bankConditions() == null || keywords.bankConditions().isEmpty()) {
            throw new BusinessException(SearchErrorCode.BANK_CONDITION_REQUIRED);
        }
    }

    /**
     * 저축기간 유효값 검증.
     * 허용값: {1, 3, 6, 12, 24, 36}개월
     */
    private void validateSaveTrm(Integer saveTrm) {
        if (saveTrm == null) {
            throw new BusinessException(SearchErrorCode.SAVING_PERIOD_REQUIRED);
        }
        if (!VALID_SAVE_TERMS.contains(saveTrm)) {
            throw new BusinessException(SearchErrorCode.INVALID_SAVING_PERIOD);
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
                && request.getSaveTrm(keywords) != null  // 저축기간 검증
                && keywords != null
                && keywords.bankConditions() != null
                && !keywords.bankConditions().isEmpty();
    }

    // ===== 단기예치 (SHORT_TERM) 정책 =====

    /**
     * 단기예치 검색 요청 검증.
     * - 예치액(depositAmount) 필수
     * - 저축기간 필수 (키워드에서 추출)
     * - 은행조건 선택 불필요 (파킹통장 탭은 비로그인 허용)
     */
    public void validateForShortTerm(SearchRequestDto request, ResolvedKeywords keywords) {
        if (request == null) {
            throw new BusinessException(SearchErrorCode.INVALID_REQUEST);
        }
        if (request.depositAmount() == null || request.depositAmount() <= 0) {
            throw new BusinessException(SearchErrorCode.DEPOSIT_AMOUNT_REQUIRED);
        }
        // 저축기간 검증 (키워드에서만 추출)
        validateSaveTrm(request.getSaveTrm(keywords));
    }

    /**
     * 단기예치 예적금 탭(tabC) 활성화 여부.
     * 명세: 우대조건 + 거래이력 + 생년월일 입력 시 활성화
     */
    public boolean canUseShortTermPersonalization(
            SearchRequestDto request,
            ResolvedKeywords keywords,
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

        // 저축기간 입력 확인 (키워드에서 추출)
        if (request.getSaveTrm(keywords) == null) {
            return false;
        }

        // 우대조건 입력 확인 (bankConditions)
        if (keywords == null || keywords.bankConditions() == null || keywords.bankConditions().isEmpty()) {
            return false;
        }

        // 거래이력 입력 확인 (neverUsedBanks, maturedSavingBanks)
        if (!request.hasTransactionHistory()) {
            return false;
        }

        // 생년월일 입력 확인
        DetailedOptionsDto detail = request.detailedOptions();
        return detail != null && detail.birthdate() != null;
    }

    // ===== 통합 검색 정책 =====

    /**
     * 대분류별 검색 요청 검증.
     */
    public void validateForCategory(SearchRequestDto request, ResolvedKeywords keywords, ProductCategoryEnum category) {
        if (category == ProductCategoryEnum.SHORT_TERM) {
            validateForShortTerm(request, keywords);
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
            return canUseShortTermPersonalization(request, keywords, userDetails);
        } else {
            return canUsePersonalization(request, keywords, userDetails);
        }
    }
}
