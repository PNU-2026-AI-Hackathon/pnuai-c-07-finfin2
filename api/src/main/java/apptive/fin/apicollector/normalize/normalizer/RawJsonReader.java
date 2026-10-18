package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.raw.ProductRaw;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * 소스별 raw JSON 파싱과 필수값 검증을 공통화한다.
 *
 * <p>예외 메시지에 들어갈 소스 라벨은 normalizer마다 고정 상수이므로 매 호출 인자로 넘기지 않고
 * 생성자에 한 번만 바인딩한다. normalizer가 각자 자신의 라벨로 인스턴스를 보유한다.
 */
public class RawJsonReader {

    private final ObjectMapper objectMapper;
    private final String sourceLabel;

    public RawJsonReader(ObjectMapper objectMapper, String sourceLabel) {
        this.objectMapper = objectMapper;
        this.sourceLabel = sourceLabel;
    }

    public JsonNode read(ProductRaw rawProduct) {
        try {
            return objectMapper.readTree(rawProduct.getRawJson());
        }
        catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to parse " + sourceLabel + " raw JSON. rawId=" + rawProduct.getId(), e);
        }
    }

    public String required(String value, String fieldName, ProductRaw rawProduct) {
        if (value == null) {
            throw new IllegalArgumentException(
                    sourceLabel + " " + fieldName + " is required. rawId=" + rawProduct.getId());
        }
        return value;
    }
}
