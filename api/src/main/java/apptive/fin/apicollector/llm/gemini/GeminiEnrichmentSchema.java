package apptive.fin.apicollector.llm.gemini;

import apptive.fin.apicollector.product.KeywordValueEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.ObjectMapper;

/** Gemini structured-output 응답 JSON Schema를 구성한다. */
@Component
@RequiredArgsConstructor
public class GeminiEnrichmentSchema {

    private final ObjectMapper objectMapper;

    public ObjectNode build() {
        ObjectNode schema = objectMapper.createObjectNode();
        schema.put("type", "object");

        ObjectNode properties = objectMapper.createObjectNode();
        properties.set("summaryContent", stringSchema("상품 설명을 사용자에게 보여줄 수 있게 한국어로 간결히 정리한 내용"));
        properties.set("keywords", stringArraySchema("허용된 keyword enum 목록"));
        properties.set("minMonthlyLimit", integerSchema("적금(SAVING)의 명시된 최소 월 납입액. 정기예금(DEPOSIT) 가입금액은 넣지 않는다. 없으면 null"));
        properties.set("maxMonthlyLimit", integerSchema("적금(SAVING)의 명시된 유한 최대 월 납입한도. 정기예금(DEPOSIT) 가입한도는 넣지 않는다. 없거나 제한 없음이면 null"));
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
        // 모든 property가 required이므로 이름을 재나열하지 않고 properties에서 파생한다
        // (필드 추가 시 required 누락으로 조용히 drift 되는 것을 방지).
        ArrayNode required = objectMapper.createArrayNode();
        properties.propertyNames().forEach(required::add);
        schema.set("required", required);
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
                BANK_* 8개 키워드로 정확히 표현할 수 없지만 조건별 가산금리가 명시된 우대조건은 BANK_ETC로 매핑한다.
                단, 요구불평잔, 평균잔액, 예금/적금 보유, 특정 상품 만기/해지 고객, 추천/쿠폰/이벤트처럼 우대금리 조건으로 보기 어려운 항목은 여전히 제외한다.
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
                BANK_AGE=나이/연령 조건,
                BANK_ETC=위 8개 중 어디에도 정확히 해당하지 않지만 조건별 가산금리가 명시된 기타 우대조건.
                위 8개 중 하나에 명확히 해당하면 그 키워드를 쓰고, 그렇지 않을 때만 BANK_ETC를 쓴다.
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
            if (keyword.isRequired()) {
                array.add(keyword.name());
            }
        }
        return array;
    }

    private ArrayNode preferentialRateKeywordEnumValues() {
        ArrayNode array = objectMapper.createArrayNode();
        for (KeywordValueEnum keyword : KeywordValueEnum.values()) {
            if (keyword.isPreferentialRate()) {
                array.add(keyword.name());
            }
        }
        return array;
    }
}
