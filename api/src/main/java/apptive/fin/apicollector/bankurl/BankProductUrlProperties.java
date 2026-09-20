package apptive.fin.apicollector.bankurl;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "collector.bank-product-url")
public record BankProductUrlProperties(
        boolean enabled,
        int concurrency,
        int timeoutSeconds,
        int retries
) {

    public int effectiveConcurrency() {
        return Math.max(1, concurrency);
    }

    public int effectiveTimeoutSeconds() {
        return Math.max(1, timeoutSeconds);
    }

    public int effectiveRetries() {
        return Math.max(0, retries);
    }
}
