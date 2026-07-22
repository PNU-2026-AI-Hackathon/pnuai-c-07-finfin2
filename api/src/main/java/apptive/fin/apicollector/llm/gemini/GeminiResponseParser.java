package apptive.fin.apicollector.llm.gemini;

import apptive.fin.apicollector.llm.LlmProductEnrichment;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ContributionType;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import apptive.fin.apicollector.util.JsonNodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Gemini interactions 응답(JSON)에서 enrichment 본문을 추출·복구하고 {@link LlmProductEnrichment}로 매핑한다.
 * 코드펜스/steps 구조 등 다양한 응답 형태를 허용하고, 유효하지 않은 항목은 조용히 드롭한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiResponseParser {

    private final ObjectMapper objectMapper;

    public LlmProductEnrichment parse(JsonNode response) {
        JsonNode enrichmentNode = extractEnrichmentNode(response);
        validateResponseShape(enrichmentNode);
        return new LlmProductEnrichment(
                JsonNodes.text(enrichmentNode, "summaryContent"),
                enrichmentKeywords(enrichmentNode.path("keywords")),
                JsonNodes.longValue(enrichmentNode, "minMonthlyLimit"),
                JsonNodes.longValue(enrichmentNode, "maxMonthlyLimit"),
                JsonNodes.integer(enrichmentNode, "minAge"),
                JsonNodes.integer(enrichmentNode, "maxAge"),
                JsonNodes.longValue(enrichmentNode, "earnMaxAmt"),
                JsonNodes.integer(enrichmentNode, "earnPercent"),
                JsonNodes.bool(enrichmentNode, "requiresHomeless"),
                JsonNodes.bool(enrichmentNode, "requiresHouseholder"),
                JsonNodes.decimal(enrichmentNode, "govContributionRate"),
                contributionType(enrichmentNode, "govContributionType"),
                JsonNodes.decimal(enrichmentNode, "govMatchingRatio"),
                JsonNodes.longValue(enrichmentNode, "govMonthlyFixedContribution"),
                JsonNodes.integer(enrichmentNode, "govContributionPeriodMonths"),
                JsonNodes.bool(enrichmentNode, "excludeFromRateComparison"),
                JsonNodes.bool(enrichmentNode, "allowsMilitaryAgeExtension"),
                JsonNodes.integer(enrichmentNode, "militaryMaxAge"),
                requiredKeywords(enrichmentNode.path("requiredKeywords")),
                preferentialRates(enrichmentNode.path("preferentialRates"))
        );
    }

    private JsonNode extractEnrichmentNode(JsonNode response) {
        if (response == null || response.isMissingNode() || response.isNull()) {
            throw new IllegalStateException("Gemini response body is empty");
        }

        String outputText = JsonNodes.text(response, "output_text");
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
            if ("model_output".equals(JsonNodes.text(step, "type"))) {
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
            String value = JsonNodes.text(item, "text");
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
            RequiredKeywordEffect effect = enumValue(RequiredKeywordEffect.class, JsonNodes.text(item, "effect"));
            ExtractionConfidence confidence = enumValue(ExtractionConfidence.class, JsonNodes.text(item, "confidence"));
            if (keyword == null || !keyword.isRequired() || effect == null || confidence == null) {
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
            BigDecimal rate = JsonNodes.decimal(item, "rate");
            Integer minAge = JsonNodes.integer(item, "minAge");
            Integer maxAge = JsonNodes.integer(item, "maxAge");
            String description = JsonNodes.text(item, "description");
            if (keyword == null || !keyword.isPreferentialRate() || rate == null || description == null) {
                log.debug("Dropping invalid Gemini preferentialRates item. response={}", preview(item));
                continue;
            }
            if (minAge != null && maxAge != null && maxAge < minAge) {
                log.debug("Dropping Gemini preferentialRates item with invalid age range. response={}", preview(item));
                continue;
            }
            // 나이 구간 없는 BANK_AGE는 백엔드가 나이 필터를 못 하므로 일반 기타(BANK_ETC)로 재분류(금리는 보존).
            if (keyword == KeywordValueEnum.BANK_AGE && minAge == null && maxAge == null) {
                keyword = KeywordValueEnum.BANK_ETC;
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

    private KeywordValueEnum keyword(JsonNode node, String fieldName) {
        return enumValue(KeywordValueEnum.class, JsonNodes.text(node, fieldName));
    }

    private ContributionType contributionType(JsonNode node, String fieldName) {
        return enumValue(ContributionType.class, JsonNodes.text(node, fieldName));
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
}
