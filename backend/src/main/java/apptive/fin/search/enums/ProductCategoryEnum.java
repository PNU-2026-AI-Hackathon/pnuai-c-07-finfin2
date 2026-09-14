package apptive.fin.search.enums;

/**
 * 상품 대분류.
 * 저축기간에 따라 결정되며, 각 대분류별로 다른 탭 구조와 정렬 기준을 가짐.
 */
public enum ProductCategoryEnum {
    /**
     * 단기예치 (1개월, 3개월).
     * - 파킹통장 탭: 최고금리순, 비로그인 허용
     * - 예적금 탭: 세후 실수령액순, 로그인 필요
     */
    SHORT_TERM,

    /**
     * 목돈만들기 (6개월 이상).
     * - 탭A: 적합도순, 비로그인 허용
     * - 탭B: 세후 실수령액순, 로그인 필요
     */
    LONG_TERM;

    /**
     * 저축기간(개월)으로 대분류 결정.
     * @param saveTrm 저축기간 (개월)
     * @return 대분류 (null이면 기본값 LONG_TERM)
     */
    public static ProductCategoryEnum fromSaveTrm(Integer saveTrm) {
        if (saveTrm == null) {
            return LONG_TERM; // 기본값
        }
        if (saveTrm == 1 || saveTrm == 3) {
            return SHORT_TERM;
        }
        return LONG_TERM;
    }

    /**
     * KeywordValueEnum으로 대분류 결정.
     */
    public static ProductCategoryEnum fromKeyword(KeywordValueEnum keyword) {
        if (keyword == null) {
            return LONG_TERM;
        }
        if (keyword.isShortTerm()) {
            return SHORT_TERM;
        }
        return LONG_TERM;
    }
}
