package apptive.fin.apicollector.bankurl;

import apptive.fin.apicollector.bankurl.scraper.BankProductScraper;
import apptive.fin.apicollector.bankurl.scraper.ScrapedProduct;

interface BankScrapeWorker extends AutoCloseable {

    ScrapedProduct scrape(BankProductScraper scraper, BankProductUrlTarget target);

    @Override
    void close();
}
