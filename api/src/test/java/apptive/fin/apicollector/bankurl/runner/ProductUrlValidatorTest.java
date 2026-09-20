package apptive.fin.apicollector.bankurl.runner;

import apptive.fin.apicollector.bankurl.ScrapeStatus;
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
    void rejectsGenericCandidateName() {
        ValidationOutcome outcome = validator.validate(
                "하나의정기예금",
                "예금",
                "https://www.kebhana.com/product",
                Set.of("kebhana.com")
        );

        assertThat(outcome.error()).isEqualTo("candidate name is generic product category");
    }

    @Test
    void rejectsDifferentKbankProductCandidate() {
        ValidationOutcome outcome = validator.validate(
                "마이키즈 적금",
                "궁금한 적금",
                "https://www.kbanknow.com/product",
                Set.of("kbanknow.com")
        );

        assertThat(outcome.status()).isEqualTo(ScrapeStatus.FAIL);
        assertThat(outcome.error()).isEqualTo("candidate name similarity is too low");
    }

    @Test
    void warnsWhenOfficialCandidateLooksLikeARenamedProduct() {
        ValidationOutcome outcome = validator.validate(
                "Sh월복리자유적금",
                "Sh주거래우대 월복리적금",
                "https://www.suhyup-bank.com/product",
                Set.of("suhyup-bank.com")
        );

        assertThat(outcome.status()).isEqualTo(ScrapeStatus.WARN);
    }

    @Test
    void rejectsConflictingProductVariant() {
        ValidationOutcome outcome = validator.validate(
                "JB 다이렉트적금(자유적립식)",
                "JB 다이렉트적금(정액적립식)",
                "https://m.jbbank.co.kr/product",
                Set.of("jbbank.co.kr")
        );

        assertThat(outcome.error()).isEqualTo("candidate product variant conflicts with target");
    }

    private static Stream<Arguments> invalidUrls() {
        return Stream.of(
                Arguments.of("/product", "product_url is not absolute http(s)"),
                Arguments.of("ftp://www.kebhana.com/product", "product_url is not absolute http(s)"),
                Arguments.of("https://www.kebhana.com/" + "a".repeat(500), "product_url exceeds database limit")
        );
    }
}
