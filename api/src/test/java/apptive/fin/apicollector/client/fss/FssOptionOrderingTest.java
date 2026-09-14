package apptive.fin.apicollector.client.fss;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FssOptionOrderingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FssOptionOrdering ordering = new FssOptionOrdering(objectMapper);

    @Test
    void sort_isStableRegardlessOfInputOrder() {
        List<JsonNode> ascending = options("6", "12", "24", "36");
        List<JsonNode> shuffled = options("24", "6", "36", "12");

        List<JsonNode> sortedAsc = ordering.sort(ascending);
        List<JsonNode> sortedShuffled = ordering.sort(shuffled);

        assertThat(saveTrms(sortedShuffled)).containsExactly("6", "12", "24", "36");
        assertThat(sortedShuffled).isEqualTo(sortedAsc);
    }

    @Test
    void sort_producesIdenticalSerializationRegardlessOfOrder() {
        String first = serialize(ordering.sort(options("6", "12", "24")));
        String second = serialize(ordering.sort(options("24", "12", "6")));

        assertThat(second).isEqualTo(first);
    }

    @Test
    void sort_ordersByRateTypeAndReserveTypeWhenTermIsEqual() {
        JsonNode single = option("12", "S", "S");
        JsonNode mixed = option("12", "M", "F");

        List<JsonNode> sorted = ordering.sort(List.of(mixed, single));

        // intr_rate_type: "M" < "S", so mixed comes first
        assertThat(sorted.get(0).path("intr_rate_type").asString()).isEqualTo("M");
        assertThat(sorted.get(1).path("intr_rate_type").asString()).isEqualTo("S");
    }

    private List<JsonNode> options(String... saveTerms) {
        return java.util.Arrays.stream(saveTerms)
                .map(term -> option(term, "S", "S"))
                .map(JsonNode.class::cast)
                .toList();
    }

    private JsonNode option(String saveTrm, String intrRateType, String rsrvType) {
        return objectMapper.createObjectNode()
                .put("save_trm", saveTrm)
                .put("intr_rate_type", intrRateType)
                .put("rsrv_type", rsrvType);
    }

    private List<String> saveTrms(List<JsonNode> options) {
        return options.stream().map(o -> o.path("save_trm").asString()).toList();
    }

    private String serialize(List<JsonNode> options) {
        return objectMapper.writeValueAsString(options);
    }
}
