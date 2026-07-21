package apptive.fin.apicollector.util;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class JsonNodesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ObjectNode node() {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("padded", "  hi  ");
        node.put("blank", "   ");
        node.putNull("nullField");
        node.put("commaInt", "1,234");
        node.put("spacedInt", "  12  ");
        node.put("notNumber", "abc");
        node.put("zero", "0");
        node.put("bigLong", "1,000,000");
        node.put("dec", "1,234.5");
        node.put("truthy", "true");
        node.put("falsy", "false");
        return node;
    }

    @Test
    void text_trimsAndNullsOutBlankOrMissing() {
        ObjectNode node = node();
        assertThat(JsonNodes.text(node, "padded")).isEqualTo("hi");
        assertThat(JsonNodes.text(node, "blank")).isNull();
        assertThat(JsonNodes.text(node, "nullField")).isNull();
        assertThat(JsonNodes.text(node, "missing")).isNull();
    }

    @Test
    void integer_stripsCommasAndWhitespace() {
        ObjectNode node = node();
        assertThat(JsonNodes.integer(node, "commaInt")).isEqualTo(1234);
        assertThat(JsonNodes.integer(node, "spacedInt")).isEqualTo(12);
        assertThat(JsonNodes.integer(node, "notNumber")).isNull();
        assertThat(JsonNodes.integer(node, "missing")).isNull();
    }

    @Test
    void longValue_variantsControlZeroHandling() {
        ObjectNode node = node();
        assertThat(JsonNodes.longValueOrNullIfZero(node, "zero")).isNull();
        assertThat(JsonNodes.longValue(node, "zero")).isEqualTo(0L);
        assertThat(JsonNodes.longValueOrNullIfZero(node, "bigLong")).isEqualTo(1_000_000L);
        assertThat(JsonNodes.longValue(node, "bigLong")).isEqualTo(1_000_000L);
    }

    @Test
    void decimal_stripsCommas() {
        assertThat(JsonNodes.decimal(node(), "dec")).isEqualByComparingTo(new BigDecimal("1234.5"));
    }

    @Test
    void bool_parsesOrDefaultsFalse() {
        ObjectNode node = node();
        assertThat(JsonNodes.bool(node, "truthy")).isTrue();
        assertThat(JsonNodes.bool(node, "falsy")).isFalse();
        assertThat(JsonNodes.bool(node, "missing")).isFalse();
    }
}
