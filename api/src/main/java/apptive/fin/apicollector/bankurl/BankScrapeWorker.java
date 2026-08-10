package apptive.fin.apicollector.bankurl;

interface BankScrapeWorker extends AutoCloseable {

    ScrapedProduct scrape(BankProductScraper scraper, BankProductUrlTarget target);

    @Override
    void close();
}
