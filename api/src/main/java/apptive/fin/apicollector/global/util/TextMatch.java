package apptive.fin.apicollector.global.util;

/** 텍스트에 토큰이 포함되는지 대소문자 무시로 검사하는 공용 헬퍼. */
public final class TextMatch {

    private TextMatch() {
    }

    public static boolean containsAny(String value, String... tokens) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.toLowerCase();
        for (String token : tokens) {
            if (normalized.contains(token.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
