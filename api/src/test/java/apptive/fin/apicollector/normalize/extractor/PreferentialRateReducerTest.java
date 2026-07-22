package apptive.fin.apicollector.normalize.extractor;

import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PreferentialRateReducerTest {

    private PreferentialRateDraft rate(KeywordValueEnum keyword, String rate, String description) {
        return PreferentialRateDraft.builder()
                .keywordCode(keyword)
                .rate(new BigDecimal(rate))
                .description(description)
                .build();
    }

    @Test
    void reduce_keepsHighestRatePerKeyword() {
        List<PreferentialRateDraft> result = PreferentialRateReducer.reduce(List.of(
                rate(KeywordValueEnum.BANK_CARD_USAGE, "0.3", "카드 30만"),
                rate(KeywordValueEnum.BANK_CARD_USAGE, "0.5", "카드 50만")
        ));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).rate()).isEqualByComparingTo("0.5");
    }

    @Test
    void reduce_keepsBankEtcPerDescriptionAndDedupsSameDescription() {
        List<PreferentialRateDraft> result = PreferentialRateReducer.reduce(List.of(
                rate(KeywordValueEnum.BANK_ETC, "0.2", "첫 거래 고객"),
                rate(KeywordValueEnum.BANK_ETC, "0.1", "앱 로그인"),
                rate(KeywordValueEnum.BANK_ETC, "0.4", "첫 거래 고객")
        ));

        // 서로 다른 description 2건 유지, 동일 description은 최고금리로 정리
        assertThat(result).hasSize(2);
        assertThat(result).extracting(d -> d.description()).containsExactly("첫 거래 고객", "앱 로그인");
        assertThat(result.get(0).rate()).isEqualByComparingTo("0.4");
    }

    @Test
    void reduce_ordersKeywordBestsThenEtcBests_inEncounterOrder() {
        List<PreferentialRateDraft> result = PreferentialRateReducer.reduce(List.of(
                rate(KeywordValueEnum.BANK_ETC, "0.1", "이벤트"),
                rate(KeywordValueEnum.BANK_CARD_USAGE, "0.5", "카드"),
                rate(KeywordValueEnum.BANK_SALARY_TRANSFER, "0.3", "급여")
        ));

        // 키워드 best(등장순: CARD, SALARY) 다음 ETC best
        assertThat(result).extracting(d -> d.keywordCode()).containsExactly(
                KeywordValueEnum.BANK_CARD_USAGE,
                KeywordValueEnum.BANK_SALARY_TRANSFER,
                KeywordValueEnum.BANK_ETC
        );
    }
}
