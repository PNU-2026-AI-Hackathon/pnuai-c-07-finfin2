package apptive.fin.apicollector.bankurl;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public final class BankUrlPolicy {

    private BankUrlPolicy() {
    }

    public static Optional<String> validationError(String url, Set<String> allowedDomains) {
        Optional<String> navigationError = navigationError(url, allowedDomains);
        if (navigationError.isPresent()) {
            return navigationError;
        }
        if (url.length() > 500) {
            return Optional.of("product_url exceeds database limit");
        }
        return Optional.empty();
    }

    public static Optional<String> navigationError(String url, Set<String> allowedDomains) {
        URI uri = parseHttpUri(url);
        if (uri == null) {
            return Optional.of("product_url is not absolute http(s)");
        }
        if (!domainMatches(uri.getHost(), allowedDomains)) {
            return Optional.of("product_url domain mismatch");
        }
        return Optional.empty();
    }

    private static URI parseHttpUri(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            URI uri = URI.create(value);
            if (("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null) {
                return uri;
            }
        } catch (IllegalArgumentException ignored) {
            // Invalid values are reported through the validation result.
        }
        return null;
    }

    private static boolean domainMatches(String hostname, Set<String> allowedDomains) {
        if (hostname == null || allowedDomains == null) {
            return false;
        }
        String normalizedHost = hostname.toLowerCase(Locale.ROOT);
        return allowedDomains.stream()
                .map(domain -> domain.toLowerCase(Locale.ROOT))
                .anyMatch(domain -> normalizedHost.equals(domain) || normalizedHost.endsWith("." + domain));
    }
}
