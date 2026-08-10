package apptive.fin.apicollector.bankurl.scrape;

import com.microsoft.playwright.Browser;
import apptive.fin.apicollector.bankurl.BankProductUrlProperties;
import apptive.fin.apicollector.bankurl.BankProductUrlTarget;
import apptive.fin.apicollector.bankurl.ScrapeResult;
import apptive.fin.apicollector.bankurl.ScrapeStatus;
import apptive.fin.apicollector.bankurl.scraper.BankProductScraper;
import apptive.fin.apicollector.bankurl.scraper.ScrapedProduct;
import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class BankProductUrlScrapeServiceTest {

    @Test
    void retriesTemporaryFailureAndReturnsValidatedResult() {
        AtomicInteger attempts = new AtomicInteger();
        BankProductScraper scraper = new FakeScraper();
        BankScrapeWorkerFactory workerFactory = () -> new BankScrapeWorker() {
            @Override
            public ScrapedProduct scrape(BankProductScraper ignored, BankProductUrlTarget target) {
                if (attempts.incrementAndGet() == 1) {
                    throw new IllegalStateException("temporary");
                }
                return new ScrapedProduct(target.productName(), "https://example.com/product");
            }

            @Override
            public void close() {
            }
        };
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(scraper),
                new BankProductUrlProperties(true, 1, 90, 1),
                workerFactory
        );

        List<ScrapeResult> results = service.scrape(List.of(target("TEST")));

        assertThat(results).singleElement().satisfies(result -> {
            assertThat(result.status()).isEqualTo(ScrapeStatus.PASS);
            assertThat(result.productUrl()).isEqualTo("https://example.com/product");
            assertThat(result.attempts()).isEqualTo(2);
        });
    }

    @Test
    void unsupportedProviderIsReportedWithoutScrapeAttempt() {
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(new FakeScraper()),
                new BankProductUrlProperties(true, 1, 90, 0),
                () -> new BankScrapeWorker() {
                    @Override
                    public ScrapedProduct scrape(BankProductScraper scraper, BankProductUrlTarget target) {
                        throw new AssertionError("must not scrape unsupported provider");
                    }

                    @Override
                    public void close() {
                    }
                }
        );

        ScrapeResult result = service.scrape(List.of(target("UNKNOWN"))).getFirst();

        assertThat(result.status()).isEqualTo(ScrapeStatus.FAIL);
        assertThat(result.attempts()).isZero();
        assertThat(result.error()).contains("no scraper mapped for provider_code");
    }

    private BankProductUrlTarget target(String providerCode) {
        return new BankProductUrlTarget(
                1L, "P1", "테스트정기예금", ProductType.DEPOSIT, providerCode, "테스트은행"
        );
    }

    private static class FakeScraper implements BankProductScraper {

        @Override
        public String providerCode() {
            return "TEST";
        }

        @Override
        public Set<String> allowedDomains() {
            return Set.of("example.com");
        }

        @Override
        public ScrapedProduct scrape(Browser browser, String productName, int timeoutMillis) {
            throw new AssertionError("worker boundary should invoke the fake worker");
        }
    }
}
