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
    void reduce_dropsAggregateSummaryButKeepsItemizedCondition() {
        List<PreferentialRateDraft> result = PreferentialRateReducer.reduce(List.of(
                rate(KeywordValueEnum.BANK_ETC, "2.5", "우대이율 최대 2.5%"),          // 총합 요약 → 제외
                rate(KeywordValueEnum.BANK_ETC, "1.5", "전라남도 관광지 방문 인증시 : 최고 1.5%p") // 개별조건 → 유지
        ));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).rate()).isEqualByComparingTo("1.5");
    }

    @Test
    void reduce_keepsBankAgePerAgeBracket_notCollapsedToHighest() {
        PreferentialRateDraft youth = PreferentialRateDraft.builder()
                .keywordCode(KeywordValueEnum.BANK_AGE).rate(new BigDecimal("0.3"))
                .description("만 19~34세 우대").minAge(19).maxAge(34).build();
        PreferentialRateDraft senior = PreferentialRateDraft.builder()
                .keywordCode(KeywordValueEnum.BANK_AGE).rate(new BigDecimal("0.5"))
                .description("만 65세 이상 우대").minAge(65).build();

        List<PreferentialRateDraft> result = PreferentialRateReducer.reduce(List.of(youth, senior));

        // 청년(0.3)이 노인(0.5)에 뭉개지지 않고 나이대별로 둘 다 보존
        assertThat(result).hasSize(2);
        assertThat(result).extracting(d -> d.minAge()).containsExactlyInAnyOrder(19, 65);
    }

    @Test
    void reduce_keepsHighestPerSameAgeBracket() {
        PreferentialRateDraft low = PreferentialRateDraft.builder()
                .keywordCode(KeywordValueEnum.BANK_AGE).rate(new BigDecimal("0.2"))
                .description("만 19~34세").minAge(19).maxAge(34).build();
        PreferentialRateDraft high = PreferentialRateDraft.builder()
                .keywordCode(KeywordValueEnum.BANK_AGE).rate(new BigDecimal("0.4"))
                .description("만 19~34세").minAge(19).maxAge(34).build();

        List<PreferentialRateDraft> result = PreferentialRateReducer.reduce(List.of(low, high));

        assertThat(result).hasSize(1);
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
