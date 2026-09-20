package apptive.fin.provider;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BankCategoryRegistry {

    private static final String UNKNOWN_CATEGORY = "기타";

    private static final Map<String, String> BY_CODE = Map.ofEntries(
            Map.entry("0010001", "시중"),
            Map.entry("0010002", "시중"),
            Map.entry("0010016", "시중"),
            Map.entry("0010017", "지방"),
            Map.entry("0010019", "지방"),
            Map.entry("0010020", "지방"),
            Map.entry("0010022", "지방"),
            Map.entry("0010024", "지방"),
            Map.entry("0010026", "특수"),
            Map.entry("0010030", "특수"),
            Map.entry("0010927", "시중"),
            Map.entry("0011625", "시중"),
            Map.entry("0013175", "특수"),
            Map.entry("0013909", "시중"),
            Map.entry("0014674", "인터넷"),
            Map.entry("0014807", "특수"),
            Map.entry("0015130", "인터넷"),
            Map.entry("0017801", "인터넷")
    );

    public String categoryOrFallback(String code) {
        if (code == null) {
            return UNKNOWN_CATEGORY;
        }
        return BY_CODE.getOrDefault(code, UNKNOWN_CATEGORY);
    }
}
