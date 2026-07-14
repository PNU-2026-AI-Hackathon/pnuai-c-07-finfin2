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
    void skipsAggregateLineForBankEtc() {
        List<PreferentialRateDraft> result = extractor.extract("최대우대금리 연 0.5%p");

        assertThat(result).isEmpty();
    }

    @Test
    void skipsAggregateVariantsInsteadOfCollectingAsBankEtc() {
        assertThat(extractor.extract("우대이율 최고 연 1.0%")).isEmpty();
        assertThat(extractor.extract("최대 연 1.0%p")).isEmpty();
        assertThat(extractor.extract("우대금리 합계 1.0%p")).isEmpty();
    }

    @Test
    void keepsModeledKeywordInsteadOfBankEtc() {
        List<PreferentialRateDraft> result = extractor.extract("급여이체 시 연 0.3%p");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).keywordCode()).isEqualTo(KeywordValueEnum.BANK_SALARY_TRANSFER);
    }
}
