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

    private String compact(String value) {
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
