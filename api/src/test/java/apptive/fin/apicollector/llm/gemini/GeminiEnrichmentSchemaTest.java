package apptive.fin.apicollector.llm.gemini;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GeminiEnrichmentSchemaTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeminiEnrichmentSchema schema = new GeminiEnrichmentSchema(objectMapper);

    private List<String> values(JsonNode enumArray) {
        List<String> result = new ArrayList<>();
        enumArray.forEach(node -> result.add(node.asString()));
        return result;
    }

    @Test
    void build_declaresAllTopLevelKeysAsRequired() {
        ObjectNode built = schema.build();

        assertThat(values(built.get("required")))
                .hasSize(20)
                .contains("summaryContent", "keywords", "requiredKeywords", "preferentialRates", "militaryMaxAge");
    }

    @Test
    void build_keywordsEnumExcludesTermKeywords() {
        JsonNode keywordsEnum = schema.build()
                .get("properties").get("keywords").get("items").get("enum");

        assertThat(values(keywordsEnum)).isNotEmpty().noneMatch(v -> v.startsWith("TERM_"));
    }

    @Test
    void build_requiredKeywordsEnumContainsOnlyStatus() {
        JsonNode enumValues = schema.build()
                .get("properties").get("requiredKeywords")
                .get("items").get("properties").get("keywordCode").get("enum");

        assertThat(values(enumValues)).isNotEmpty().allMatch(v -> v.startsWith("STATUS_"));
    }

    @Test
    void build_preferentialRatesEnumContainsOnlyBank() {
        JsonNode enumValues = schema.build()
                .get("properties").get("preferentialRates")
                .get("items").get("properties").get("keywordCode").get("enum");

        assertThat(values(enumValues)).isNotEmpty().allMatch(v -> v.startsWith("BANK_"));
    }
}
