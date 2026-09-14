package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.Browser;

import java.util.Set;

public interface BankProductScraper {

    String providerCode();

    Set<String> allowedDomains();

    ScrapedProduct scrape(Browser browser, String productName, int timeoutMillis);
}
