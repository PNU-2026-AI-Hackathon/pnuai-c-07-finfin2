package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FetchManualRawTaskletTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FetchManualRawTasklet tasklet = new FetchManualRawTasklet(
            null,
            null,
            objectMapper
    );

    @Test
    void defaultsMissingTypeToPolicy() throws Exception {
        assertThat(tasklet.productType(objectMapper.readTree("{}"), "POLICY001"))
                .isEqualTo(ProductType.POLICY);
    }

    @Test
    void acceptsSubscriptionType() throws Exception {
        assertThat(tasklet.productType(objectMapper.readTree("{\"type\":\"SUBSCRIPTION\"}"), "SUB001"))
                .isEqualTo(ProductType.SUBSCRIPTION);
    }

    @Test
    void rejectsBankAndUnknownTypes() throws Exception {
        assertThatThrownBy(() -> tasklet.productType(
                objectMapper.readTree("{\"type\":\"SAVING\"}"),
                "INVALID001"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be POLICY or SUBSCRIPTION");

        assertThatThrownBy(() -> tasklet.productType(
                objectMapper.readTree("{\"type\":\"UNKNOWN\"}"),
                "INVALID002"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported manual product type");
    }
}
