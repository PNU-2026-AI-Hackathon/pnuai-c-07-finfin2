package apptive.fin.apicollector.normalize.extractor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PreferentialRatePhrasesTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "우대이율 최대 2.5%",
            "공통 우대이율 최대 2%",
            "우대이율 6개월 미만 최대2.00%, 6개월 이상 2.20%",
            "12개월제 최대 우대이율",
            "가입자격별 우대이율 최대 0.5%",
            "최대우대금리 연 0.5%p",
            "*우대이율(최대 0.90%p)"
    })
    void detectsAggregateSummaryPhrases(String text) {
        assertThat(PreferentialRatePhrases.isAggregate(text)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "급여이체 실적 충족 시 우대금리 최대 0.3%p",
            "예금가입일~만기일전일까지 당행이선정한 전라남도 관광지 방문 인증시 : 최고 1.5%p",
            "요구불거래:최대0.2%",
            "급여이체 시 연 0.3%p",
            "카드 사용 시 연 0.2%p"
    })
    void keepsItemizedConditions(String text) {
        assertThat(PreferentialRatePhrases.isAggregate(text)).isFalse();
    }

    @Test
    void nullIsNotAggregate() {
        assertThat(PreferentialRatePhrases.isAggregate(null)).isFalse();
    }
}
