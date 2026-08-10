package apptive.fin.apicollector.bankurl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ProductUrlValidatorTest {

    private final ProductUrlValidator validator = new ProductUrlValidator();

    @Test
    void rejectsUrlOutsideProviderDomain() {
        ValidationOutcome outcome = validator.validate(
                "하나의정기예금",
                "하나의정기예금",
                "https://example.com/product",
                Set.of("kebhana.com")
        );

        assertThat(outcome).isEqualTo(new ValidationOutcome(
                ScrapeStatus.FAIL,
                1.0,
                "product_url domain mismatch"
        ));
    }

    @ParameterizedTest
    @MethodSource("invalidUrls")
    void rejectsInvalidUrls(String url, String expectedError) {
        ValidationOutcome outcome = validator.validate(
                "하나의정기예금",
                "하나의정기예금",
                url,
                Set.of("kebhana.com")
        );

        assertThat(outcome.error()).isEqualTo(expectedError);
    }

    @Test
    void compactContainmentPassesWithFullSimilarity() {
        ValidationOutcome outcome = validator.validate(
                "내맘적금",
                "(내맘) 적금",
                "https://www.kebhana.com/product",
                Set.of("kebhana.com")
        );

        assertThat(outcome).isEqualTo(new ValidationOutcome(ScrapeStatus.PASS, 1.0, ""));
    }

    @Test
    void rejectsGenericProductTitle() {
        ValidationOutcome outcome = validator.validate(
                "하나의정기예금",
                "예금",
                "https://www.kebhana.com/product",
                Set.of("kebhana.com")
        );

        assertThat(outcome.error()).isEqualTo("title is generic product category");
    }

    private static Stream<Arguments> invalidUrls() {
        return Stream.of(
                Arguments.of("/product", "product_url is not absolute http(s)"),
                Arguments.of("ftp://www.kebhana.com/product", "product_url is not absolute http(s)"),
                Arguments.of("https://www.kebhana.com/" + "a".repeat(500), "product_url exceeds database limit")
        );
    }
}
