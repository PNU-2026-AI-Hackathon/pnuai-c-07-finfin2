package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawJsonReaderTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RawJsonReader reader = new RawJsonReader(objectMapper, "FSS");

    private ProductRaw rawWith(String rawJson) {
        return new ProductRaw(Source.FSS, "EXT-1", "hash", rawJson, ProductType.DEPOSIT);
    }

    @Test
    void read_parsesValidJson() {
        assertThat(reader.read(rawWith("{\"a\":1}")).path("a").asInt()).isEqualTo(1);
    }

    @Test
    void read_wrapsParseFailureWithLabel() {
        assertThatThrownBy(() -> reader.read(rawWith("{invalid")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Failed to parse FSS raw JSON. rawId=");
    }

    @Test
    void required_returnsValueOrThrowsWithLabel() {
        ProductRaw raw = rawWith("{}");
        assertThat(reader.required("value", "productName", raw)).isEqualTo("value");
        assertThatThrownBy(() -> reader.required(null, "productName", raw))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("FSS productName is required. rawId=");
    }

    @Test
    void label_isBoundAtConstruction() {
        RawJsonReader manual = new RawJsonReader(objectMapper, "Manual product");
        assertThatThrownBy(() -> manual.required(null, "accountName", rawWith("{}")))
                .hasMessageContaining("Manual product accountName is required.");
    }
}
