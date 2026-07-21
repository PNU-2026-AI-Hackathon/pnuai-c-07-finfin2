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

        ProductDraft result = enricher.enrich(raw("중소기업 재직 청년만 가입 가능", "월 1만원 이상 가입"), draft());
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
                KeywordValueEnum.REGION_SEOUL,
                KeywordValueEnum.TERM_AROUND_1_YEAR
        );
        verify(cacheRepository).save(any(LlmEnrichmentCache.class));
    }

    @Test
    void preservesDeterministicFieldsWhenLlmEnrichmentIsIncomplete() {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                null,
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
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
        ProductDraft draft = draftWithProperty(draft().properties().getFirst().toBuilder()
                .minAge(18)
                .maxAge(35)
                .earnMaxAmt(40_000_000L)
                .requiresHomeless(true)
                .excludeFromRateComparison(true)
                .build());

        ProductDraft result = enricher.enrich(raw(), draft);
        ProductPropertyDraft property = result.properties().getFirst();

        assertThat(result.contentSummary()).isNull();
        assertThat(property.maxMonthlyLimit()).isEqualTo(300_000L);
        assertThat(property.minAge()).isEqualTo(18);
        assertThat(property.maxAge()).isEqualTo(35);
        assertThat(property.earnMaxAmt()).isEqualTo(40_000_000L);
        assertThat(property.requiresHomeless()).isTrue();
        assertThat(property.excludeFromRateComparison()).isTrue();
        assertThat(property.keywords()).containsExactlyInAnyOrder(
                KeywordValueEnum.REGION_SEOUL,
                KeywordValueEnum.TERM_AROUND_1_YEAR
        );
        verify(cacheRepository).save(any(LlmEnrichmentCache.class));
    }

    @Test
    void dropsEarnMaxAmtAndEarnPercentWhenIncomeIsNotMentioned() {
        // 원문(content/eligibilityText)에 "소득" 언급이 없으면 LLM이 채운 earnMaxAmt/earnPercent는
        // 가입금액/한도를 착각한 오탐일 수 있으므로 신뢰하지 않고 버린다.
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                50_000_000L,
                180,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
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

        ProductDraft result = enricher.enrich(raw("실명의 개인", "월 1만원 이상 가입"), draft());
        ProductPropertyDraft property = result.properties().getFirst();

        assertThat(property.earnMaxAmt()).isNull();
        assertThat(property.earnPercent()).isNull();
    }

    @Test
    void keepsEarnMaxAmtAndEarnPercentWhenIncomeIsMentionedInEligibilityText() {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                50_000_000L,
                180,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
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

        ProductDraft result = enricher.enrich(raw("연소득 5천만원 이하인 개인", "월 1만원 이상 가입"), draft());
        ProductPropertyDraft property = result.properties().getFirst();

        assertThat(property.earnMaxAmt()).isEqualTo(50_000_000L);
        assertThat(property.earnPercent()).isEqualTo(180);
    }

    @Test
    void dropsEarnMaxAmtAndEarnPercentWhenOnlyFinancialIncomeAggregateTaxationMentioned() {
        // "금융소득종합과세"만 있고 실제 소득요건 언급이 없으면 소득 가드를 통과하지 못하고 버려야 한다.
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                50_000_000L,
                180,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
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

        ProductDraft result = enricher.enrich(raw("실명의 개인", "금융소득종합과세 대상자는 가입이 제한됩니다"), draft());
        ProductPropertyDraft property = result.properties().getFirst();

        assertThat(property.earnMaxAmt()).isNull();
        assertThat(property.earnPercent()).isNull();
    }

    @Test
    void dropsEarnMaxAmtAndEarnPercentWhenOnlyIncomeDeductionMentioned() {
        // "소득공제"만 있고 실제 소득요건 언급이 없으면 소득 가드를 통과하지 못하고 버려야 한다.
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                50_000_000L,
                180,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
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

        ProductDraft result = enricher.enrich(raw("실명의 개인", "소득공제 혜택"), draft());
        ProductPropertyDraft property = result.properties().getFirst();

        assertThat(property.earnMaxAmt()).isNull();
        assertThat(property.earnPercent()).isNull();
    }

    @Test
    void keepsEarnMaxAmtWhenEligibilityTextMentionsTotalSalary() {
        // "총급여" 표현도 소득요건 언급으로 인정해 earnMaxAmt를 유지해야 한다.
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                50_000_000L,
                180,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
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

        ProductDraft result = enricher.enrich(raw("총급여 5천만원 이하인 자", "월 1만원 이상 가입"), draft());
        ProductPropertyDraft property = result.properties().getFirst();

        assertThat(property.earnMaxAmt()).isEqualTo(50_000_000L);
    }

    @Test
    void keepsEarnMaxAmtWhenEligibilityTextMentionsAnnualSalary() {
        // "연봉" 표현도 소득요건 언급으로 인정해 earnMaxAmt를 유지해야 한다.
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                50_000_000L,
                180,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
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

        ProductDraft result = enricher.enrich(raw("연봉 4천만원 이하", "월 1만원 이상 가입"), draft());
        ProductPropertyDraft property = result.properties().getFirst();

        assertThat(property.earnMaxAmt()).isEqualTo(50_000_000L);
    }

    @Test
    void nullsMonthlyLimitsForDepositProducts() {
        // 정기예금(DEPOSIT)은 월 납입 개념이 없다. LLM이 가입금액을 min/maxMonthlyLimit에 채워도,
        // 결정적 FSS max가 있어도 두 필드는 모두 null로 강제되어야 한다.
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                1_000_000L,
                2_000_000L,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
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

        ProductDraft result = enricher.enrich(raw(), depositDraft());
        ProductPropertyDraft property = result.properties().getFirst();

        assertThat(property.minMonthlyLimit()).isNull();
        assertThat(property.maxMonthlyLimit()).isNull();
        verify(cacheRepository).save(any(LlmEnrichmentCache.class));
    }

    @Test
    void dropsFittedRequiredKeywordsWhenEligibilityTextDoesNotExplicitlyMatch() {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(
                        RequiredKeywordDraft.builder()
                                .keywordCode(KeywordValueEnum.STATUS_UNEMPLOYED)
                                .effect(RequiredKeywordEffect.REQUIRE)
                                .confidence(ExtractionConfidence.LOW)
                                .build(),
                        RequiredKeywordDraft.builder()
                                .keywordCode(KeywordValueEnum.STATUS_PART_TIME)
                                .effect(RequiredKeywordEffect.REQUIRE)
                                .confidence(ExtractionConfidence.HIGH)
                                .build(),
                        RequiredKeywordDraft.builder()
                                .keywordCode(KeywordValueEnum.STATUS_SME_WORKER)
                                .effect(RequiredKeywordEffect.EXCLUDE)
                                .confidence(ExtractionConfidence.HIGH)
                                .build()
                ),
                List.of()
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

        ProductDraft result = enricher.enrich(raw("만 17세 이상 실명의 개인 및 개인사업자", "가입금액: 1천원 이상"), draft());

        assertThat(result.properties().getFirst().requiredKeywords()).isEmpty();
    }

    @Test
    void keepsExplicitRequiredAndExcludedStatusKeywords() {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(
                        RequiredKeywordDraft.builder()
                                .keywordCode(KeywordValueEnum.STATUS_SME_WORKER)
                                .effect(RequiredKeywordEffect.REQUIRE)
                                .confidence(ExtractionConfidence.HIGH)
                                .build(),
                        RequiredKeywordDraft.builder()
                                .keywordCode(KeywordValueEnum.STATUS_MILITARY)
                                .effect(RequiredKeywordEffect.EXCLUDE)
                                .confidence(ExtractionConfidence.HIGH)
                                .build()
                ),
                List.of()
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

        ProductDraft result = enricher.enrich(raw(
                "중소기업 재직 청년만 가입 가능",
                "군인은 가입 제외"
        ), draft());

        assertThat(result.properties().getFirst().requiredKeywords())
                .extracting(RequiredKeywordDraft::keywordCode)
                .containsExactlyInAnyOrder(KeywordValueEnum.STATUS_SME_WORKER, KeywordValueEnum.STATUS_MILITARY);
    }

    @Test
    void dropsUnsupportedKeywordsWithoutFailingEnrichment() {
        // 지원하지 않는 키워드(예: 삭제된 BENEFIT_HOUSE_PREPARE)가 섞여도 enrichment 전체를 실패시키지 않고,
        // 해당 항목만 조용히 드롭한 뒤 유효 키워드는 보존해야 한다.
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of("BANK_CARD_USAGE", "NOT_A_REAL_KEYWORD"),
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
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

        // enrichment이 적용됨(폴백 아님) → 요약이 채워지고 캐시가 성공 저장된다.
        assertThat(result.contentSummary()).isEqualTo("요약");
        // 유효 키워드는 유지, 미지원 키워드는 드롭(존재하지 않음).
        assertThat(result.properties().getFirst().keywords()).containsExactlyInAnyOrder(
                KeywordValueEnum.BANK_CARD_USAGE,
                KeywordValueEnum.REGION_SEOUL,
                KeywordValueEnum.TERM_AROUND_1_YEAR
        );
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

    @Test
    void skipsProviderCallWhenFailedCacheIsStillInCooldown() {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        LlmEnrichmentCache cache = LlmEnrichmentCache.create(
                Source.FSS,
                "FSS:SAVING:001:ABC",
                "hash",
                "GEMINI",
                "gemini-test",
                1,
                1,
                "request-hash"
        );
        cache.markFailed("request-hash", "503 Service Unavailable");
        when(cacheRepository.findBySourceAndExternalIdAndContentHashAndProviderAndModelAndPromptVersionAndSchemaVersion(
                any(), any(), any(), any(), any(), anyInt(), anyInt()
        )).thenReturn(Optional.of(cache));

        FssLlmProductDraftEnricher enricher = new FssLlmProductDraftEnricher(
                properties(true),
                List.of(providerClient),
                cacheRepository,
                objectMapper
        );
        ProductDraft draft = draft();

        ProductDraft result = enricher.enrich(raw(), draft);

        assertThat(result).isSameAs(draft);
        verify(providerClient, never()).enrich(any());
        verify(cacheRepository, never()).save(any());
    }

    @Test
    void callsProviderWhenSuccessCacheHashMismatches() {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);
        when(providerClient.enrich(any())).thenReturn(new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
        ));

        LlmEnrichmentCache cache = LlmEnrichmentCache.create(
                Source.FSS,
                "FSS:SAVING:001:ABC",
                "hash",
                "GEMINI",
                "gemini-test",
                1,
                1,
                "wrong-hash"
        );
        cache.markSuccess("wrong-hash", "{\"summaryContent\":\"요약\",\"keywords\":[],\"minMonthlyLimit\":null,\"maxMonthlyLimit\":null,\"minAge\":null,\"maxAge\":null,\"earnMaxAmt\":null,\"earnPercent\":null,\"requiresHomeless\":false,\"requiresHouseholder\":false,\"govContributionRate\":null,\"govContributionType\":null,\"govMatchingRatio\":null,\"govMonthlyFixedContribution\":null,\"govContributionPeriodMonths\":null,\"excludeFromRateComparison\":false,\"allowsMilitaryAgeExtension\":false,\"militaryMaxAge\":null,\"requiredKeywords\":[],\"preferentialRates\":[]}");
        when(cacheRepository.findBySourceAndExternalIdAndContentHashAndProviderAndModelAndPromptVersionAndSchemaVersion(
                any(), any(), any(), any(), any(), anyInt(), anyInt()
        )).thenReturn(Optional.of(cache));

        FssLlmProductDraftEnricher enricher = new FssLlmProductDraftEnricher(
                properties(true),
                List.of(providerClient),
                cacheRepository,
                objectMapper
        );
        ProductDraft draft = draft();

        ProductDraft result = enricher.enrich(raw(), draft);

        verify(providerClient).enrich(any());
        verify(cacheRepository).save(any());
    }

    @Test
    void skipsProviderWhenSuccessCacheHashMatches() throws Exception {
        LlmProviderClient providerClient = mock(LlmProviderClient.class);
        LlmEnrichmentCacheRepository cacheRepository = mock(LlmEnrichmentCacheRepository.class);
        when(providerClient.supports("GEMINI")).thenReturn(true);

        FssLlmProductDraftEnricher enricher = new FssLlmProductDraftEnricher(
                properties(true),
                List.of(providerClient),
                cacheRepository,
                objectMapper
        );

        ProductRaw raw = raw();
        ProductDraft draft = draft();

        // Compute requestHash using reflection to match what enricher will compute
        var promptMethod = FssLlmProductDraftEnricher.class.getDeclaredMethod("prompt", ProductRaw.class, ProductDraft.class);
        promptMethod.setAccessible(true);
        String prompt = (String) promptMethod.invoke(enricher, raw, draft);

        String requestHash = apptive.fin.apicollector.util.Sha256.hex(prompt);

        LlmProductEnrichment enrichment = new LlmProductEnrichment(
                "요약",
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                null,
                List.of(),
                List.of()
        );

        LlmEnrichmentCache cache = LlmEnrichmentCache.create(
                Source.FSS,
                "FSS:SAVING:001:ABC",
                "hash",
                "GEMINI",
                "gemini-test",
                1,
                1,
                requestHash
        );
        cache.markSuccess(requestHash, objectMapper.writeValueAsString(enrichment));

        when(cacheRepository.findBySourceAndExternalIdAndContentHashAndProviderAndModelAndPromptVersionAndSchemaVersion(
                any(), any(), any(), any(), any(), anyInt(), anyInt()
        )).thenReturn(Optional.of(cache));

        ProductDraft result = enricher.enrich(raw, draft);

        verify(providerClient, never()).enrich(any());
        assertThat(result.contentSummary()).isEqualTo("요약");
    }

    private ProductRaw raw() {
        return raw("실명의 개인", "월 1만원 이상 가입");
    }

    private ProductRaw raw(String joinMember, String etcNote) {
        return new ProductRaw(Source.FSS, "FSS:SAVING:001:ABC", "hash", """
                {
                  "source": "FSS",
                  "base": {
                    "fin_prdt_nm": "청년 적금",
                    "join_member": "%s",
                    "etc_note": "%s"
                  }
                }
                """.formatted(joinMember, etcNote), ProductType.SAVING);
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

    private ProductDraft depositDraft() {
        return draft().toBuilder()
                .type(ProductType.DEPOSIT)
                .productCode("FSS:DEPOSIT:001:ABC")
                .build();
    }

    private ProductDraft draftWithProperty(ProductPropertyDraft property) {
        return draft().toBuilder()
                .properties(List.of(property))
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
                        10,
                        3,
                        0.1,
                        "http://localhost",
                        llmEnabled ? "key" : ""
                )
        );
    }
}
