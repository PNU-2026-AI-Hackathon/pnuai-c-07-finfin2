package apptive.fin.search;

import apptive.fin.search.enums.CategoryIdEnum;
import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.ProductSearchResultDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.service.SearchService;
import apptive.fin.support.IntegrationTestSupport;
import apptive.fin.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.offset;

@Sql(
        scripts = "/sql/search-products.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup-product-fixtures.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class SearchServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private SearchService searchService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 기본상황이면_정부와_은행_상품을_적합도와_금리순으로_반환한다() {
        ProductSearchResultDto result = searchService.search(createRequest(50, List.of()), authenticatedUser());

        assertThat(matchNames(result.governmentRanked()))
                .containsExactlyInAnyOrder("청년내일채움공제", "청년우대형 청약통장");
        assertThat(matchNames(result.bankRanked()))
                .containsExactlyInAnyOrder("e-쎄이프 정기예금", "청년우대적금");

        assertThat(result.governmentRanked())
                .allSatisfy(product -> assertThat(product.totalScore()).isCloseTo(100.0, offset(0.0001)));
        assertThat(result.bankRanked())
                .allSatisfy(product -> assertThat(product.totalScore()).isCloseTo(100.0, offset(0.0001)));

        assertThat(rateNames(result.governmentRateRanked()))
                .containsExactly("청년내일채움공제");
        assertThat(rateNames(result.bankRateRanked()))
                .containsExactly("청년우대적금", "e-쎄이프 정기예금");
        assertThat(rateNames(result.subscriptionProducts()))
                .containsExactly("청년우대형 청약통장");
        assertThat(result.bankRateRanked())
                .allSatisfy(product -> {
                    assertThat(product.productPropertyId()).isNotNull();
                    assertThat(product.providerName()).isNotBlank();
                });
    }

    @Test
    void 저축기간_1년을_선택하면_기간점수가_반영된다() {
        ProductSearchResultDto result = searchService.search(createRequest(
                50,
                List.of(new OptionRequestDto(CategoryIdEnum.PERIOD.getId(), 24L))
        ), authenticatedUser());

        ProductMatchDto oneYearBankProduct = findMatch(result.bankRanked(), "e-쎄이프 정기예금");
        ProductMatchDto adjacentGovProduct = findMatch(result.governmentRanked(), "청년내일채움공제");

        assertThat(oneYearBankProduct.periodScore()).isCloseTo(57.1429, offset(0.0001));
        assertThat(oneYearBankProduct.totalScore()).isCloseTo(100.0, offset(0.0001));
        assertThat(adjacentGovProduct.periodScore()).isCloseTo(27.5, offset(0.0001));
    }

    @Test
    void 희망납입액이_상품_최소납입액에_미달하면_제외된다() {
        ProductSearchResultDto result = searchService.search(createRequest(5, List.of()), authenticatedUser());

        assertThat(matchNames(result.governmentRanked())).hasSize(1);
        assertThat(result.bankRanked()).isEmpty();
        assertThat(result.bankRateRanked()).isEmpty();

        assertThat(result.subscriptionProducts()).hasSize(1);
    }

    @Test
    void 군복무_신분을_선택하면_키워드가_일치하는_상품의_신분점수가_상승한다() {
        jdbcTemplate.update("""
                INSERT INTO product_property_required_keyword
                    (product_property_id, keyword_code, effect, confidence)
                VALUES (
                    (SELECT pp.id
                     FROM product_properties pp
                     JOIN product p ON p.id = pp.product_id
                     WHERE p.product_code = 'SEARCH_YOUTH_SAVING'),
                    'STATUS_MILITARY',
                    'REQUIRE',
                    'HIGH'
                )
                """);

        ProductSearchResultDto result = searchService.search(createRequest(
                50,
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 21L))
        ), authenticatedUser());

        assertThat(matchNames(result.bankRanked()))
                .containsExactly("청년우대적금", "e-쎄이프 정기예금");

        ProductMatchDto militaryProduct = result.bankRanked().get(0);
        ProductMatchDto generalProduct = result.bankRanked().get(1);
        assertThat(militaryProduct.identityScore()).isCloseTo(25.0, offset(0.0001));
        assertThat(militaryProduct.totalScore()).isCloseTo(100.0, offset(0.0001));
        assertThat(generalProduct.identityScore()).isZero();
        assertThat(generalProduct.totalScore()).isCloseTo(75.0, offset(0.0001));
    }

    @Test
    void 거주지역을_선택하면_온통청년_상품에만_지역필터를_적용한다() {
        ProductSearchResultDto result = searchService.search(createRequest(
                50,
                List.of(new OptionRequestDto(CategoryIdEnum.REGION.getId(), 1L))
        ), authenticatedUser());

        assertThat(matchNames(result.bankRanked()))
                .containsExactlyInAnyOrder("e-쎄이프 정기예금", "청년우대적금");
    }
    @Test
    void 동일_상품의_여러_옵션은_하나로_합쳐져서_반환된다() {
        ProductSearchResultDto result = searchService.search(createRequest(50, List.of()), authenticatedUser());

        // 상품명 기준으로 중복 없는지 확인
        List<String> govNames = matchNames(result.governmentRanked());
        List<String> bankNames = matchNames(result.bankRanked());
        List<String> rateNames = rateNames(result.bankRateRanked());

        // 중복 없이 distinct하게 반환되는지
        assertThat(govNames).doesNotHaveDuplicates();
        assertThat(bankNames).doesNotHaveDuplicates();
        assertThat(rateNames).doesNotHaveDuplicates();

        // 단리/복리가 있는 e-쎄이프 정기예금이 하나만 나오는지
        assertThat(bankNames.stream()
                .filter(name -> name.equals("e-쎄이프 정기예금"))
                .count()).isEqualTo(1);

        // 12개월/24개월 옵션이 있는 청년우대적금이 하나만 나오는지
        assertThat(bankNames.stream()
                .filter(name -> name.equals("청년우대적금"))
                .count()).isEqualTo(1);
    }

    @Test
    void 부적격_옵션은_같은_상품의_금리계산에서_다시_선택되지_않는다() {
        insertHighYieldSmeOnlyOption();

        ProductSearchResultDto result = searchService.search(createRequest(50, List.of()), authenticatedUser());

        ProductRateDto youthEmployment = findRate(result.governmentRateRanked(), "청년내일채움공제");
        assertThat(youthEmployment.achievableRate()).isCloseTo(50.0, offset(0.0001));
    }

    @Test
    void 키워드를_하나도_선택하지_않으면_예외를_던진다() {
        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(27),
                        null,
                        null,
                        null,
                        12,
                        null,
                        true,
                        null,
                        50L,
                        null,
                        List.of()
                )
        );

        assertThatThrownBy(() -> searchService.search(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
                        .isEqualTo(SearchErrorCode.KEYWORD_REQUIRED));
    }

    @Test
    void 비로그인_검색은_탭B를_비활성화한다() {
        ProductSearchResultDto result = searchService.search(createRequest(50, List.of()));

        assertThat(result.tabs().tabAEnabled()).isTrue();
        assertThat(result.tabs().tabBEnabled()).isFalse();
        assertThat(result.tabs().tabBDisabledReason()).isNotBlank();
        assertThat(result.governmentRanked()).isNotEmpty();
        assertThat(result.bankRanked()).isNotEmpty();
        assertThat(result.governmentRateRanked()).isEmpty();
        assertThat(result.bankRateRanked()).isEmpty();
        assertThat(result.subscriptionProducts()).isEmpty();
    }

    @Test
    void 모든_속성이_비활성인_상품은_추천과_상품명검색에_노출되지_않는다() {
        jdbcTemplate.update("""
                UPDATE product_properties
                SET is_joinable = false
                WHERE product_id = (
                    SELECT id FROM product WHERE product_code = 'SEARCH_YOUTH_SAVING'
                )
                """);

        ProductSearchResultDto result = searchService.search(
                createRequest(50, List.of()), authenticatedUser());

        assertThat(matchNames(result.bankRanked())).doesNotContain("청년우대적금");
        assertThat(rateNames(result.bankRateRanked())).doesNotContain("청년우대적금");
        assertThat(searchService.searchByName("청년우대적금")).isEmpty();
    }

    private SearchRequestDto createRequest(long monthlySavingsGoal, List<OptionRequestDto> options) {
        List<OptionRequestDto> selectedOptions = options.isEmpty()
                ? List.of(new OptionRequestDto(CategoryIdEnum.REGION.getId(), 2L))
                : options;

        return new SearchRequestDto(
                selectedOptions,
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(27),
                        30_000_000L,
                        3,
                        100,
                        12,
                        null,
                        true,
                        null,
                        monthlySavingsGoal,
                        null,
                        List.of(),
                        List.of(),
                        List.of()
                )
        );
    }

    private ProductMatchDto findMatch(List<ProductMatchDto> products, String productName) {
        return products.stream()
                .filter(product -> product.productName().equals(productName))
                .findFirst()
                .orElseThrow();
    }

    private ProductRateDto findRate(List<ProductRateDto> products, String productName) {
        return products.stream()
                .filter(product -> product.productName().equals(productName))
                .findFirst()
                .orElseThrow();
    }

    private List<String> matchNames(List<ProductMatchDto> products) {
        return products.stream()
                .map(ProductMatchDto::productName)
                .toList();
    }

    private List<String> rateNames(List<ProductRateDto> products) {
        return products.stream()
                .map(ProductRateDto::productName)
                .toList();
    }

    private AuthUserDetails authenticatedUser() {
        return new AuthUserDetails(1L, UserRole.RECOMMENDATION);
    }

    private void insertHighYieldSmeOnlyOption() {
        jdbcTemplate.update("""
                INSERT INTO product_properties (
                    product_id, provider_id, base_rate, max_rate, min_monthly_limit, max_monthly_limit,
                    gov_contribution_type, gov_matching_ratio, gov_monthly_fixed_contribution, gov_contribution_period_months,
                    min_age, max_age, min_tenure_months, requires_homeless, requires_householder,
                    is_joinable, intr_rate_type, save_trm
                )
                VALUES (
                    (SELECT id FROM product WHERE product_code = 'SEARCH_YOUTH_EMPLOYMENT'),
                    (SELECT id FROM provider WHERE code = 'SEARCH_GOV'),
                    10.0, 10.0, 12, 50,
                    'RATIO', 3.0000, NULL, 24,
                    15, 34, 6, false, false,
                    true, NULL, 24
                )
                """);
        jdbcTemplate.update("""
                INSERT INTO product_property_required_keyword (
                    product_property_id, keyword_code, effect, confidence
                )
                SELECT pp.id, 'STATUS_SME_WORKER', 'REQUIRE', 'HIGH'
                FROM product_properties pp
                JOIN product p ON p.id = pp.product_id
                WHERE p.product_code = 'SEARCH_YOUTH_EMPLOYMENT'
                  AND pp.gov_matching_ratio = 3.0000
                """);
    }
}
