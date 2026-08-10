package apptive.fin.apicollector.bankurl.runner;

import apptive.fin.apicollector.bankurl.ScrapeStatus;

record ValidationOutcome(
        ScrapeStatus status,
        double similarity,
        String error
) {
}
