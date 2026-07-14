package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.extractor.FssPreferentialRateExtractor;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FssPreferentialRateExtractorTest {

    private final FssPreferentialRateExtractor extractor = new FssPreferentialRateExtractor();

    @Test
    void assignsBankEtcWhenNoWhitelistTokenMatches() {
        List<PreferentialRateDraft> result = extractor.extract("○○페이 등록 시 연 0.2%p");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_ETC);
        assertThat(result.get(0).rate()).isEqualByComparingTo(new BigDecimal("0.2"));
    }

    @Test
    void keepsEachUnmatchedLineAsSeparateBankEtc() {
        List<PreferentialRateDraft> result = extractor.extract(
                "○○페이 등록 시 연 0.2%p\n지로 공과금 납부 실적 연 0.1%p"
        );

        assertThat(result)
                .hasSize(2)
                .allMatch(draft -> draft.keywordCode() == KeywordValueEnum.BANK_ETC);
    }

    @Test
    void skipsAggregateLineWhenItemizedLinesExist() {
        List<PreferentialRateDraft> result = extractor.extract(
                "최대우대금리 연 0.5%p\n급여이체 시 연 0.3%p"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_SALARY_TRANSFER);
    }

    @Test
    void skipsParenthesizedAggregateLineWhenItemizedLinesExist() {
        // 부산은행 더(The) 레벨업 정기예금 케이스: "*우대이율(최대 0.90%p)" 총합 라인이 개별 항목과 이중계상되던 누수
        List<PreferentialRateDraft> result = extractor.extract(
                "*우대이율(최대 0.90%p)\n가. 모바일뱅킹 금융정보 및 혜택알림 동의 우대이율 : 0.10%p"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).rate()).isEqualByComparingTo(new BigDecimal("0.10"));
    }

    @Test
    void fallsBackToSingleBankEtcWhenOnlyAggregateExpressionExists() {
        // 케이뱅크 주거래우대 자유적금 케이스: 총합 표현 한 줄뿐이면 필터로 전멸하는 대신 1건 보존
        List<PreferentialRateDraft> result = extractor.extract(
                "급여이체 또는 통신비 자동이체, 체크카드 고객에게 우대금리 제공 (최고 연 0.6%)"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_ETC);
        assertThat(result.get(0).rate()).isEqualByComparingTo(new BigDecimal("0.6"));
    }

    @Test
    void fallbackKeepsHighestRateLine() {
        // 카카오뱅크 한달적금 케이스: 모든 라인이 총합/상한 표현이면 최고 금리 라인 1건만 보존
        List<PreferentialRateDraft> result = extractor.extract(
                "매일/보너스 우대금리 제공 : 최고 연 5.50%p\n"
                        + "① 매일 우대금리 : 매 입금 시 마다 연 0.10%p 제공(최대 연 3.10%p)"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).rate()).isEqualByComparingTo(new BigDecimal("5.50"));
    }

    @Test
    void returnsEmptyWhenNoRateExistsAtAll() {
        // 마이키즈 적금 케이스: 금리 숫자가 아예 없으면 폴백도 만들지 않는다
        List<PreferentialRateDraft> result = extractor.extract(
                "1. 입금실적에 따라 우대금리 적용\n2. 금리쿠폰을 입력시 우대금리 적용"
        );

        assertThat(result).isEmpty();
    }

    @Test
    void keepsModeledKeywordInsteadOfBankEtc() {
        List<PreferentialRateDraft> result = extractor.extract("급여이체 시 연 0.3%p");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_SALARY_TRANSFER);
    }

    @Test
    void preservesLeadingContentNumbers() {
        // KB국민프리미엄적금 케이스: "3년:", "50만원"의 선두 숫자를 불릿으로 오인해 제거하면 안 된다
        List<PreferentialRateDraft> result = extractor.extract("3년: 연 0.9%p, 5년: 연 1.0%p");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).startsWith("3년:");

        List<PreferentialRateDraft> amounts = extractor.extract("50만원 이하: 연 0.5%p, 50만원 초과: 연 1.0%p");

        assertThat(amounts).hasSize(1);
        assertThat(amounts.get(0).description()).startsWith("50만원 이하:");
    }

    @Test
    void stripsNumericBulletsWithDelimiter() {
        List<PreferentialRateDraft> result = extractor.extract("1. 지로 공과금 납부 실적 연 0.1%p");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("지로 공과금 납부 실적 연 0.1%p");
    }

    @Test
    void mergesWrappedSentenceLines() {
        // IBK중기근로자우대적금 케이스: 금리 없는 라인 + 불릿 없이 이어지는 라인은 한 문장으로 병합
        List<PreferentialRateDraft> result = extractor.extract(
                "2. 당행 급여이체 실적(월50만원 이상) 6개월 이상\n   인 경우 : 연 1.0%p"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_SALARY_TRANSFER);
        assertThat(result.get(0).rate()).isEqualByComparingTo(new BigDecimal("1.0"));
        assertThat(result.get(0).description()).contains("급여이체 실적").contains("인 경우 : 연 1.0%p");
    }

    @Test
    void doesNotMergeBulletedLines() {
        List<PreferentialRateDraft> result = extractor.extract(
                "제공조건\n① 급여이체 시 연 0.3%p\n② 자동이체 시 연 0.2%p"
        );

        assertThat(result).hasSize(2);
    }

    @Test
    void preservesLeadingDecimalRate() {
        // 리뷰 지적 #1: "0.5%p …"의 "0."을 숫자 불릿으로 오인해 5%로 만들면 안 된다
        List<PreferentialRateDraft> result = extractor.extract("0.5%p 자동이체 납입 시 제공");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).rate()).isEqualByComparingTo(new BigDecimal("0.5"));
        assertThat(result.get(0).description()).startsWith("0.5%p");

        List<PreferentialRateDraft> one = extractor.extract("1.0%p 지로 납부 실적 우대");

        assertThat(one).hasSize(1);
        assertThat(one.get(0).rate()).isEqualByComparingTo(new BigDecimal("1.0"));
    }

    @Test
    void doesNotMergeWhenContinuationHasOwnKeywordToken() {
        // 리뷰 지적 #2: 무금리 헤더("첫거래 고객")가 다음 항목에 병합되어 없는 우대항목을 날조하면 안 된다
        List<PreferentialRateDraft> result = extractor.extract("첫거래 고객\n급여이체 : 연 0.3%p");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_SALARY_TRANSFER);
    }

    @Test
    void treatsKoreanEnumeratorsAsBullets() {
        // 리뷰 지적 #2: 가./나. 열거 라인은 병합 대상이 아니다
        List<PreferentialRateDraft> result = extractor.extract(
                "가. 지로 요금 납부 조건\n나. 통신비 청구 할인 시 연 0.2%p"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_ETC);
        assertThat(result.get(0).rate()).isEqualByComparingTo(new BigDecimal("0.2"));
        assertThat(result.get(0).description()).doesNotContain("지로 요금");
    }

    @Test
    void keepsItemizedLineContainingPreferentialMaxPhrase() {
        // 리뷰 지적 #3: "…우대금리 최대 0.3%p"는 개별 조건이므로 총합으로 필터하면 안 된다
        List<PreferentialRateDraft> result = extractor.extract(
                "급여이체 실적 충족 시 우대금리 최대 0.3%p\n카드 사용 시 연 0.2%p"
        );

        assertThat(result).hasSize(2);
    }

    @Test
    void filtersMaxLimitAggregateLine() {
        List<PreferentialRateDraft> result = extractor.extract(
                "※ 우대금리 최대한도 : 1.0%p\n급여이체 시 연 0.3%p"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_SALARY_TRANSFER);
    }

    @Test
    void fallbackUsesHighestRateWithinLine() {
        // 리뷰 지적 #5: 폴백은 라인의 첫 금리(0.10)가 아닌 최고 금리(3.10)를 저장한다
        List<PreferentialRateDraft> result = extractor.extract(
                "매 입금 시 마다 연 0.10%p 제공(최대 연 3.10%p)"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_ETC);
        assertThat(result.get(0).rate()).isEqualByComparingTo(new BigDecimal("3.10"));
    }
}
