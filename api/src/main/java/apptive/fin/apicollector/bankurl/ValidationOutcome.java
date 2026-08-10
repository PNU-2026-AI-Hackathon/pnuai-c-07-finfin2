package apptive.fin.apicollector.bankurl;

public record ValidationOutcome(
        ScrapeStatus status,
        double similarity,
        String error
) {
}
