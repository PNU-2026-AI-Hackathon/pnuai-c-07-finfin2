package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.product.KeywordValueEnum;

/** 가입 기간(개월)을 TERM_* 키워드 구간으로 매핑하는 단일 소스. */
public final class TermKeywords {

    private TermKeywords() {
    }

    public static KeywordValueEnum bucket(int saveTermMonths) {
        if (saveTermMonths < 24) {
            return KeywordValueEnum.TERM_AROUND_1_YEAR;
        }
        if (saveTermMonths < 37) {
            return KeywordValueEnum.TERM_2_TO_3_YEARS;
        }
        return KeywordValueEnum.TERM_OVER_3_YEARS;
    }
}
