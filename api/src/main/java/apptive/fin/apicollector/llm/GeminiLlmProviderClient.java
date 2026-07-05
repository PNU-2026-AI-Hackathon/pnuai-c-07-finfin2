package apptive.fin.apicollector.llm;

import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ContributionType;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

        Double temperature = properties.llm().temperature();
        if (temperature != null) {
            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", temperature);
            body.set("generation_config", generationConfig);
        }

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
        properties.set("govContributionType", contributionTypeSchema());
        properties.set("govMatchingRatio", numberSchema("정부 매칭 비율. 없으면 null"));
        properties.set("govMonthlyFixedContribution", integerSchema("정부 월 정액 지원금. 없으면 null"));
        properties.set("govContributionPeriodMonths", integerSchema("정부 지원 기간 개월 수. 없으면 null"));
        properties.set("excludeFromRateComparison", booleanSchema("금리/수익률 비교 대상에서 제외해야 하면 true"));
        properties.set("allowsMilitaryAgeExtension", booleanSchema("병역 이행 기간만큼 나이 연장 조건이 명시되어 있으면 true"));
        properties.set("militaryMaxAge", integerSchema("병역 연장 적용 후 최대 나이. 없으면 null"));
        properties.set("requiredKeywords", requiredKeywordsSchema());
        properties.set("preferentialRates", preferentialRatesSchema());

        schema.set("properties", properties);
        schema.set("required", array(
                "summaryContent",
                "keywords",
                "minMonthlyLimit",
                "maxMonthlyLimit",
                "minAge",
                "maxAge",
                "earnMaxAmt",
                "earnPercent",
                "requiresHomeless",
                "requiresHouseholder",
                "govContributionRate",
                "govContributionType",
                "govMatchingRatio",
                "govMonthlyFixedContribution",
                "govContributionPeriodMonths",
                "excludeFromRateComparison",
                "allowsMilitaryAgeExtension",
                "militaryMaxAge",
                "requiredKeywords",
                "preferentialRates"
        ));
        return schema;
    }

    private ObjectNode contributionTypeSchema() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.set("type", array("string", "null"));
        schema.set("enum", nullableArray("NONE", "RATIO", "FIXED_AMOUNT"));
        schema.put("description", "정부 지원 방식. 없으면 null");
        return schema;
    }

    private ObjectNode requiredKeywordsSchema() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "array");
        schema.put("description", "가입 가능 여부를 제한하는 필수/제외 신분 키워드");

        ObjectNode item = objectMapper.createObjectNode();
        item.put("type", "object");
        ObjectNode properties = objectMapper.createObjectNode();
        properties.set("keywordCode", keywordEnumSchema("STATUS_* 키워드만 허용", requiredKeywordEnumValues()));
        properties.set("effect", enumSchema("필수 또는 제외 조건", "REQUIRE", "EXCLUDE"));
        properties.set("confidence", enumSchema("추출 신뢰도", "HIGH", "MEDIUM", "LOW"));
        item.set("properties", properties);
        item.set("required", array("keywordCode", "effect", "confidence"));
        schema.set("items", item);
        return schema;
    }

    private ObjectNode preferentialRatesSchema() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "array");
        schema.put("description", """
                조건별 명시 가산 우대금리. 총합/최고우대금리만 있으면 빈 배열.
                제공된 BANK_* 키워드로 정확히 표현할 수 없는 우대조건은 제외한다.
                요구불평잔, 평균잔액, 예금/적금 보유, 특정 상품 만기/해지 고객, 추천/쿠폰/이벤트 조건은 제외한다.
                """);

        ObjectNode item = objectMapper.createObjectNode();
        item.put("type", "object");
        ObjectNode properties = objectMapper.createObjectNode();
        properties.set("keywordCode", keywordEnumSchema("""
                우대금리 조건 키워드.
                BANK_CARD_USAGE=카드 보유/사용/결제실적,
                BANK_SALARY_TRANSFER=급여이체,
                BANK_AUTO_TRANSFER=자동이체,
                BANK_MARKETING=마케팅/개인정보 동의,
                BANK_FIRST_TRANSACTION=첫거래/최초거래/신규고객,
                BANK_REDEPOSIT=재예치/재가입,
                BANK_ONLINE_JOIN=인터넷/모바일/비대면 가입,
                BANK_AGE=나이/연령 조건.
                의미가 정확히 맞지 않으면 항목을 만들지 않는다.
                """, preferentialRateKeywordEnumValues()));
        properties.set("rate", numberSchema("가산 우대금리 percentage point"));
        properties.set("description", stringSchema("원문 근거 요약"));
        properties.set("minAge", integerSchema("나이 우대 최소 나이. 없으면 null"));
        properties.set("maxAge", integerSchema("나이 우대 최대 나이. 없으면 null"));
        item.set("properties", properties);
        item.set("required", array("keywordCode", "rate", "description", "minAge", "maxAge"));
        schema.set("items", item);
        return schema;
    }

    private ObjectNode enumSchema(String description, String... values) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "string");
        schema.set("enum", array(values));
        schema.put("description", description);
        return schema;
    }

    private ObjectNode keywordEnumSchema(String description, ArrayNode values) {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "string");
        schema.set("enum", values);
        schema.put("description", description);
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
        items.set("enum", enrichmentKeywordEnumValues());
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

    private ArrayNode nullableArray(String... values) {
        ArrayNode array = array(values);
        array.addNull();
        return array;
    }

    private ArrayNode enrichmentKeywordEnumValues() {
        ArrayNode array = objectMapper.createArrayNode();
        for (KeywordValueEnum keyword : KeywordValueEnum.values()) {
            if (!keyword.name().startsWith("TERM_")) {
                array.add(keyword.name());
            }
        }
        return array;
    }

    private ArrayNode requiredKeywordEnumValues() {
        ArrayNode array = objectMapper.createArrayNode();
        for (KeywordValueEnum keyword : KeywordValueEnum.values()) {
            if (isRequiredKeyword(keyword)) {
                array.add(keyword.name());
            }
        }
        return array;
    }

    private ArrayNode preferentialRateKeywordEnumValues() {
        ArrayNode array = objectMapper.createArrayNode();
        for (KeywordValueEnum keyword : KeywordValueEnum.values()) {
            if (isPreferentialRateKeyword(keyword)) {
                array.add(keyword.name());
            }
        }
        return array;
    }

    LlmProductEnrichment parseResponse(JsonNode response) {
        JsonNode enrichmentNode = extractEnrichmentNode(response);
        validateResponseShape(enrichmentNode);
        return new LlmProductEnrichment(
                text(enrichmentNode, "summaryContent"),
                enrichmentKeywords(enrichmentNode.path("keywords")),
                longValue(enrichmentNode, "minMonthlyLimit"),
                longValue(enrichmentNode, "maxMonthlyLimit"),
                integer(enrichmentNode, "minAge"),
                integer(enrichmentNode, "maxAge"),
                longValue(enrichmentNode, "earnMaxAmt"),
                integer(enrichmentNode, "earnPercent"),
                bool(enrichmentNode, "requiresHomeless"),
                bool(enrichmentNode, "requiresHouseholder"),
                decimal(enrichmentNode, "govContributionRate"),
                contributionType(enrichmentNode, "govContributionType"),
                decimal(enrichmentNode, "govMatchingRatio"),
                longValue(enrichmentNode, "govMonthlyFixedContribution"),
                integer(enrichmentNode, "govContributionPeriodMonths"),
                bool(enrichmentNode, "excludeFromRateComparison"),
                bool(enrichmentNode, "allowsMilitaryAgeExtension"),
                integer(enrichmentNode, "militaryMaxAge"),
                requiredKeywords(enrichmentNode.path("requiredKeywords")),
                preferentialRates(enrichmentNode.path("preferentialRates"))
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

        JsonNode steps = response.path("steps");
        if (!steps.isMissingNode() && !steps.isNull() && steps.isArray()) {
            String stepText = findStepText(steps);
            if (stepText != null) {
                return parseJsonText(stepText, "steps content text");
            }

            throw new IllegalStateException(
                    "Gemini response steps contain no text content. response=" + preview(response));
        }

        return response;
    }

    private String findStepText(JsonNode steps) {
        List<JsonNode> candidates = new ArrayList<>();
        for (JsonNode step : steps) {
            if ("model_output".equals(text(step, "type"))) {
                candidates.add(step);
            }
        }
        if (candidates.isEmpty()) {
            for (JsonNode step : steps) {
                candidates.add(step);
            }
        }

        String result = null;
        for (JsonNode step : candidates) {
            String value = firstContentText(step);
            if (value != null) {
                result = value;
            }
        }
        return result;
    }

    private String firstContentText(JsonNode step) {
        JsonNode content = step.path("content");
        if (!content.isArray()) {
            return null;
        }

        for (JsonNode item : content) {
            String value = text(item, "text");
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private JsonNode parseJsonText(String value, String fieldName) {
        String json = extractJsonObjectText(value);
        try {
            return objectMapper.readTree(json);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to parse Gemini " + fieldName, e);
        }
    }

    private String extractJsonObjectText(String value) {
        String text = value == null ? "" : value.trim();
        if (text.startsWith("```")) {
            int firstLineEnd = text.indexOf('\n');
            int lastFenceStart = text.lastIndexOf("```");
            if (firstLineEnd >= 0 && lastFenceStart > firstLineEnd) {
                text = text.substring(firstLineEnd + 1, lastFenceStart).trim();
            }
        }

        if (text.startsWith("{") && text.endsWith("}")) {
            return text;
        }

        int objectStart = text.indexOf('{');
        int objectEnd = text.lastIndexOf('}');
        if (objectStart >= 0 && objectEnd > objectStart) {
            return text.substring(objectStart, objectEnd + 1).trim();
        }

        return text;
    }

    private void validateResponseShape(JsonNode node) {
        if (node == null || !node.isObject()) {
            throw new IllegalStateException("Gemini response is not a JSON object. response=" + preview(node));
        }
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

    private List<String> enrichmentKeywords(JsonNode node) {
        List<String> values = stringList(node);
        if (values.isEmpty()) {
            return values;
        }

        List<String> result = new ArrayList<>();
        for (String value : values) {
            KeywordValueEnum keyword = KeywordValueEnum.from(value);
            if (keyword == null || keyword.name().startsWith("TERM_")) {
                log.debug("Dropping invalid Gemini keyword. keyword={}", value);
                continue;
            }
            result.add(keyword.name());
        }
        return List.copyOf(result);
    }

    private List<RequiredKeywordDraft> requiredKeywords(JsonNode node) {
        if (node == null || !node.isArray()) {
            return List.of();
        }

        List<RequiredKeywordDraft> result = new ArrayList<>();
        for (JsonNode item : node) {
            KeywordValueEnum keyword = keyword(item, "keywordCode");
            RequiredKeywordEffect effect = enumValue(RequiredKeywordEffect.class, text(item, "effect"));
            ExtractionConfidence confidence = enumValue(ExtractionConfidence.class, text(item, "confidence"));
            if (!isRequiredKeyword(keyword) || effect == null || confidence == null) {
                log.debug("Dropping invalid Gemini requiredKeywords item. response={}", preview(item));
                continue;
            }
            if (confidence != ExtractionConfidence.HIGH) {
                log.debug("Dropping low-confidence Gemini requiredKeywords item. response={}", preview(item));
                continue;
            }
            result.add(RequiredKeywordDraft.builder()
                    .keywordCode(keyword)
                    .effect(effect)
                    .confidence(confidence)
                    .build());
        }
        return List.copyOf(result);
    }

    private List<PreferentialRateDraft> preferentialRates(JsonNode node) {
        if (node == null || !node.isArray()) {
            return List.of();
        }

        List<PreferentialRateDraft> result = new ArrayList<>();
        for (JsonNode item : node) {
            KeywordValueEnum keyword = keyword(item, "keywordCode");
            BigDecimal rate = decimal(item, "rate");
            Integer minAge = integer(item, "minAge");
            Integer maxAge = integer(item, "maxAge");
            String description = text(item, "description");
            if (!isPreferentialRateKeyword(keyword) || rate == null || description == null) {
                log.debug("Dropping invalid Gemini preferentialRates item. response={}", preview(item));
                continue;
            }
            if (minAge != null && maxAge != null && maxAge < minAge) {
                log.debug("Dropping Gemini preferentialRates item with invalid age range. response={}", preview(item));
                continue;
            }
            PreferentialRateDraft draft = PreferentialRateDraft.builder()
                    .keywordCode(keyword)
                    .rate(rate)
                    .description(description)
                    .minAge(minAge)
                    .maxAge(maxAge)
                    .build();
            if (!draft.matchesKeywordCondition()) {
                log.debug("Dropping Gemini preferentialRates item with unsupported condition. response={}", preview(item));
                continue;
            }
            result.add(draft);
        }
        return List.copyOf(result);
    }

    private boolean isRequiredKeyword(KeywordValueEnum keyword) {
        return keyword != null && keyword.name().startsWith("STATUS_");
    }

    private boolean isPreferentialRateKeyword(KeywordValueEnum keyword) {
        return keyword != null && keyword.name().startsWith("BANK_");
    }

    private KeywordValueEnum keyword(JsonNode node, String fieldName) {
        return enumValue(KeywordValueEnum.class, text(node, fieldName));
    }

    private ContributionType contributionType(JsonNode node, String fieldName) {
        return enumValue(ContributionType.class, text(node, fieldName));
    }

    private <T extends Enum<T>> T enumValue(Class<T> enumType, String value) {
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, value);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
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
        return value != null && Boolean.parseBoolean(value);
    }
}
