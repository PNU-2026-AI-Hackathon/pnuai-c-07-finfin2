package apptive.fin.apicollector.normalize;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MonthlyLimitExtractorTest {

    private final MonthlyLimitExtractor extractor = new MonthlyLimitExtractor();

    @Test
    void extractsMonthlyAmountFirst() {
        Long result = extractor.extract(
                "지역 청년통장",
                "매월 본인 납입 10만원, 최대 지원 30만원"
        );

        assertThat(result).isEqualTo(100_000L);
    }

    @Test
    void extractsLimitAmountWhenMonthlyAmountIsMissing() {
        Long result = extractor.extract(
                "청년 금융상품",
                "가입 한도: 500,000원까지 납입 가능"
        );

        assertThat(result).isEqualTo(500_000L);
    }

    @Test
    void extractsMatchingAmountWhenMonthlyAndLimitAreMissing() {
        Long result = extractor.extract(
                "청년 금융상품",
                "본인 저축액에 10만원 매칭 지원"
        );

        assertThat(result).isEqualTo(100_000L);
    }

    @Test
    void usesManualLimitTableWhenTextDoesNotMatch() {
        Long result = extractor.extract("청년주택드림 청약통장", "자유롭게 납입할 수 있습니다.");

        assertThat(result).isEqualTo(1_000_000L);
    }

    @Test
    void returnsNullForFreeDepositProduct() {
        Long result = extractor.extract("장병내일준비적금", "복무기간 중 자유 납입");

        assertThat(result).isNull();
    }
}
