package apptive.fin.apicollector.bankurl;

import apptive.fin.apicollector.bankurl.scraper.BankProductScraper;
import apptive.fin.apicollector.bankurl.scraper.ScrapedProduct;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.springframework.stereotype.Component;

@Component
class PlaywrightBankScrapeWorkerFactory implements BankScrapeWorkerFactory {

    private final BankProductUrlProperties properties;

    PlaywrightBankScrapeWorkerFactory(BankProductUrlProperties properties) {
        this.properties = properties;
    }

    @Override
    public BankScrapeWorker create() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
        );
        return new PlaywrightWorker(playwright, browser, properties.effectiveTimeoutSeconds() * 1_000);
    }

    private record PlaywrightWorker(
            Playwright playwright,
            Browser browser,
            int timeoutMillis
    ) implements BankScrapeWorker {

        @Override
        public ScrapedProduct scrape(BankProductScraper scraper, BankProductUrlTarget target) {
            return scraper.scrape(browser, target.productName(), timeoutMillis);
        }

        @Override
        public void close() {
            browser.close();
            playwright.close();
        }
    }
}
