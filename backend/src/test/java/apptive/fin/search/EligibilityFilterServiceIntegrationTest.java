package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.service.EligibilityFilterService;
import apptive.fin.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(
        scripts = "/sql/eligibility-filter-products.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup-product-fixtures.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class EligibilityFilterServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private EligibilityFilterService eligibilityFilterService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 만27세_무주택자이면_모든_상품이_노출된다() {
        SearchRequestDto request = createRequest(27, true, List.of());

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(productCodes(result))
                .containsExactlyInAnyOrder(
                        "TEST_COMMON",
                        "TEST_TENURE_REQUIRED",
                        "TEST_HOMELESS_ONLY",
                        "TEST_YOUTH_PREFERENTIAL",
                        "TEST_MILITARY_EXTENSION"
                );
    }

    @Test
    void 미취업이면_근속기간이_요구되는_상품은_제외된다() {
        SearchRequestDto request = createRequest(
                27,
                true,
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 18L))
        );

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(productCodes(result))
                .containsExactlyInAnyOrder(
                        "TEST_COMMON",
                        "TEST_HOMELESS_ONLY",
                        "TEST_YOUTH_PREFERENTIAL",
                        "TEST_MILITARY_EXTENSION"
                );
        assertThat(productCodes(result)).doesNotContain("TEST_TENURE_REQUIRED");
    }

    @Test
    void 나이를_충족하지_못하면_빈_목록을_반환한다() {
        SearchRequestDto request = createRequest(14, true, List.of());

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(result).isEmpty();
    }

    @Test
    void 주택소유자이면_무주택자만_가능한_상품은_제외된다() {
        SearchRequestDto request = createRequest(27, false, List.of());

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(productCodes(result))
                .containsExactlyInAnyOrder(
                        "TEST_COMMON",
                        "TEST_TENURE_REQUIRED",
                        "TEST_YOUTH_PREFERENTIAL",
                        "TEST_MILITARY_EXTENSION"
                );
        assertThat(productCodes(result)).doesNotContain("TEST_HOMELESS_ONLY");
    }

    @Test
    void 만30세이면_청년우대형_상품은_제외된다() {
        SearchRequestDto request = createRequest(30, true, List.of());

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(productCodes(result))
                .containsExactlyInAnyOrder(
                        "TEST_COMMON",
                        "TEST_TENURE_REQUIRED",
                        "TEST_HOMELESS_ONLY",
                        "TEST_MILITARY_EXTENSION"
                );
        assertThat(productCodes(result)).doesNotContain("TEST_YOUTH_PREFERENTIAL");
    }

    @Test
    void 만36세_일반유저이면_병역연령확장_상품도_제외된다() {
        SearchRequestDto request = createRequest(36, true, List.of());

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(result).isEmpty();
    }

    @Test
    void 만36세_군복무_신분이어도_연령상한을_확장하지_않는다() {
        SearchRequestDto request = createRequest(
                36,
                true,
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 21L))
        );

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(result).isEmpty();
    }

    @Test
    void 만40세_군복무_신분이면_병역연령확장_상품도_제외된다() {
        SearchRequestDto request = createRequest(
                40,
                true,
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 21L))
        );

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(result).isEmpty();
    }

    @Test
    void high_confidence_required_identity_matches_selected_identity() {
        insertRequiredKeyword("TEST_COMMON", "STATUS_MILITARY", "REQUIRE", "HIGH");
        SearchRequestDto request = createRequest(
                27,
                true,
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 21L))
        );

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(productCodes(result)).contains("TEST_COMMON");
    }

    @Test
    void high_confidence_required_identity_excludes_unmatched_identity() {
        insertRequiredKeyword("TEST_COMMON", "STATUS_MILITARY", "REQUIRE", "HIGH");
        SearchRequestDto request = createRequest(
                27,
                true,
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 20L))
        );

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(productCodes(result)).doesNotContain("TEST_COMMON");
    }

    @Test
    void high_confidence_required_identity_excludes_when_identity_not_selected() {
        insertRequiredKeyword("TEST_COMMON", "STATUS_MILITARY", "REQUIRE", "HIGH");
        SearchRequestDto request = createRequest(27, true, List.of());

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(productCodes(result)).doesNotContain("TEST_COMMON");
    }

    @Test
    void high_confidence_excluded_identity_excludes_matched_identity() {
        insertRequiredKeyword("TEST_COMMON", "STATUS_UNEMPLOYED", "EXCLUDE", "HIGH");
        SearchRequestDto request = createRequest(
                27,
                true,
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 18L))
        );

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(productCodes(result)).doesNotContain("TEST_COMMON");
    }

    @Test
    void medium_confidence_required_identity_is_not_used_for_filtering() {
        insertRequiredKeyword("TEST_COMMON", "STATUS_MILITARY", "REQUIRE", "MEDIUM");
        SearchRequestDto request = createRequest(27, true, List.of());

        List<Product> result = eligibilityFilterService.filterEligible(request);

        assertThat(productCodes(result)).contains("TEST_COMMON");
    }

    private SearchRequestDto createRequest(int age, Boolean isHomeless, List<OptionRequestDto> options) {
        return new SearchRequestDto(
                options,
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(age),
                        null,
                        null,
                        null,
                        null,
                        null,
                        isHomeless,
                        null,
                        null,
                        null,
                        List.of()
                )
        );
    }

    private List<String> productCodes(List<Product> products) {
        return products.stream()
                .map(Product::getProductCode)
                .toList();
    }

    private void insertRequiredKeyword(String productCode, String keywordCode, String effect, String confidence) {
        jdbcTemplate.update("""
                INSERT INTO product_property_required_keyword (
                    product_property_id, keyword_code, effect, confidence
                )
                SELECT pp.id, ?, ?, ?
                FROM product_properties pp
                JOIN product p ON p.id = pp.product_id
                WHERE p.product_code = ?
                """, keywordCode, effect, confidence, productCode);
    }
}
