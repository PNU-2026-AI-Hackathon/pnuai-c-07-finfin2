package apptive.fin.apicollector.client.fss;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 한 상품의 FSS 옵션 목록을 결정적(canonical) 순서로 정렬한다.
 *
 * <p>FSS API는 동일 상품의 옵션 행(만기 tier 등)을 fetch 시점에 따라 다른 순서로
 * 내려줄 수 있는데, 옵션 배열의 순서 자체는 의미가 없다(다운스트림에서 자연키
 * PropertyKey 로 order-independent 하게 reconcile). 그런데 raw JSON 을 그대로
 * 해싱해 변경 감지를 하므로, 정렬하지 않으면 내용이 같아도 해시가 변경되어
 * 불필요한 UPDATED/재정규화/LLM 재호출이 발생한다.
 *
 * <p>이 정렬은 raw JSON 을 canonical 하게 만들기 위한 별도 책임이며, HTTP 수집을
 * 담당하는 {@link FssClient} 와 분리한다.
 */
@Component
public class FssOptionOrdering {

    private final ObjectMapper objectMapper;

    public FssOptionOrdering(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 옵션을 구분하는 필드(save_trm, intr_rate_type, rsrv_type)로 정렬하고,
     * 완전한 결정성을 위해 마지막에 직렬화 문자열로 tie-break 한다.
     */
    public List<JsonNode> sort(List<JsonNode> options) {
        List<JsonNode> sorted = new ArrayList<>(options);
        sorted.sort(OPTION_COMPARATOR
                .thenComparing(objectMapper::writeValueAsString));
        return sorted;
    }

    private static final Comparator<JsonNode> OPTION_COMPARATOR =
            Comparator.comparing(FssOptionOrdering::saveTrm, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(o -> optionText(o, "intr_rate_type"), Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(o -> optionText(o, "rsrv_type"), Comparator.nullsLast(Comparator.naturalOrder()));

    private static Integer saveTrm(JsonNode option) {
        String value = optionText(option, "save_trm");
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private static String optionText(JsonNode option, String field) {
        JsonNode value = option.path(field);
        if (value.isMissingNode() || value.isNull()) {
            return null;
        }
        return value.asString();
    }
}
