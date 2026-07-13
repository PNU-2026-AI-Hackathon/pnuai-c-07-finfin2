package apptive.fin.apicollector.llm;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeminiLlmProviderClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeminiLlmProviderClient client = new GeminiLlmProviderClient(
            RestClient.builder().baseUrl("http://localhost").build(),
            objectMapper,
            properties()
    );

    @Test
    void parsesExpectedResponseShape() {
        LlmProductEnrichment result = client.parseResponse(objectMapper.createObjectNode()
                .put("summaryContent", "요약")
                .set("keywords", objectMapper.createArrayNode().add("BANK_CARD_USAGE"))
                .putNull("minMonthlyLimit")
                .put("maxMonthlyLimit", 100000)
                .put("minAge", 19)
                .put("maxAge", 34)
                .putNull("earnMaxAmt")
                .putNull("earnPercent")
                .put("requiresHomeless", false)
                .put("requiresHouseholder", false)
                .putNull("govContributionRate")
                .putNull("govContributionType")
                .putNull("govMatchingRatio")
                .putNull("govMonthlyFixedContribution")
                .putNull("govContributionPeriodMonths")
                .put("excludeFromRateComparison", false)
                .put("allowsMilitaryAgeExtension", false)
                .putNull("militaryMaxAge")
                .set("requiredKeywords", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "STATUS_MILITARY")
                                .put("effect", "REQUIRE")
                                .put("confidence", "HIGH")
                                .put("evidence", "군 장병 대상")))
                .set("preferentialRates", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BANK_CARD_USAGE")
                                .put("rate", 0.3)
                                .put("description", "카드 실적 우대")
                                .put("minAge", 19)
                                .put("maxAge", 34))));

        assertThat(result.summaryContent()).isEqualTo("요약");
        assertThat(result.keywords()).containsExactly("BANK_CARD_USAGE");
        assertThat(result.maxMonthlyLimit()).isEqualTo(100_000L);
        assertThat(result.minAge()).isEqualTo(19);
        assertThat(result.requiredKeywords()).hasSize(1);
        assertThat(result.preferentialRates()).hasSize(1);
    }

    @Test
    void parsesInteractionsStepTextResponseShape() {
        String output = """
                {
                  "summaryContent": "요약",
                  "keywords": ["INTEREST_SAVINGS"],
                  "minMonthlyLimit": null,
                  "maxMonthlyLimit": 1000000,
                  "minAge": null,
                  "maxAge": null,
                  "earnMaxAmt": null,
                  "earnPercent": null,
                  "requiresHomeless": false,
                  "requiresHouseholder": false,
                  "govContributionRate": null,
                  "govContributionType": null,
                  "govMatchingRatio": null,
                  "govMonthlyFixedContribution": null,
                  "govContributionPeriodMonths": null,
                  "excludeFromRateComparison": false,
                  "allowsMilitaryAgeExtension": false,
                  "militaryMaxAge": null,
                  "requiredKeywords": [],
                  "preferentialRates": []
                }
                """;

        LlmProductEnrichment result = client.parseResponse(objectMapper.createObjectNode()
                .put("id", "v1_test")
                .put("status", "completed")
                .set("steps", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode().put("type", "thought"))
                        .add(objectMapper.createObjectNode()
                                .set("content", objectMapper.createArrayNode()
                                        .add(objectMapper.createObjectNode()
                                                .put("type", "text")
                                                .put("text", output))))));

        assertThat(result.summaryContent()).isEqualTo("요약");
        assertThat(result.keywords()).containsExactly("INTEREST_SAVINGS");
        assertThat(result.maxMonthlyLimit()).isEqualTo(1_000_000L);
    }

    @Test
    void parsesInteractionsModelOutputStepWithoutThoughtStep() {
        String output = """
                {
                  "summaryContent": "요약",
                  "keywords": ["INTEREST_SAVINGS"],
                  "minMonthlyLimit": null,
                  "maxMonthlyLimit": 500000,
                  "minAge": null,
                  "maxAge": null,
                  "earnMaxAmt": null,
                  "earnPercent": null,
                  "requiresHomeless": false,
                  "requiresHouseholder": false,
                  "govContributionRate": null,
                  "govContributionType": null,
                  "govMatchingRatio": null,
                  "govMonthlyFixedContribution": null,
                  "govContributionPeriodMonths": null,
                  "excludeFromRateComparison": false,
                  "allowsMilitaryAgeExtension": false,
                  "militaryMaxAge": null,
                  "requiredKeywords": [],
                  "preferentialRates": []
                }
                """;

        LlmProductEnrichment result = client.parseResponse(objectMapper.createObjectNode()
                .put("id", "v1_test")
                .put("status", "completed")
                .set("steps", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("type", "model_output")
                                .set("content", objectMapper.createArrayNode()
                                        .add(objectMapper.createObjectNode()
                                                .put("type", "text")
                                                .put("text", output))))));

        assertThat(result.summaryContent()).isEqualTo("요약");
        assertThat(result.keywords()).containsExactly("INTEREST_SAVINGS");
        assertThat(result.maxMonthlyLimit()).isEqualTo(500_000L);
    }

    @Test
    void parsesInteractionsModelOutputStepWithNonTextContentBeforeTextItem() {
        String output = """
                {
                  "summaryContent": "요약",
                  "keywords": ["INTEREST_SAVINGS"],
                  "minMonthlyLimit": null,
                  "maxMonthlyLimit": 700000,
                  "minAge": null,
                  "maxAge": null,
                  "earnMaxAmt": null,
                  "earnPercent": null,
                  "requiresHomeless": false,
                  "requiresHouseholder": false,
                  "govContributionRate": null,
                  "govContributionType": null,
                  "govMatchingRatio": null,
                  "govMonthlyFixedContribution": null,
                  "govContributionPeriodMonths": null,
                  "excludeFromRateComparison": false,
                  "allowsMilitaryAgeExtension": false,
                  "militaryMaxAge": null,
                  "requiredKeywords": [],
                  "preferentialRates": []
                }
                """;

        LlmProductEnrichment result = client.parseResponse(objectMapper.createObjectNode()
                .put("id", "v1_test")
                .put("status", "completed")
                .set("steps", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("type", "model_output")
                                .set("content", objectMapper.createArrayNode()
                                        .add(objectMapper.createObjectNode()
                                                .put("type", "thought_signature"))
                                        .add(objectMapper.createObjectNode()
                                                .put("type", "text")
                                                .put("text", output))))));

        assertThat(result.summaryContent()).isEqualTo("요약");
        assertThat(result.keywords()).containsExactly("INTEREST_SAVINGS");
        assertThat(result.maxMonthlyLimit()).isEqualTo(700_000L);
    }

    @Test
    void throwsWhenInteractionsStepsContainNoTextContent() {
        assertThatThrownBy(() -> client.parseResponse(objectMapper.createObjectNode()
                .put("id", "v1_test")
                .put("status", "completed")
                .set("steps", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode().put("type", "thought"))
                        .add(objectMapper.createObjectNode()
                                .put("type", "model_output")
                                .set("content", objectMapper.createArrayNode()
                                        .add(objectMapper.createObjectNode()
                                                .put("type", "thought_signature")))))))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void parsesInteractionsStepTextResponseWrappedInMarkdownFence() {
        String output = """
                ```json
                {
                  "summaryContent": "요약",
                  "keywords": ["INTEREST_SAVINGS"],
                  "minMonthlyLimit": null,
                  "maxMonthlyLimit": 1000000,
                  "minAge": null,
                  "maxAge": null,
                  "earnMaxAmt": null,
                  "earnPercent": null,
                  "requiresHomeless": false,
                  "requiresHouseholder": false,
                  "govContributionRate": null,
                  "govContributionType": null,
                  "govMatchingRatio": null,
                  "govMonthlyFixedContribution": null,
                  "govContributionPeriodMonths": null,
                  "excludeFromRateComparison": false,
                  "allowsMilitaryAgeExtension": false,
                  "militaryMaxAge": null,
                  "requiredKeywords": [],
                  "preferentialRates": []
                }
                ```
                """;

        LlmProductEnrichment result = client.parseResponse(objectMapper.createObjectNode()
                .put("id", "v1_test")
                .put("status", "completed")
                .set("steps", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode().put("type", "thought"))
                        .add(objectMapper.createObjectNode()
                                .set("content", objectMapper.createArrayNode()
                                        .add(objectMapper.createObjectNode()
                                                .put("type", "text")
                                                .put("text", output))))));

        assertThat(result.summaryContent()).isEqualTo("요약");
        assertThat(result.keywords()).containsExactly("INTEREST_SAVINGS");
        assertThat(result.maxMonthlyLimit()).isEqualTo(1_000_000L);
    }

    @Test
    void parsesPartialResponseWithDefaults() {
        LlmProductEnrichment result = client.parseResponse(objectMapper.createObjectNode()
                .put("summaryContent", "partial summary")
                .put("maxMonthlyLimit", 100000));

        assertThat(result.summaryContent()).isEqualTo("partial summary");
        assertThat(result.keywords()).isEmpty();
        assertThat(result.maxMonthlyLimit()).isEqualTo(100_000L);
        assertThat(result.minAge()).isNull();
        assertThat(result.requiresHomeless()).isFalse();
        assertThat(result.excludeFromRateComparison()).isFalse();
        assertThat(result.requiredKeywords()).isEmpty();
        assertThat(result.preferentialRates()).isEmpty();
    }

    @Test
    void treatsNonArrayCollectionsAsEmpty() {
        LlmProductEnrichment result = client.parseResponse(objectMapper.createObjectNode()
                .put("summaryContent", "요약")
                .put("keywords", "BANK_CARD_USAGE")
                .putNull("minMonthlyLimit")
                .putNull("maxMonthlyLimit")
                .putNull("minAge")
                .putNull("maxAge")
                .putNull("earnMaxAmt")
                .putNull("earnPercent")
                .put("requiresHomeless", false)
                .put("requiresHouseholder", false)
                .putNull("govContributionRate")
                .putNull("govContributionType")
                .putNull("govMatchingRatio")
                .putNull("govMonthlyFixedContribution")
                .putNull("govContributionPeriodMonths")
                .put("excludeFromRateComparison", false)
                .put("allowsMilitaryAgeExtension", false)
                .putNull("militaryMaxAge")
                .put("requiredKeywords", "STATUS_MILITARY")
                .put("preferentialRates", "BANK_CARD_USAGE"));

        assertThat(result.keywords()).isEmpty();
        assertThat(result.requiredKeywords()).isEmpty();
        assertThat(result.preferentialRates()).isEmpty();
    }

    @Test
    void dropsInvalidKeywordItemsFromOtherwiseValidResponse() {
        LlmProductEnrichment result = client.parseResponse(objectMapper.createObjectNode()
                .put("summaryContent", "summary")
                .set("keywords", objectMapper.createArrayNode()
                        .add("BANK_CARD_USAGE")
                        .add("TERM_AROUND_1_YEAR")
                        .add("DEPOSIT")
                        .add("INTERNET")
                        .add("MOBILE"))
                .putNull("minMonthlyLimit")
                .putNull("maxMonthlyLimit")
                .putNull("minAge")
                .putNull("maxAge")
                .putNull("earnMaxAmt")
                .putNull("earnPercent")
                .put("requiresHomeless", false)
                .put("requiresHouseholder", false)
                .putNull("govContributionRate")
                .putNull("govContributionType")
                .putNull("govMatchingRatio")
                .putNull("govMonthlyFixedContribution")
                .putNull("govContributionPeriodMonths")
                .put("excludeFromRateComparison", false)
                .put("allowsMilitaryAgeExtension", false)
                .putNull("militaryMaxAge")
                .set("requiredKeywords", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BANK_FIRST_TRANSACTION")
                                .put("effect", "REQUIRE")
                                .put("confidence", "HIGH"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "STATUS_MILITARY")
                                .put("effect", "REQUIRE")
                                .put("confidence", "HIGH")))
                .set("preferentialRates", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "TERM_AROUND_1_YEAR")
                                .put("rate", 0.7)
                                .put("description", "term condition")
                                .putNull("minAge")
                                .putNull("maxAge"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BENEFIT_MAX_INTEREST")
                                .put("rate", 0.2)
                                .put("description", "benefit keyword is not a rate condition")
                                .putNull("minAge")
                                .putNull("maxAge"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BANK_REDEPOSIT")
                                .putNull("rate")
                                .put("description", "missing rate")
                                .putNull("minAge")
                                .putNull("maxAge"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BANK_AUTO_TRANSFER")
                                .put("rate", 0.2)
                                .putNull("minAge")
                                .putNull("maxAge"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BANK_FIRST_TRANSACTION")
                                .put("rate", 0.2)
                                .put("description", "요구불평잔 500만원 이상")
                                .putNull("minAge")
                                .putNull("maxAge"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BANK_CARD_USAGE")
                                .put("rate", 0.3)
                                .put("description", "card usage")
                                .putNull("minAge")
                                .putNull("maxAge"))));

        assertThat(result.keywords()).containsExactly("BANK_CARD_USAGE");
        assertThat(result.requiredKeywords())
                .extracting(RequiredKeywordDraft::keywordCode)
                .containsExactly(KeywordValueEnum.STATUS_MILITARY);
        assertThat(result.preferentialRates())
                .extracting(PreferentialRateDraft::keywordCode)
                .containsExactly(KeywordValueEnum.BANK_CARD_USAGE);
    }

    @Test
    void dropsLowAndMediumConfidenceRequiredKeywords() {
        LlmProductEnrichment result = client.parseResponse(baseResponse()
                .set("requiredKeywords", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "STATUS_UNEMPLOYED")
                                .put("effect", "REQUIRE")
                                .put("confidence", "LOW"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "STATUS_PART_TIME")
                                .put("effect", "REQUIRE")
                                .put("confidence", "MEDIUM"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "STATUS_MILITARY")
                                .put("effect", "REQUIRE")
                                .put("confidence", "HIGH"))));

        assertThat(result.requiredKeywords())
                .extracting(RequiredKeywordDraft::keywordCode)
                .containsExactly(KeywordValueEnum.STATUS_MILITARY);
    }

    @Test
    void dropsFittedPreferentialRateKeywordsWhenConditionDoesNotMatchKeywordMeaning() {
        LlmProductEnrichment result = client.parseResponse(baseResponse()
                .set("preferentialRates", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BANK_MARKETING")
                                .put("rate", 0.1)
                                .put("description", "신규(재예치)시 마케팅동의 및 모바일메시지 수신동의 0.10%")
                                .putNull("minAge")
                                .putNull("maxAge"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BANK_ONLINE_JOIN")
                                .put("rate", 0.1)
                                .put("description", "신규(재예치)시 마케팅동의 및 모바일메시지 수신동의 0.10%")
                                .putNull("minAge")
                                .putNull("maxAge"))
                        .add(objectMapper.createObjectNode()
                                .put("keywordCode", "BANK_REDEPOSIT")
                                .put("rate", 0.1)
                                .put("description", "신규시 가입(재예치)금액 20백만원 이상인 경우 0.10%")
                                .putNull("minAge")
                                .putNull("maxAge"))));

        assertThat(result.preferentialRates())
                .extracting(PreferentialRateDraft::keywordCode)
                .containsExactly(KeywordValueEnum.BANK_MARKETING);
    }

    private tools.jackson.databind.node.ObjectNode baseResponse() {
        return objectMapper.createObjectNode()
                .put("summaryContent", "summary")
                .set("keywords", objectMapper.createArrayNode())
                .putNull("minMonthlyLimit")
                .putNull("maxMonthlyLimit")
                .putNull("minAge")
                .putNull("maxAge")
                .putNull("earnMaxAmt")
                .putNull("earnPercent")
                .put("requiresHomeless", false)
                .put("requiresHouseholder", false)
                .putNull("govContributionRate")
                .putNull("govContributionType")
                .putNull("govMatchingRatio")
                .putNull("govMonthlyFixedContribution")
                .putNull("govContributionPeriodMonths")
                .put("excludeFromRateComparison", false)
                .put("allowsMilitaryAgeExtension", false)
                .putNull("militaryMaxAge")
                .set("requiredKeywords", objectMapper.createArrayNode())
                .set("preferentialRates", objectMapper.createArrayNode());
    }

    private CollectorProperties properties() {
        return new CollectorProperties(
                true,
                Source.FSS,
                Mode.SYNC,
                1,
                100,
                7,
                new CollectorProperties.OntongYouth("http://localhost", "key", 100),
                new CollectorProperties.Fss("http://localhost", "key", 100),
                new CollectorProperties.Llm(true, "GEMINI", "gemini-test", 1, 1, 10, 3, 0.1, "http://localhost", "key")
        );
    }
}
