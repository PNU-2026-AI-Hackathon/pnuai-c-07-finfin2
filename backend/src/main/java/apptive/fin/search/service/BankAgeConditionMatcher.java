package apptive.fin.search.service;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.entity.ProductPreferentialRate;

final class BankAgeConditionMatcher {

    private static final int YOUTH_MIN_AGE = 19;
    private static final int YOUTH_MAX_AGE = 34;

    private BankAgeConditionMatcher() {
    }

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
}
