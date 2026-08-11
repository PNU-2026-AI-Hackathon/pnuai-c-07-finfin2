package apptive.fin.apicollector.bankurl.runner;

import apptive.fin.apicollector.bankurl.ScrapeStatus;
import apptive.fin.apicollector.bankurl.scraper.ProductNameSimilarity;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

final class ProductUrlValidator {

    private final ProductNameSimilarity similarity = new ProductNameSimilarity();

    ValidationOutcome validate(
            String expectedName,
            String actualTitle,
            String productUrl,
            Set<String> allowedDomains
    ) {
        double score = similarity.score(expectedName, actualTitle);
        URI uri = parseHttpUri(productUrl);
        if (uri == null) {
            return fail(score, "product_url is not absolute http(s)");
        }
        if (productUrl.length() > 500) {
            return fail(score, "product_url exceeds database limit");
        }
        if (!domainMatches(uri.getHost(), allowedDomains)) {
            return fail(score, "product_url domain mismatch");
        }
        if (isGenericProductName(actualTitle)) {
            return fail(score, "title is generic product category");
        }
        if (namesContradict(expectedName, actualTitle)) {
            // 유사도보다 먼저 본다. 유사도는 괄호 안 내용을 지우고 비교하므로
            // 자유적립식과 정액적립식을 1.0 으로 같다고 판정한다.
            return fail(score, "title names a different product variant");
        }

        String expectedCompact = compact(expectedName);
        String titleCompact = compact(actualTitle);
        if (!expectedCompact.isEmpty()
                && (expectedCompact.equals(titleCompact) || titleCompact.contains(expectedCompact))) {
            return new ValidationOutcome(ScrapeStatus.PASS, 1.0, "");
        }
        if (score < 0.55) {
            return fail(score, "title similarity is too low");
        }
        if (score < 0.80) {
            return new ValidationOutcome(ScrapeStatus.WARN, score, "title similarity is low");
        }
        return new ValidationOutcome(ScrapeStatus.PASS, score, "");
    }

    /**
     * 두 이름이 서로 다른 상품을 가리키는지. 공통 접두·접미를 벗겨내고 양쪽에 고유 잔여가 남으면 다른 상품이다.
     * <p>
     * 한쪽만 남으면 축약 관계이므로 같은 상품으로 본다 — 은행도 FSS 도 서로 줄여 쓴다
     * ({@code 실세금리정기예금} ↔ {@code 정기예금}, {@code 정액} ↔ {@code 정액적립식}).
     * 구분자나 어휘 사전에 기대지 않으므로 은행이 괄호 없이 {@code JB다이렉트적금 자유형} 처럼 써도 동작한다.
     */
    static boolean namesContradict(String expectedName, String actualTitle) {
        String expected = compact(expectedName);
        String actual = compact(actualTitle);
        if (expected.isEmpty() || actual.isEmpty()) {
            return false;
        }
        int prefix = 0;
        while (prefix < expected.length() && prefix < actual.length()
                && expected.charAt(prefix) == actual.charAt(prefix)) {
            prefix++;
        }
        int suffix = 0;
        while (suffix < expected.length() - prefix && suffix < actual.length() - prefix
                && expected.charAt(expected.length() - 1 - suffix) == actual.charAt(actual.length() - 1 - suffix)) {
            suffix++;
        }
        return expected.length() - prefix - suffix > 0 && actual.length() - prefix - suffix > 0;
    }

    private URI parseHttpUri(String value) {
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

    private boolean domainMatches(String hostname, Set<String> allowedDomains) {
        if (hostname == null || allowedDomains == null) {
            return false;
        }
        String normalizedHost = hostname.toLowerCase(Locale.ROOT);
        return allowedDomains.stream()
                .map(domain -> domain.toLowerCase(Locale.ROOT))
                .anyMatch(domain -> normalizedHost.equals(domain) || normalizedHost.endsWith("." + domain));
    }

    private boolean isGenericProductName(String title) {
        return Set.of("예금", "적금", "통장", "deposit", "saving", "savings")
                .contains(compact(title));
    }

    private static String compact(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9a-zA-Z가-힣]", "")
                .toLowerCase(Locale.ROOT);
    }

    private ValidationOutcome fail(double score, String error) {
        return new ValidationOutcome(ScrapeStatus.FAIL, score, error);
    }
}
