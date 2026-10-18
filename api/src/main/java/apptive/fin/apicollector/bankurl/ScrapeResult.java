package apptive.fin.apicollector.bankurl;

public record ScrapeResult(
        BankProductUrlTarget target,
        String scraper,
        ScrapeStatus status,
        String candidateName,
        String productUrl,
        double similarity,
        String error,
        long elapsedMillis,
        int attempts
) {
}
