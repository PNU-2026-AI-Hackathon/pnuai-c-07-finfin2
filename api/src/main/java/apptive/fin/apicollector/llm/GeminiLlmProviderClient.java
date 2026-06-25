package apptive.fin.apicollector.llm;

import apptive.fin.apicollector.config.CollectorProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class GeminiLlmProviderClient implements LlmProviderClient {

    private static final String PROVIDER = "GEMINI";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final CollectorProperties properties;

    public GeminiLlmProviderClient(
            @Qualifier("geminiRestClient") RestClient restClient,
            ObjectMapper objectMapper,
            CollectorProperties properties
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public boolean supports(String provider) {
        return PROVIDER.equalsIgnoreCase(provider);
    }

    @Override
    public LlmProductEnrichment enrich(LlmProductEnrichmentRequest request) {
        JsonNode response = restClient.post()
                .uri("/v1beta/interactions")
                .header("x-goog-api-key", properties.llm().apiKey())
                .body(requestBody(request))
                .retrieve()
                .body(JsonNode.class);

        return parseResponse(response);
    }

    private ObjectNode requestBody(LlmProductEnrichmentRequest request) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", request.model());
        body.put("input", request.prompt());

        ObjectNode responseFormat = objectMapper.createObjectNode();
        responseFormat.put("type", "text");
        responseFormat.put("mime_type", "application/json");
        responseFormat.set("schema", enrichmentSchema());
        body.set("response_format", responseFormat);

        return body;
    }

    private ObjectNode enrichmentSchema() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");

        ObjectNode properties = objectMapper.createObjectNode();
        properties.set("summaryContent", stringSchema("상품 설명을 사용자에게 보여줄 수 있게 한국어로 간결히 정리한 내용"));
        properties.set("keywords", stringArraySchema("허용된 keyword enum 목록"));
        properties.set("minMonthlyLimit", integerSchema("명시된 최소 월 납입액 또는 최소 가입금액. 없으면 null"));
        properties.set("maxMonthlyLimit", integerSchema("명시된 유한 최대 월 납입액 또는 최대 가입한도. 제한 없음이면 null"));
        properties.set("minAge", integerSchema("가입 가능 최소 나이. 없으면 null"));
        properties.set("maxAge", integerSchema("가입 가능 최대 나이. 없으면 null"));
        properties.set("earnMaxAmt", integerSchema("가입 소득 상한 금액. 없으면 null"));
        properties.set("earnPercent", integerSchema("가입 소득 기준 중위소득 비율. 없으면 null"));
        properties.set("requiresHomeless", booleanSchema("무주택 조건이 명시되어 있으면 true"));
        properties.set("requiresHouseholder", booleanSchema("세대주 조건이 명시되어 있으면 true"));
        properties.set("govContributionRate", numberSchema("정부 기여금 또는 지원금 비율. 없으면 null"));

        schema.set("properties", properties);
        schema.set("required", array(
                "summaryContent",
                "keywords",
                "requiresHomeless",
                "requiresHouseholder"
        ));
        return schema;
    }

    private ObjectNode stringSchema(String description) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "string");
        schema.put("description", description);
        return schema;
    }

    private ObjectNode integerSchema(String description) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.set("type", array("integer", "null"));
        schema.put("description", description);
        return schema;
    }

    private ObjectNode numberSchema(String description) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.set("type", array("number", "null"));
        schema.put("description", description);
        return schema;
    }

    private ObjectNode booleanSchema(String description) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "boolean");
        schema.put("description", description);
        return schema;
    }

    private ObjectNode stringArraySchema(String description) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "array");
        schema.put("description", description);

        ObjectNode items = objectMapper.createObjectNode();
        items.put("type", "string");
        items.set("enum", array(
                "REGION_SEOUL",
                "REGION_BUSAN",
                "REGION_DAEGU",
                "REGION_INCHEON",
                "REGION_GWANGJU",
                "REGION_DAEJEON",
                "REGION_ULSAN",
                "REGION_SEJONG",
                "REGION_GYEONGGI",
                "REGION_GANGWON",
                "REGION_CHUNGBUK",
                "REGION_CHUNGNAM",
                "REGION_JEONBUK",
                "REGION_JEONNAM",
                "REGION_GYEONGBUK",
                "REGION_GYEONGNAM",
                "REGION_JEJU",
                "STATUS_UNEMPLOYED",
                "STATUS_PART_TIME",
                "STATUS_SME_WORKER",
                "STATUS_MILITARY",
                "BENEFIT_MAX_INTEREST",
                "BENEFIT_TAX_FREE",
                "BENEFIT_EASY_CONDITION",
                "BENEFIT_GOV_SUBSIDY",
                "BENEFIT_HOUSE_PREPARE",
                "INTEREST_SAVINGS",
                "INTEREST_LOAN",
                "BANK_FIRST_TRANSACTION",
                "BANK_SALARY_TRANSFER",
                "BANK_CARD_USAGE"
        ));
        schema.set("items", items);
        return schema;
    }

    private ArrayNode array(String... values) {
        ArrayNode array = objectMapper.createArrayNode();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }

    LlmProductEnrichment parseResponse(JsonNode response) {
        JsonNode enrichmentNode = extractEnrichmentNode(response);
        validateResponseShape(enrichmentNode);
        return new LlmProductEnrichment(
                text(enrichmentNode, "summaryContent"),
                stringList(enrichmentNode.path("keywords")),
                longValue(enrichmentNode, "minMonthlyLimit"),
                longValue(enrichmentNode, "maxMonthlyLimit"),
                integer(enrichmentNode, "minAge"),
                integer(enrichmentNode, "maxAge"),
                longValue(enrichmentNode, "earnMaxAmt"),
                integer(enrichmentNode, "earnPercent"),
                bool(enrichmentNode, "requiresHomeless"),
                bool(enrichmentNode, "requiresHouseholder"),
                decimal(enrichmentNode, "govContributionRate")
        );
    }

    private JsonNode extractEnrichmentNode(JsonNode response) {
        if (response == null || response.isMissingNode() || response.isNull()) {
            throw new IllegalStateException("Gemini response body is empty");
        }

        String outputText = text(response, "output_text");
        if (outputText != null) {
            return parseJsonText(outputText, "output_text");
        }

        JsonNode stepText = response
                .path("steps")
                .path(1)
                .path("content")
                .path(0)
                .path("text");
        if (!stepText.isMissingNode() && !stepText.isNull()) {
            String value = stepText.asString(null);
            if (value != null && !value.isBlank()) {
                return parseJsonText(value, "steps[1].content[0].text");
            }
        }

        return response;
    }

    private JsonNode parseJsonText(String value, String fieldName) {
        try {
            return objectMapper.readTree(value);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to parse Gemini " + fieldName, e);
        }
    }

    private void validateResponseShape(JsonNode node) {
        if (node == null || !node.isObject()) {
            throw new IllegalStateException("Gemini response is not a JSON object. response=" + preview(node));
        }
        requireField(node, "summaryContent");
        requireField(node, "keywords");
        requireField(node, "minMonthlyLimit");
        requireField(node, "maxMonthlyLimit");
        requireField(node, "minAge");
        requireField(node, "maxAge");
        requireField(node, "earnMaxAmt");
        requireField(node, "earnPercent");
        requireField(node, "requiresHomeless");
        requireField(node, "requiresHouseholder");
        requireField(node, "govContributionRate");

        if (!node.path("keywords").isArray()) {
            throw new IllegalStateException("Gemini response keywords must be an array. response=" + preview(node));
        }
    }

    private void requireField(JsonNode node, String fieldName) {
        if (!node.has(fieldName)) {
            throw new IllegalStateException(
                    "Gemini response is missing field: %s. fields=%s, response=%s"
                            .formatted(fieldName, fieldNames(node), preview(node))
            );
        }
    }

    private List<String> fieldNames(JsonNode node) {
        if (node == null || !node.isObject()) {
            return List.of();
        }

        List<String> result = new ArrayList<>();
        result.addAll(node.propertyNames());
        return result;
    }

    private String preview(JsonNode node) {
        if (node == null) {
            return "null";
        }

        String value = node.toString();
        return value.length() <= 1000 ? value : value.substring(0, 1000);
    }

    private List<String> stringList(JsonNode node) {
        if (node == null || !node.isArray()) {
            return List.of();
        }

        List<String> result = new ArrayList<>();
        for (JsonNode item : node) {
            String value = item.asString(null);
            if (value != null && !value.isBlank()) {
                result.add(value);
            }
        }
        return result;
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        if (value == null || value.isMissingNode() || value.isNull()) {
            return null;
        }

        String text = value.asString(null);
        return text == null || text.isBlank() ? null : text.trim();
    }

    private Integer integer(JsonNode node, String fieldName) {
        String value = text(node, fieldName);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.replace(",", ""));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private Long longValue(JsonNode node, String fieldName) {
        String value = text(node, fieldName);
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value.replace(",", ""));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal decimal(JsonNode node, String fieldName) {
        String value = text(node, fieldName);
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value.replace(",", ""));
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean bool(JsonNode node, String fieldName) {
        String value = text(node, fieldName);
        return value == null ? null : Boolean.parseBoolean(value);
    }
}
