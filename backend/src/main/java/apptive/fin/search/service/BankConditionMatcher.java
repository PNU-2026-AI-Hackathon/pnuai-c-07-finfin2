package apptive.fin.search.service;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.entity.ProductPreferentialRate;
import apptive.fin.search.entity.ProductProperty;

// 은행 우대조건이 "상품 자체에 존재하는지" 판정한다. 사용자 조건과 무관한 상품 고유의 사실.
final class BankConditionMatcher {

    private static final int YOUTH_MIN_AGE = 19;
    private static final int YOUTH_MAX_AGE = 34;

    private BankConditionMatcher() {
    }

    // 상품에 청년 구간과 겹치는 BANK_AGE 우대금리가 있는지
    static boolean hasYouthAgeCondition(ProductProperty property) {
        return property.getPreferentialRates().stream()
                .anyMatch(BankConditionMatcher::matchesYouthRange);
    }

    // 개별 우대금리의 연령 구간이 청년 구간과 겹치는지
    static boolean matchesYouthRange(ProductPreferentialRate rate) {
        if (rate.getKeywordCode() != KeywordValueEnum.BANK_AGE) {
            return false;
        }

        boolean startsBeforeYouthRangeEnds =
                rate.getMinAge() == null || rate.getMinAge() <= YOUTH_MAX_AGE;
        boolean endsAfterYouthRangeStarts =
                rate.getMaxAge() == null || rate.getMaxAge() >= YOUTH_MIN_AGE;
        return startsBeforeYouthRangeEnds && endsAfterYouthRangeStarts;
    }

    // 상품에 비대면가입 우대조건이 있는지.
    // BANK_AGE와 달리 키워드 태그로만 표기된 상품이 있어 태그/우대금리 양쪽을 본다.
}
