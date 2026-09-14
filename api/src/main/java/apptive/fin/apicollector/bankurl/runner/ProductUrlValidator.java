package apptive.fin.apicollector.bankurl.runner;

import apptive.fin.apicollector.bankurl.BankUrlPolicy;
import apptive.fin.apicollector.bankurl.ScrapeStatus;
import apptive.fin.apicollector.bankurl.scraper.ProductNameSimilarity;

import java.util.Locale;
import java.util.Set;

final class ProductUrlValidator {

    private final ProductNameSimilarity similarity = new ProductNameSimilarity();

    ValidationOutcome validate(
            String expectedName,
            String candidateName,
            String productUrl,
            Set<String> allowedDomains
    ) {
        double score = similarity.score(expectedName, candidateName);
        var urlError = BankUrlPolicy.validationError(productUrl, allowedDomains);
        if (urlError.isPresent()) {
            return fail(score, urlError.get());
        }
        if (isGenericProductName(candidateName)) {
            return fail(score, "candidate name is generic product category");
        }
        if (similarity.hasConflictingVariant(expectedName, candidateName)) {
            return fail(score, "candidate product variant conflicts with target");
        }

        String expectedCompact = compact(expectedName);
        String candidateCompact = compact(candidateName);
        if (!expectedCompact.isEmpty()
                && (expectedCompact.equals(candidateCompact) || candidateCompact.contains(expectedCompact))) {
            return new ValidationOutcome(ScrapeStatus.PASS, 1.0, "");
        }
        if (score < 0.55) {
            return fail(score, "candidate name similarity is too low");
        }
        if (score < 0.80) {
            return new ValidationOutcome(ScrapeStatus.WARN, score, "candidate name similarity is low");
        }
        return new ValidationOutcome(ScrapeStatus.PASS, score, "");
    }

    private boolean isGenericProductName(String candidateName) {
        return Set.of("예금", "적금", "통장", "deposit", "saving", "savings")
                .contains(compact(candidateName));
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
