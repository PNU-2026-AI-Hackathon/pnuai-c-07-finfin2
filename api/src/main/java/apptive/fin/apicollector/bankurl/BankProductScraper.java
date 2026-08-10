package apptive.fin.apicollector.bankurl;

import com.microsoft.playwright.Browser;

import java.util.Set;

public interface BankProductScraper {

    String providerCode();

    Set<String> allowedDomains();

    ScrapedProduct scrape(Browser browser, String productName, int timeoutMillis);
}
