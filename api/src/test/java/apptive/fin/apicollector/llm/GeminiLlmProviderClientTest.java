package apptive.fin.apicollector.llm;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
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
                .putNull("govContributionRate"));

        assertThat(result.summaryContent()).isEqualTo("요약");
        assertThat(result.keywords()).containsExactly("BANK_CARD_USAGE");
        assertThat(result.maxMonthlyLimit()).isEqualTo(100_000L);
        assertThat(result.minAge()).isEqualTo(19);
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
                  "govContributionRate": null
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
    void rejectsUnexpectedResponseShape() {
        assertThatThrownBy(() -> client.parseResponse(objectMapper.createObjectNode()
                .put("unexpected", "value")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("missing field");
    }

    @Test
    void rejectsNonArrayKeywords() {
        assertThatThrownBy(() -> client.parseResponse(objectMapper.createObjectNode()
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
                .putNull("govContributionRate")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("keywords must be an array");
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
                new CollectorProperties.Llm(true, "GEMINI", "gemini-test", 1, 1, "http://localhost", "key")
        );
    }
}
