package apptive.fin.apicollector.util;

import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;

/**
 * JsonNode 필드에서 값을 안전하게 꺼내는 공용 헬퍼.
 *
 * <p>{@link #text}는 값을 미리 trim/공백처리해서 반환하므로, 이후 숫자 파싱 헬퍼는
 * 별도 trim 없이 콤마만 제거해도 동일하게 동작한다.
 */
public final class JsonNodes {

    private JsonNodes() {
    }

    public static String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String text(JsonNode node, String fieldName) {
        JsonNode value = node.path(fieldName);
        if (value == null || value.isMissingNode() || value.isNull()) {
            return null;
        }
        return blankToNull(value.asString(null));
    }

    public static Integer integer(JsonNode node, String fieldName) {
        String value = text(node, fieldName);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.replace(",", "").trim());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    /** 0을 유효한 값으로 그대로 반환한다. */
    public static Long longValue(JsonNode node, String fieldName) {
        return parseLong(node, fieldName, false);
    }

    /** 0은 "미설정"으로 보고 null을 반환한다(FSS 등 "0=미설정" 필드 처리). */
    public static Long longValueOrNullIfZero(JsonNode node, String fieldName) {
        return parseLong(node, fieldName, true);
    }

    private static Long parseLong(JsonNode node, String fieldName, boolean zeroAsNull) {
        String value = text(node, fieldName);
        if (value == null) {
            return null;
        }
        try {
            long parsed = Long.parseLong(value.replace(",", "").trim());
            return (zeroAsNull && parsed == 0L) ? null : parsed;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static BigDecimal decimal(JsonNode node, String fieldName) {
        String value = text(node, fieldName);
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value.replace(",", "").trim());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean bool(JsonNode node, String fieldName) {
        String value = text(node, fieldName);
        return value != null && Boolean.parseBoolean(value);
    }
}
