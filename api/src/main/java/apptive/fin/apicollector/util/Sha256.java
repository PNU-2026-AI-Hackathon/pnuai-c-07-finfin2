package apptive.fin.apicollector.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** SHA-256 해시를 소문자 hex 문자열로 계산하는 공용 유틸. */
public final class Sha256 {

    private Sha256() {
    }

    public static String hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to calculate SHA-256 hash", e);
        }
    }
}
