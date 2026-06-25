package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.llm.*;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FssLlmProductDraftEnricherTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void returnsOriginalDraftWhenDisabled() {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        FssLlmProductDraftEnricher enricher = new FssLlmProductDraftEnricher(
                properties(false),
                List.of(providerClient),
                cacheRepository,
                objectMapper
        );
        ProductDraft draft = draft();

        ProductDraft result = enricher.enrich(raw(), draft);

        assertThat(result).isSameAs(draft);
        verifyNoInteractions(providerClient, cacheRepository);
    }

    @Test
    void enrichesDraftAndPreservesFssFacts() {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약된 상품 설명",
                List.of("BANK_CARD_USAGE", "BENEFIT_TAX_FREE"),
                10_000L,
                1_000_000L,
                19,
                34,
                50_000_000L,
                180,
                true,
                false,
                new BigDecimal("3.5"),
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(RequiredKeywordDraft.builder()
                        .keywordCode(KeywordValueEnum.STATUS_SME_WORKER)
                        .effect(RequiredKeywordEffect.REQUIRE)
                        .confidence(ExtractionConfidence.HIGH)
                        .build()),
                List.of(PreferentialRateDraft.builder()
                        .keywordCode(KeywordValueEnum.BANK_CARD_USAGE)
                        .rate(new BigDecimal("0.5"))
                        .description("카드 사용 우대")
                        .build())
        ));
        when(cacheRepository.findBySourceAndExternalIdAndContentHashAndProviderAndModelAndPromptVersionAndSchemaVersion(
                any(), any(), any(), any(), any(), anyInt(), anyInt()
        )).thenReturn(Optional.empty());

        FssLlmProductDraftEnricher enricher = new FssLlmProductDraftEnricher(
                properties(true),
                List.of(providerClient),
                cacheRepository,
                objectMapper
        );

        ProductDraft result = enricher.enrich(raw(), draft());
        ProductPropertyDraft property = result.properties().getFirst();

        assertThat(result.productName()).isEqualTo("청년 적금");
        assertThat(result.content()).isEqualTo("원문 설명");
        assertThat(result.contentSummary()).isEqualTo("요약된 상품 설명");
        assertThat(property.baseRate()).isEqualByComparingTo("3.0");
        assertThat(property.maxRate()).isEqualByComparingTo("4.0");
        assertThat(property.maxMonthlyLimit()).isEqualTo(300_000L);
        assertThat(property.minMonthlyLimit()).isEqualTo(10_000L);
        assertThat(property.minAge()).isEqualTo(19);
        assertThat(property.maxAge()).isEqualTo(34);
        assertThat(property.requiresHomeless()).isTrue();
        assertThat(property.requiresHouseholder()).isFalse();
        assertThat(property.govContributionRate()).isEqualByComparingTo("3.5");
        assertThat(property.requiredKeywords())
                .extracting(RequiredKeywordDraft::keywordCode)
                .containsExactly(KeywordValueEnum.STATUS_SME_WORKER);
        assertThat(property.preferentialRates())
                .extracting(PreferentialRateDraft::keywordCode)
                .containsExactly(KeywordValueEnum.BANK_CARD_USAGE);
        assertThat(property.keywords()).contains(
                KeywordValueEnum.BANK_CARD_USAGE,
                KeywordValueEnum.BENEFIT_TAX_FREE,
                KeywordValueEnum.TERM_AROUND_1_YEAR
        );
        assertThat(property.keywords()).doesNotContain(KeywordValueEnum.REGION_SEOUL);
        verify(cacheRepository).save(any(LlmEnrichmentCache.class));
    }

    @Test
    void fallsBackToOriginalDraftWhenProviderFails() {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenThrow(new IllegalStateException("boom"));
        when(cacheRepository.findBySourceAndExternalIdAndContentHashAndProviderAndModelAndPromptVersionAndSchemaVersion(
                any(), any(), any(), any(), any(), anyInt(), anyInt()
        )).thenReturn(Optional.empty());

        FssLlmProductDraftEnricher enricher = new FssLlmProductDraftEnricher(
                properties(true),
                List.of(providerClient),
                cacheRepository,
                objectMapper
        );
        ProductDraft draft = draft();

        ProductDraft result = enricher.enrich(raw(), draft);

        assertThat(result).isSameAs(draft);
        verify(cacheRepository).save(any(LlmEnrichmentCache.class));
    }

    private ProductRaw raw() {
        return new ProductRaw(Source.FSS, "FSS:SAVING:001:ABC", "hash", """
                {
                  "source": "FSS",
                  "base": {
                    "fin_prdt_nm": "청년 적금",
                    "join_member": "실명의 개인",
                    "etc_note": "월 1만원 이상 가입"
                  }
                }
                """, ProductType.SAVING);
    }

    private ProductDraft draft() {
        return ProductDraft.builder()
                .rawId(1L)
                .rawSource(Source.FSS)
                .normalizerVersion(1)
                .sourceCode("FSS")
                .type(ProductType.SAVING)
                .productCode("FSS:SAVING:001:ABC")
                .productName("청년 적금")
                .content("원문 설명")
                .properties(List.of(ProductPropertyDraft.builder()
                        .providerCode("001")
                        .providerName("테스트은행")
                        .saveTerm(12)
                        .baseRate(new BigDecimal("3.0"))
                        .maxRate(new BigDecimal("4.0"))
                        .maxMonthlyLimit(300_000L)
                        .keywords(List.of(KeywordValueEnum.REGION_SEOUL))
                        .build()))
                .build();
    }

    private CollectorProperties properties(boolean llmEnabled) {
        return new CollectorProperties(
                true,
                Source.FSS,
                Mode.SYNC,
                1,
                100,
                7,
                new CollectorProperties.OntongYouth("http://localhost", "key", 100),
                new CollectorProperties.Fss("http://localhost", "key", 100),
                new CollectorProperties.Llm(
                        llmEnabled,
                        "GEMINI",
                        "gemini-test",
                        1,
                        1,
                        "http://localhost",
                        llmEnabled ? "key" : ""
                )
        );
    }
}
