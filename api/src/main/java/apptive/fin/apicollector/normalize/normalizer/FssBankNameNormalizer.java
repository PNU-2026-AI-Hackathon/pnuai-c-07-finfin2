package apptive.fin.apicollector.normalize.normalizer;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FssBankNameNormalizer {

    private static final Map<String, String> DISPLAY_NAMES = Map.ofEntries(
            Map.entry("0010001", "우리은행"),
            Map.entry("0010002", "SC제일은행"),
            Map.entry("0010016", "iM뱅크"),
            Map.entry("0010017", "부산은행"),
            Map.entry("0010019", "광주은행"),
            Map.entry("0010020", "제주은행"),
            Map.entry("0010022", "전북은행"),
            Map.entry("0010024", "경남은행"),
            Map.entry("0010026", "IBK기업은행"),
            Map.entry("0010030", "KDB산업은행"),
            Map.entry("0010927", "KB국민은행"),
            Map.entry("0011625", "신한은행"),
            Map.entry("0013175", "NH농협은행"),
            Map.entry("0013909", "하나은행"),
            Map.entry("0014674", "케이뱅크"),
            Map.entry("0014807", "Sh수협은행"),
            Map.entry("0015130", "카카오뱅크"),
            Map.entry("0017801", "토스뱅크")
    );

    public String normalize(String code, String rawName) {
        if (code == null) {
            return rawName;
        }
        return DISPLAY_NAMES.getOrDefault(code, rawName);
    }
}
