package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.normalizer.ManualProductNormalizer;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ManualProductNormalizerTest {

    private final ManualProductNormalizer normalizer = new ManualProductNormalizer(
            new ObjectMapper(),
            properties()
    );

    @Test
    void preservesVariantCodesForIdenticalPropertyCoordinates() {
        ProductRaw raw = raw("""
                {
                  "productCode": "POLICY002",
                  "productName": "청년미래적금",
                  "properties": [
                    {
                      "variantCode": "GENERAL",
                      "providerCode": "KINFA",
                      "providerName": "서민금융진흥원",
                      "saveTerm": 36,
                      "govContributionType": "RATIO",
                      "govContributionRate": 2.00
                    },
                    {
                      "variantCode": "PREFERENTIAL",
                      "providerCode": "KINFA",
                      "providerName": "서민금융진흥원",
                      "saveTerm": 36,
                      "govContributionType": "RATIO",
                      "govContributionRate": 4.00
                    }
                  ]
                }
                """, ProductType.POLICY);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.properties())
                .extracting(property -> property.variantCode())
                .containsExactly("GENERAL", "PREFERENTIAL");
    }

    @Test
    void rejectsDuplicatePropertyCoordinatesWithoutVariantCode() {
        ProductRaw raw = raw("""
                {
                  "productCode": "POLICY002",
                  "productName": "청년미래적금",
                  "properties": [
                    {"providerCode": "KINFA", "providerName": "서민금융진흥원", "saveTerm": 36},
                    {"providerCode": "KINFA", "providerName": "서민금융진흥원", "saveTerm": 36}
                  ]
                }
                """, ProductType.POLICY);

        assertThatThrownBy(() -> normalizer.normalize(raw))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate manual product property key");
    }

    @Test
    void usesRawSubscriptionType() {
        ProductRaw raw = raw("""
                {
                  "productCode": "SUB001",
                  "productName": "청년 청약상품",
                  "properties": [
                    {"providerCode": "MOCT", "providerName": "국토교통부", "saveTerm": 12}
                  ]
                }
                """, ProductType.SUBSCRIPTION);

        assertThat(normalizer.normalize(raw).type()).isEqualTo(ProductType.SUBSCRIPTION);
    }

    @Test
    void normalizesPolicy002FromActualManualProductsResource() throws Exception {
        JsonNodeHolder resource = loadPolicy002();
        ProductDraft draft = normalizer.normalize(new ProductRaw(
                Source.ONTONG,
                "POLICY002",
                "hash",
                resource.objectMapper().writeValueAsString(resource.policy002()),
                ProductType.POLICY
        ));

        assertThat(draft.productCode()).isEqualTo("POLICY002");
        assertThat(draft.properties()).hasSize(2);
        assertThat(draft.properties())
                .extracting(
                        property -> property.variantCode(),
                        property -> property.govContributionRate(),
                        property -> property.govMatchingRatio()
                )
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("GENERAL", new java.math.BigDecimal("2.0"), new java.math.BigDecimal("0.06")),
                        org.assertj.core.groups.Tuple.tuple("PREFERENTIAL", new java.math.BigDecimal("4.0"), new java.math.BigDecimal("0.12"))
                );
    }

    private ProductRaw raw(String json, ProductType type) {
        return new ProductRaw(Source.ONTONG, "POLICY002", "hash", json, type);
    }

    private JsonNodeHolder loadPolicy002() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream input = getClass().getResourceAsStream("/manual-products.json")) {
            assertThat(input).isNotNull();
            var products = objectMapper.readTree(input);
            for (var product : products) {
                if ("POLICY002".equals(product.path("productCode").asString())) {
                    return new JsonNodeHolder(objectMapper, product);
                }
            }
        }
        throw new AssertionError("POLICY002 not found in manual-products.json");
    }

    private CollectorProperties properties() {
        return new CollectorProperties(
                true,
                Source.ONTONG,
                Mode.NORMALIZE_ONLY,
                19,
                500,
                7,
                new CollectorProperties.OntongYouth("http://localhost", "key", 100),
                new CollectorProperties.Fss("http://localhost", "key", 100),
                new CollectorProperties.Llm(false, "GEMINI", "gemini-test", 1, 1, 10, 3, 0.1, "http://localhost", "")
        );
    }

    private record JsonNodeHolder(ObjectMapper objectMapper, tools.jackson.databind.JsonNode policy002) {
    }
}
