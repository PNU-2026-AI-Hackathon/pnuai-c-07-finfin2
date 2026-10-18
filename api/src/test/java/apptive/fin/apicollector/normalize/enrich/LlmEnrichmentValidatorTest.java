package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.llm.LlmProductEnrichment;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LlmEnrichmentValidatorTest {

    private final LlmEnrichmentValidator validator = new LlmEnrichmentValidator();

    private LlmProductEnrichment enrichment(
            Integer minAge,
            Integer maxAge,
            List<RequiredKeywordDraft> requiredKeywords,
            List<PreferentialRateDraft> preferentialRates
    ) {
        return new LlmProductEnrichment(
                null, List.of(), null, null, minAge, maxAge, null, null, false, false,
                null, null, null, null, null, false, false, null, requiredKeywords, preferentialRates);
    }

    private PreferentialRateDraft preferential(KeywordValueEnum keyword, String rate, String description) {
        return PreferentialRateDraft.builder()
                .keywordCode(keyword)
                .rate(new BigDecimal(rate))
                .description(description)
                .build();
    }

    @Test
    void validate_passesForMinimalEnrichment() {
        assertThatCode(() -> validator.validate(enrichment(null, null, List.of(), List.of())))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_throwsWhenMaxAgeBelowMinAge() {
        assertThatThrownBy(() -> validator.validate(enrichment(30, 20, List.of(), List.of())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_throwsForNonStatusRequiredKeyword() {
        RequiredKeywordDraft bad = RequiredKeywordDraft.builder()
                .keywordCode(KeywordValueEnum.REGION_SEOUL)
                .effect(RequiredKeywordEffect.REQUIRE)
                .confidence(ExtractionConfidence.HIGH)
                .build();
        assertThatThrownBy(() -> validator.validate(enrichment(null, null, List.of(bad), List.of())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validate_acceptsBankEtcPreferentialWithNovelDescription() {
        PreferentialRateDraft etc = preferential(KeywordValueEnum.BANK_ETC, "0.1", "지점 방문 이벤트 참여 시");
        assertThatCode(() -> validator.validate(enrichment(null, null, List.of(), List.of(etc))))
                .doesNotThrowAnyException();
    }

    @Test
    void validate_throwsForOutOfRangePreferentialRate() {
        PreferentialRateDraft tooHigh = preferential(KeywordValueEnum.BANK_CARD_USAGE, "150", "카드 실적 우대");
        assertThatThrownBy(() -> validator.validate(enrichment(null, null, List.of(), List.of(tooHigh))))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
