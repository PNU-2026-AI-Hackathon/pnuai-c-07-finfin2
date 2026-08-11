package apptive.fin.apicollector.bankurl.runner;

import apptive.fin.apicollector.bankurl.BankProductUrlProperties;
import apptive.fin.apicollector.bankurl.BankProductUrlTarget;
import apptive.fin.apicollector.bankurl.scraper.BankProductScraper;
import apptive.fin.apicollector.bankurl.scraper.ScrapedProduct;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class PlaywrightBankScrapeWorkerFactory implements BankScrapeWorkerFactory {

    private final BankProductUrlProperties properties;

    PlaywrightBankScrapeWorkerFactory(BankProductUrlProperties properties) {
        this.properties = properties;
    }

    @Override
    public BankScrapeWorker create() {
        Playwright playwright = Playwright.create();
        try {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true)
                            // --disable-dev-shm-usage: 컨테이너 기본 /dev/shm(64MB)로는 Chromium 이 죽는다.
                            // --disable-gpu: GPU 없는 서버에서 초기화 시도를 피한다.
                            //   메모리 목적이 아니다. 실측상 이 플래그를 줘도 gpu-process 는 그대로 뜬다(431MB/8워커).
                            .setArgs(List.of("--disable-gpu", "--disable-dev-shm-usage"))
            );
            return new PlaywrightWorker(playwright, browser, properties.effectiveTimeoutSeconds() * 1_000);
        } catch (Throwable failure) {
            // 브라우저를 못 띄웠으면 드라이버 프로세스도 함께 정리한다. 안 그러면 워커 수만큼 샌다.
            // Error(AssertionError, OOM 등)에서도 정리해야 하므로 Throwable 을 잡는다.
            try {
                playwright.close();
            } catch (Throwable cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
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
        public boolean isAlive() {
            return browser.isConnected();
        }

        @Override
        public void close() {
            // browser.close() 가 실패해도(Chromium 비정상 종료 등) 드라이버는 반드시 닫는다.
            // finally 로 감싸면 playwright.close() 가 던질 때 원래 원인이 소실되는데,
            // drain() 이 이 예외를 workerFailures 에 담으므로 진단이 막힌다.
            try {
                browser.close();
            } catch (RuntimeException browserFailure) {
                try {
                    playwright.close();
                } catch (RuntimeException driverFailure) {
                    browserFailure.addSuppressed(driverFailure);
                }
                throw browserFailure;
            }
            playwright.close();
        }
    }
}
