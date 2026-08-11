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

    // 양쪽에 고유 잔여가 남으면 다른 상품이다. 실제 수집 96건에서 아래 3건만 여기 걸렸다.
    @ParameterizedTest
    @MethodSource("contradictingNames")
    void detectsDifferentProductsByLeftoverOnBothSides(String expected, String title) {
        assertThat(ProductUrlValidator.namesContradict(expected, title)).isTrue();
    }

    static Stream<Arguments> contradictingNames() {
        return Stream.of(
                // 잔여 자유 / 정액 — 전북은행이 유형별로 다른 상세 페이지를 준다
                Arguments.of("JB 다이렉트적금(자유적립식)", "JB다이렉트적금(정액적립식)"),
                // 은행이 상품명을 바꾼 케이스
                Arguments.of("Sh월복리자유적금", "Sh주거래우대 월복리적금"),
                Arguments.of("BNK 위더스자유적금", "BNK더조은자유적금")
        );
    }

    // 한쪽만 잔여가 남으면 축약이다. 은행도 FSS 도 서로 줄여 쓴다.
    @ParameterizedTest
    @MethodSource("abbreviatedNames")
    void treatsOneSidedLeftoverAsAbbreviation(String expected, String title) {
        assertThat(ProductUrlValidator.namesContradict(expected, title)).isFalse();
    }

    static Stream<Arguments> abbreviatedNames() {
        return Stream.of(
                Arguments.of("IBK더굴리기통장(실세금리정기예금)", "IBK더굴리기통장(정기예금)"),  // 은행이 줄임
                Arguments.of("KB국민프리미엄적금(정액)", "KB국민프리미엄적금(정액적립식)"),      // FSS 가 줄임
                Arguments.of("IBK D-day적금(자유적립식)", "IBK D-day적금"),                 // 은행 제목에 유형 없음
                Arguments.of("헤이(Hey)적금 (정액적립식)", "헤이(Hey)적금"),
                Arguments.of("제주Dream 정기예금 (개인/만기 지급식)", "제주Dream 정기예금"),
                Arguments.of("JB 다이렉트적금(자유적립식)", "JB다이렉트적금(자유적립식)"),      // 동일
                Arguments.of("JB 123 정기예금 (만기일시지급식)", "JB 1・2・3 정기예금"),        // 기호만 다름
                Arguments.of("아무이름", ""),
                Arguments.of(null, "아무제목")
        );
    }

    @Test
    void rejectsVariantMismatchEvenWhenSimilarityIsPerfect() {
        // normalize() 가 괄호를 지워 유사도는 1.0 이 된다. 그래도 다른 상품이므로 통과시키면 안 된다.
        ValidationOutcome outcome = validator.validate(
                "JB 다이렉트적금(자유적립식)",
                "JB다이렉트적금(정액적립식)",
                "https://m.jbbank.co.kr:8543/JBbank.act?TRGT_URL=P_M_SID_MALL_DTL",
                Set.of("jbbank.co.kr")
        );

        assertThat(outcome.status()).isEqualTo(ScrapeStatus.FAIL);
        assertThat(outcome.error()).contains("different product variant");
    }

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
