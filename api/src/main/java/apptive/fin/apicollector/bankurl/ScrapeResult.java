package apptive.fin.apicollector.bankurl;

public record ScrapeResult(
        BankProductUrlTarget target,
        String scraper,
        ScrapeStatus status,
        String title,
        String productUrl,
        double similarity,
        String error,
        long elapsedMillis,
        int attempts
) {
}
