package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.normalizer.ManualProductNormalizer;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.type()).isEqualTo(ProductType.SUBSCRIPTION);
        assertThat(draft.properties().getFirst().requiredKeywords()).isEmpty();
    }

    @Test
    void parsesManualRequiredKeywords() {
        ProductRaw raw = raw("""
                {
                  "productCode": "POLICY999",
                  "productName": "신분 제한 상품",
                  "properties": [
                    {
                      "providerCode": "TEST",
                      "providerName": "테스트기관",
                      "requiredKeywords": [
                        {
                          "keywordCode": "STATUS_SME_WORKER",
                          "effect": "REQUIRE",
                          "confidence": "HIGH"
                        },
                        {
                          "keywordCode": "STATUS_UNEMPLOYED",
                          "effect": "EXCLUDE",
                          "confidence": "MEDIUM"
                        }
                      ]
                    }
                  ]
                }
                """, ProductType.POLICY);

        var requiredKeywords = normalizer.normalize(raw).properties().getFirst().requiredKeywords();

        assertThat(requiredKeywords)
                .extracting(
                        required -> required.keywordCode(),
                        required -> required.effect(),
                        required -> required.confidence()
                )
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(
                                KeywordValueEnum.STATUS_SME_WORKER,
                                RequiredKeywordEffect.REQUIRE,
                                ExtractionConfidence.HIGH
                        ),
                        org.assertj.core.groups.Tuple.tuple(
                                KeywordValueEnum.STATUS_UNEMPLOYED,
                                RequiredKeywordEffect.EXCLUDE,
                                ExtractionConfidence.MEDIUM
                        )
                );
    }

    @Test
    void rejectsNonStatusManualRequiredKeyword() {
        ProductRaw raw = rawWithRequiredKeyword("""
                {
                  "keywordCode": "BENEFIT_GOV_SUBSIDY",
                  "effect": "REQUIRE",
                  "confidence": "HIGH"
                }
                """);

        assertThatThrownBy(() -> normalizer.normalize(raw))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must use a STATUS_* code");
    }

    @Test
    void rejectsIncompleteManualRequiredKeyword() {
        ProductRaw raw = rawWithRequiredKeyword("""
                {
                  "keywordCode": "STATUS_SME_WORKER",
                  "effect": "REQUIRE"
                }
                """);

        assertThatThrownBy(() -> normalizer.normalize(raw))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("requiredKeywords[].confidence is required");
    }

    @Test
    void rejectsUnknownManualRequiredKeywordEnum() {
        ProductRaw raw = rawWithRequiredKeyword("""
                {
                  "keywordCode": "STATUS_SME_WORKER",
                  "effect": "UNKNOWN",
                  "confidence": "HIGH"
                }
                """);

        assertThatThrownBy(() -> normalizer.normalize(raw))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported manual required keyword");
    }

    @Test
    void excludesLegacyOntongYouthRawInsteadOfFailingRenormalization() {
        ProductRaw raw = raw("""
                {
                  "plcyNo": "R202401010001",
                  "plcyNm": "Legacy youth policy",
                  "sprvsnInstCd": "LEGACY"
                }
                """, ProductType.POLICY);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.classification()).isEqualTo(ProductClassification.EXCLUDED);
        assertThat(draft.shouldSaveProduct()).isFalse();
        assertThat(draft.normalizerVersion()).isEqualTo(19);
        assertThat(draft.sourceCode()).isEqualTo("ONTONG");
    }

    @Test
    void stillRejectsMalformedManualRaw() {
        ProductRaw raw = raw("""
                {
                  "productCode": "POLICY999",
                  "properties": []
                }
                """, ProductType.POLICY);

        assertThatThrownBy(() -> normalizer.normalize(raw))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("productName is required");
    }

    @Test
    void normalizesPolicy002FromActualManualProductsResource() throws Exception {
        JsonNodeHolder resource = loadProduct("POLICY002");
        ProductDraft draft = normalizer.normalize(new ProductRaw(
                Source.ONTONG,
                "POLICY002",
                "hash",
                resource.objectMapper().writeValueAsString(resource.product()),
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

    @Test
    void normalizesPolicy011RequiredKeywordFromActualManualProductsResource() throws Exception {
        JsonNodeHolder resource = loadProduct("POLICY011");
        ProductDraft draft = normalizer.normalize(new ProductRaw(
                Source.ONTONG,
                "POLICY011",
                "hash",
                resource.objectMapper().writeValueAsString(resource.product()),
                ProductType.POLICY
        ));

        assertThat(draft.properties().getFirst().requiredKeywords())
                .singleElement()
                .satisfies(required -> {
                    assertThat(required.keywordCode()).isEqualTo(KeywordValueEnum.STATUS_SME_WORKER);
                    assertThat(required.effect()).isEqualTo(RequiredKeywordEffect.REQUIRE);
                    assertThat(required.confidence()).isEqualTo(ExtractionConfidence.HIGH);
                });
    }

    @Test
    void actualManualProductsResourceContainsAndNormalizesPolicy001Through016() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode products = loadProducts(objectMapper);
        List<String> productCodes = new ArrayList<>();

        for (JsonNode product : products) {
            String productCode = product.path("productCode").asString();
            productCodes.add(productCode);
            ProductDraft draft = normalizer.normalize(new ProductRaw(
                    Source.ONTONG,
                    productCode,
                    "hash",
                    objectMapper.writeValueAsString(product),
                    ProductType.POLICY
            ));

            assertThat(draft.sourceCode()).isEqualTo("ONTONG");
            assertThat(draft.productCode()).isEqualTo(productCode);
            assertThat(draft.properties()).isNotEmpty();
        }

        assertThat(productCodes).containsExactly(
                "POLICY001", "POLICY002", "POLICY003", "POLICY004",
                "POLICY005", "POLICY006", "POLICY007", "POLICY008",
                "POLICY009", "POLICY010", "POLICY011", "POLICY012",
                "POLICY013", "POLICY014", "POLICY015", "POLICY016"
        );
    }

    private ProductRaw raw(String json, ProductType type) {
        return new ProductRaw(Source.ONTONG, "POLICY002", "hash", json, type);
    }

    private ProductRaw rawWithRequiredKeyword(String requiredKeyword) {
        return raw("""
                {
                  "productCode": "POLICY999",
                  "productName": "신분 제한 상품",
                  "properties": [
                    {
                      "providerCode": "TEST",
                      "providerName": "테스트기관",
                      "requiredKeywords": [%s]
                    }
                  ]
                }
                """.formatted(requiredKeyword), ProductType.POLICY);
    }

    private JsonNodeHolder loadProduct(String productCode) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        var products = loadProducts(objectMapper);
        for (var product : products) {
            if (productCode.equals(product.path("productCode").asString())) {
                return new JsonNodeHolder(objectMapper, product);
            }
        }
        throw new AssertionError(productCode + " not found in manual-products.json");
    }

    private JsonNode loadProducts(ObjectMapper objectMapper) throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/manual-products.json")) {
            assertThat(input).isNotNull();
            return objectMapper.readTree(input);
        }
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

    private record JsonNodeHolder(ObjectMapper objectMapper, tools.jackson.databind.JsonNode product) {
    }
}
