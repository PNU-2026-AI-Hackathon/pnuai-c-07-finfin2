package apptive.fin.apicollector.bankurl.scrape;

import apptive.fin.apicollector.bankurl.ScrapeStatus;

record ValidationOutcome(
        ScrapeStatus status,
        double similarity,
        String error
) {
}
