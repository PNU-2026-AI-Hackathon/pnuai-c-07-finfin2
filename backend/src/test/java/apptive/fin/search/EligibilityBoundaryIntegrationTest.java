package apptive.fin.search;

import apptive.fin.search.enums.CategoryIdEnum;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.service.EligibilityFilterService;
import apptive.fin.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 자격요건 경계값 테스트.
 * age/annualIncome/householdIncomePercent/monthlyDeposit 각각의 경계값(=, =+1, =-1)에서
 * EligibilityFilterService(및 ProductRepository.findEligibleProducts JPQL)의 필터링이
 * 정확히 동작하는지 검증한다.
 */
@Sql(
        scripts = "/sql/eligibility-boundary-products.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup-product-fixtures.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class EligibilityBoundaryIntegrationTest extends IntegrationTestSupport {

    private static final Long MILITARY_IDENTITY_OPTION_ID = 21L; // STATUS_MILITARY (군복무)

    @Autowired
    private EligibilityFilterService eligibilityFilterService;

    // ===== 1. 나이 경계 (병역 연령 확장) =====

    @Test
    void 병역연령확장_인정상품_만34세_비군필은_포함된다() {
        List<Product> result = filter(ageRequest(34, false));

        assertThat(productCodes(result)).contains("TEST_AGE_MIL_EXT");
    }

    @Test
    void 병역연령확장_인정상품_만35세_비군필은_제외된다() {
        List<Product> result = filter(ageRequest(35, false));

        assertThat(productCodes(result)).doesNotContain("TEST_AGE_MIL_EXT");
    }

    @Test
    void 병역연령확장_표시상품도_만35세_군필은_제외된다() {
        List<Product> result = filter(ageRequest(35, true));

        assertThat(productCodes(result)).doesNotContain("TEST_AGE_MIL_EXT");
    }

    @Test
    void 병역연령확장_표시상품도_만39세_군필은_제외된다() {
        List<Product> result = filter(ageRequest(39, true));

        assertThat(productCodes(result)).doesNotContain("TEST_AGE_MIL_EXT");
    }

    @Test
    void 병역연령확장_인정상품_만40세_군필은_제외된다() {
        List<Product> result = filter(ageRequest(40, true));

        assertThat(productCodes(result)).doesNotContain("TEST_AGE_MIL_EXT");
    }

    @Test
    void 병역연령확장_미인정상품은_군필이어도_만35세면_제외된다() {
        List<Product> result = filter(ageRequest(35, true));

        assertThat(productCodes(result)).doesNotContain("TEST_AGE_NO_MIL_EXT");
    }

    // ===== 2. 소득 경계 =====

    @Test
    void 소득상한과_정확히_같으면_포함된다() {
        List<Product> result = filter(incomeRequest(24_000_000L));

        assertThat(productCodes(result)).contains("TEST_INCOME_MAX");
    }

    @Test
    void 소득상한을_1원_초과하면_제외된다() {
        List<Product> result = filter(incomeRequest(24_000_001L));

        assertThat(productCodes(result)).doesNotContain("TEST_INCOME_MAX");
    }

    @Test
    void 소득증빙불가_0원이면_소득요건_있는_상품은_제외된다() {
        List<Product> result = filter(incomeRequest(0L));

        assertThat(productCodes(result)).doesNotContain("TEST_INCOME_MAX");
    }

    @Test
    void 소득요건_없는_상품은_소득증빙불가_0원이어도_포함된다() {
        List<Product> result = filter(incomeRequest(0L));

        assertThat(productCodes(result)).contains("TEST_INCOME_NONE");
    }

    // ===== 3. 가구 중위소득 경계 =====

    @Test
    void 중위소득_비율상한과_정확히_같으면_포함된다() {
        List<Product> result = filter(percentRequest(120));

        assertThat(productCodes(result)).contains("TEST_INCOME_PERCENT");
    }

    @Test
    void 중위소득_비율상한을_초과하면_제외된다() {
        List<Product> result = filter(percentRequest(121));

        assertThat(productCodes(result)).doesNotContain("TEST_INCOME_PERCENT");
    }

    // ===== 4. 최소납입 경계 =====

    @Test
    void 최소납입액과_정확히_같으면_포함된다() {
        List<Product> result = filter(monthlyDepositRequest(100_000L));

        assertThat(productCodes(result)).contains("TEST_MONTHLY_MIN");
    }

    @Test
    void 최소납입액보다_적으면_제외된다() {
        List<Product> result = filter(monthlyDepositRequest(99_999L));

        assertThat(productCodes(result)).doesNotContain("TEST_MONTHLY_MIN");
    }

    private List<Product> filter(SearchRequestDto request) {
        return eligibilityFilterService.filterEligible(request);
    }

    /**
     * 정확한 나이를 만들기 위해 생일을 하루 더 과거로 이동시킨다.
     * (오늘이 생일인 경우 minusYears(age)만으로는 경계가 흔들릴 수 있음)
     */
    private LocalDate birthdateForAge(int age) {
        return LocalDate.now().minusYears(age).minusDays(1);
    }

    private SearchRequestDto ageRequest(int age, boolean isMilitary) {
        List<OptionRequestDto> options = isMilitary
                ? List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), MILITARY_IDENTITY_OPTION_ID))
                : List.of();

        return new SearchRequestDto(
                options,
                new DetailedOptionsDto(
                        birthdateForAge(age),
                        null, null, null, null, null, null, null, null, List.of()
                )
        );
    }

    private SearchRequestDto incomeRequest(long annualIncome) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        birthdateForAge(30),
                        annualIncome,
                        null, null, null, null, null, null, null, List.of()
                )
        );
    }

    private SearchRequestDto percentRequest(int householdIncomePercent) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        birthdateForAge(30),
                        null,
                        null,
                        householdIncomePercent,
                        null, null, null, null, null, List.of()
                )
        );
    }

    private SearchRequestDto monthlyDepositRequest(long monthlyDeposit) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        birthdateForAge(30),
                        null, null, null, null, null, null, null,
                        monthlyDeposit,
                        List.of()
                )
        );
    }

    private List<String> productCodes(List<Product> products) {
        return products.stream()
                .map(Product::getProductCode)
                .toList();
    }
}
