package apptive.fin.apicollector.bankurl.runner;

import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaywrightBankScrapeWorkerFactoryTest {

    @Test
    void classifiesClosedDriverConnectionAsTransportFailure() {
        boolean result = PlaywrightBankScrapeWorkerFactory.isTransportFailure(
                new PlaywrightException("Failed to read message from driver, pipe closed.")
        );

        assertThat(result).isTrue();
    }

    @Test
    void doesNotClassifyPlaywrightTimeoutAsTransportFailure() {
        boolean result = PlaywrightBankScrapeWorkerFactory.isTransportFailure(
                new TimeoutError("Timeout 30000ms exceeded")
        );

        assertThat(result).isFalse();
    }

    @Test
    void classifiesClosedBrowserAsTransportFailure() {
        boolean result = PlaywrightBankScrapeWorkerFactory.isTransportFailure(
                new PlaywrightException("Browser has been closed")
        );

        assertThat(result).isTrue();
    }
}
