package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.product.KeywordValueEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TermKeywordsTest {

    @Test
    void bucket_mapsSaveTermToTermKeywordAtBoundaries() {
        assertThat(TermKeywords.bucket(6)).isEqualTo(KeywordValueEnum.TERM_AROUND_1_YEAR);
        assertThat(TermKeywords.bucket(23)).isEqualTo(KeywordValueEnum.TERM_AROUND_1_YEAR);
        assertThat(TermKeywords.bucket(24)).isEqualTo(KeywordValueEnum.TERM_2_TO_3_YEARS);
        assertThat(TermKeywords.bucket(36)).isEqualTo(KeywordValueEnum.TERM_2_TO_3_YEARS);
        assertThat(TermKeywords.bucket(37)).isEqualTo(KeywordValueEnum.TERM_OVER_3_YEARS);
        assertThat(TermKeywords.bucket(60)).isEqualTo(KeywordValueEnum.TERM_OVER_3_YEARS);
    }
}
