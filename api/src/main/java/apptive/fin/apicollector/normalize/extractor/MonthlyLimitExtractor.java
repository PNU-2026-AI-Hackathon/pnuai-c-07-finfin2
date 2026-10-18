package apptive.fin.apicollector.normalize.extractor;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MonthlyLimitExtractor {

    private static final Pattern MONTHLY_PATTERN = Pattern.compile(
            "(월|매월)\\s*[^\\d]*?(\\d[\\d,]*)\\s*(만\\s*원|원)"
    );
    private static final Pattern LIMIT_PATTERN = Pattern.compile(
            "(한도|최대|상한)\\s*[^\\d]*?(\\d[\\d,]*)\\s*(만\\s*원|원)"
    );
    private static final Pattern MATCHING_PATTERN = Pattern.compile(
            "(\\d[\\d,]*)\\s*(만\\s*원|원)\\s*(매칭|지원|기여)"
    );
    private static final Map<String, Long> MANUAL_LIMITS = new LinkedHashMap<>();

    static {
        MANUAL_LIMITS.put("청년미래적금", 500_000L);
        MANUAL_LIMITS.put("청년내일저축계좌", 100_000L);
        MANUAL_LIMITS.put("장병내일준비적금", null);
        MANUAL_LIMITS.put("경기도 청년 노동자 통장", 100_000L);
        MANUAL_LIMITS.put("서울 희망두배 청년통장", 150_000L);
        MANUAL_LIMITS.put("강원 청년 자산형성사업", 100_000L);
        MANUAL_LIMITS.put("청년주택드림 청약통장", 1_000_000L);
        MANUAL_LIMITS.put("청년우대형 주택청약종합저축", 500_000L);
    }

    public Long extract(String productName, String supportContent) {
        Long parsed = firstAmount(supportContent, MONTHLY_PATTERN, 2, 3);
        if (parsed != null) {
            return parsed;
        }

        parsed = firstAmount(supportContent, LIMIT_PATTERN, 2, 3);
        if (parsed != null) {
            return parsed;
        }

        parsed = firstAmount(supportContent, MATCHING_PATTERN, 1, 2);
        if (parsed != null) {
            return parsed;
        }

        return manualLimit(productName);
    }

    private Long firstAmount(String value, Pattern pattern, int amountGroup, int unitGroup) {
        if (value == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(value);
        if (!matcher.find()) {
            return null;
        }

        return toWon(matcher.group(amountGroup), matcher.group(unitGroup));
    }

    private Long manualLimit(String productName) {
        if (productName == null) {
            return null;
        }

        for (Map.Entry<String, Long> entry : MANUAL_LIMITS.entrySet()) {
            if (productName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Long toWon(String amount, String unit) {
        long value = Long.parseLong(amount.replace(",", ""));
        if (unit != null && unit.replaceAll("\\s+", "").equals("만원")) {
            return value * 10_000L;
        }
        return value;
    }
}
