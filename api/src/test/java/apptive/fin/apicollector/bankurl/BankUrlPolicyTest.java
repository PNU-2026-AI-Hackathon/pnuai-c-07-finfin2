package apptive.fin.apicollector.bankurl;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BankUrlPolicyTest {

    @Test
    void rejectsUrlOutsideProviderDomain() {
        var error = BankUrlPolicy.validationError(
                "http://127.0.0.1/internal",
                Set.of("example.com")
        );

        assertThat(error).contains("product_url domain mismatch");
    }

    @Test
    void appliesDatabaseLengthOnlyToStoredProductUrl() {
        String longProviderUrl = "https://example.com/" + "a".repeat(501);

        assertThat(BankUrlPolicy.navigationError(longProviderUrl, Set.of("example.com"))).isEmpty();
        assertThat(BankUrlPolicy.validationError(longProviderUrl, Set.of("example.com")))
                .contains("product_url exceeds database limit");
    }
}
