package apptive.fin.search;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.dto.UnifiedSearchResultDto;
import apptive.fin.search.enums.CategoryIdEnum;
import apptive.fin.search.service.SearchService;
import apptive.fin.support.IntegrationTestSupport;
import apptive.fin.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 단기예치 정렬 통합 테스트.
 * 실제 SearchService.searchUnified 결과를 기반으로 정렬을 검증한다.
 */
@Sql(
        scripts = "/sql/short-term-sorting-products.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup-product-fixtures.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class ShortTermSortingIntegrationTest extends IntegrationTestSupport {

    private static final Long TERM_1_MONTH_OPTION_ID = 21L;  // TERM_1_MONTH
    private static final Long SALARY_TRANSFER_OPTION_ID = 31L;  // BANK_SALARY_TRANSFER

    @Autowired
    private SearchService searchService;

    @Test
    void 단기예치_예적금은_실수령액_내림차순으로_정렬된다() {
        // Given: 로그인 + 모든 조건 충족
        SearchRequestDto request = createShortTermRequest();
        AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

        // When: 실제 서비스 호출
        UnifiedSearchResultDto result = searchService.searchUnified(request, userDetails);

        // Then: 예적금 탭이 활성화되고 실수령액 순 정렬
        assertThat(result.tabs().tabCEnabled()).isTrue();

        List<ProductRateDto> depositSavings = result.depositSavingsProducts();
        assertThat(depositSavings).isNotEmpty();

        // 실수령액 내림차순 검증 (높은 금리 → 높은 실수령액)
        for (int i = 0; i < depositSavings.size() - 1; i++) {
            Long current = depositSavings.get(i).netReturn();
            Long next = depositSavings.get(i + 1).netReturn();
            if (current != null && next != null) {
                assertThat(current).isGreaterThanOrEqualTo(next);
            }
        }
    }

    @Test
    void 비로그인시_예적금탭은_비활성화되고_파킹탭만_활성화된다() {
        // Given: 비로그인
        SearchRequestDto request = createShortTermRequest();

        // When
        UnifiedSearchResultDto result = searchService.searchUnified(request, null);

        // Then
        assertThat(result.tabs().tabCEnabled()).isFalse();
        assertThat(result.tabs().tabDEnabled()).isTrue();
    }

    @Test
    void 단기예치_결과에_상품개수가_올바르게_포함된다() {
        // Given
        SearchRequestDto request = createShortTermRequest();
        AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

        // When
        UnifiedSearchResultDto result = searchService.searchUnified(request, userDetails);

        // Then: 예적금 + 파킹 전체 카운트
        long depositCount = result.depositSavingsProducts() != null ? result.depositSavingsProducts().size() : 0;
        long parkingCount = result.parkingProducts() != null ? result.parkingProducts().size() : 0;

        assertThat(result.eligibleProductCount()).isEqualTo(depositCount + parkingCount);
    }

    // === Helper methods ===

    private SearchRequestDto createShortTermRequest() {
        return new SearchRequestDto(
                List.of(
                        new OptionRequestDto(CategoryIdEnum.PERIOD.getId(), TERM_1_MONTH_OPTION_ID),
                        new OptionRequestDto(CategoryIdEnum.BANK_COND.getId(), SALARY_TRANSFER_OPTION_ID)
                ),
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(25),  // 생년월일 (25세)
                        50_000_000L,   // annualIncome
                        3,             // householdSize
                        100,           // householdIncomePercent
                        null,          // tenureMonths
                        null,          // isFirstJob
                        null,          // isHomeless
                        null,          // isHouseholder
                        null,          // monthlySavingsGoal
                        10_000_000L,   // depositAmount (1천만원)
                        1,             // saveTrmExact (1개월)
                        List.of(),     // neverUsedBanks (없음)
                        List.of(),     // maturedSavingBanks (없음)
                        List.of()      // selectedInterestRateOptions
                )
        );
    }

    private AuthUserDetails mockUserDetails(UserRole role) {
        AuthUserDetails userDetails = mock(AuthUserDetails.class);
        when(userDetails.getRole()).thenReturn(role);
        return userDetails;
    }
}
