package apptive.fin.search;

import apptive.fin.search.enums.CategoryIdEnum;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ProductDetailResponseDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.ProductSearchResultDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.repository.ProductRepository;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.service.SearchService;
import apptive.fin.support.IntegrationTestSupport;
import apptive.fin.user.UserRole;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Sql(
        scripts = "/sql/search-products.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup-product-fixtures.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class SearchServiceIntegrationTest extends IntegrationTestSupport {

    // data.sql의 category_option 삽입 순서로 결정되는 옵션 id (다른 테스트도 같은 방식으로 하드코딩한다)
    private static final Long BUSAN_REGION_OPTION_ID = 2L;          // REGION_BUSAN
    private static final Long AROUND_1_YEAR_PERIOD_OPTION_ID = 24L; // TERM_AROUND_1_YEAR
    private static final Long MAX_INTEREST_BENEFIT_OPTION_ID = 25L; // BENEFIT_MAX_INTEREST

    @Autowired
    private SearchService searchService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void 추천응답은_모든_추천카드의_상세정보를_옵션단위로_포함한다() {
        ProductSearchResultDto result = searchService.search(createRequest(50, List.of()), authenticatedUser());

        // 상세는 상품이 아니라 (상품, 옵션) 단위다 — 카드가 고른 옵션과 상세가 본 옵션이 같아야 값이 일치한다.
        assertThat(result.productDetails())
                .extracting(ProductDetailResponseDto::productPropertyId)
                .containsExactlyInAnyOrderElementsOf(rankedPropertyIds(result));
    }

    @Test
    void 추천상품상세는_로그인시_화면용_정보와_수익지표를_포함한다() {
        ProductSearchResultDto result = searchService.search(createRequest(50, List.of()), authenticatedUser());

        ProductDetailResponseDto bankDetail = result.productDetails().stream()
                .filter(detail -> detail.productName().equals("청년우대적금"))
                .findFirst()
                .orElseThrow();

        assertThat(bankDetail.providerName()).isEqualTo("국민은행");
        assertThat(bankDetail.content()).isEqualTo("만 19~29세 전용 우대 적금");
        assertThat(bankDetail.metricsLocked()).isFalse();
        assertThat(bankDetail.bank()).isNotNull();
        assertThat(bankDetail.government()).isNull();
        assertThat(bankDetail.rateTable()).isNotEmpty();
    }

    @Test
    void 비로그인_추천상품상세는_수익지표를_잠근다() {
        ProductSearchResultDto result = searchService.search(createRequest(50, List.of()));

        assertThat(result.productDetails())
                .isNotEmpty() // allSatisfy는 빈 리스트에 무조건 통과하므로 먼저 비어있지 않음을 확인
                .allSatisfy(detail -> {
                    assertThat(detail.metricsLocked()).isTrue();
                    assertThat(detail.government()).isNull();
                    assertThat(detail.bank()).isNull();
                    assertThat(detail.rateTable()).isNull();
                });
    }

    @Test
    void 추천상품상세에는_청약상품도_포함되고_같은_옵션이_중복되지_않는다() {
        ProductSearchResultDto result = searchService.search(createRequest(50, List.of()), authenticatedUser());

        // 청약상품은 ProductRateDto의 productPropertyId가 null이지만 govRanked를 통해 커버돼야 한다.
        assertThat(result.productDetails())
                .extracting(ProductDetailResponseDto::productName)
                .contains("청년우대형 청약통장");
        assertThat(result.productDetails())
                .extracting(ProductDetailResponseDto::productPropertyId)
                .doesNotHaveDuplicates();
    }

    @Test
    void 추천상품상세는_상품별_저장소_재조회_없이_검색에서_확보한_엔티티를_사용한다() {
        searchService.search(createRequest(50, List.of()), authenticatedUser());

        verify(productRepository, times(1)).findEligibleProducts(
                any(), any(), any(), any(), any(), any(), any(), any());
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).findJoinableMaxRatesBySourceCode(anyString());
    }

    @Test
    void 추천상세는_상품수가_늘어도_쿼리수가_비례해_늘지_않는다() {
        SearchRequestDto request = createRequest(50, List.of());
        long baseline = queryCountOf(() -> searchService.search(request, authenticatedUser()));
        // statistics가 꺼져 있으면 0이 나와 무조건 통과하므로 계측이 살아있는지 먼저 확인
        assertThat(baseline).isPositive();

        insertExtraEligibleBankProducts(10);
        long scaled = queryCountOf(() -> searchService.search(request, authenticatedUser()));

        // @BatchSize(100) 배치 로딩이면 상품이 10개 늘어도 쿼리 수는 사실상 그대로다.
        // 상세를 만들면서 상품별 lazy 왕복이 생기면 수십 개가 붙어 바로 걸린다.
        assertThat(scaled).isLessThanOrEqualTo(baseline + 2);
    }

    @Test
    void 최고이율_중심을_선택하면_상세_적합도가_리스트_카드_점수와_일치한다() {
        // #최고이율_중심은 정적 태그가 아니라 결과셋 상위 30% 컷으로 동적 판정한다.
        // 상세가 그 임계값을 못 받으면 정적 태그 폴백으로 떨어지는데, 픽스처에 정적
        // BENEFIT_MAX_INTEREST 행이 없어서 상위권 은행상품의 점수가 카드보다 낮게 나온다.
        ProductSearchResultDto result = searchService.search(
                createRequest(50, List.of(
                        new OptionRequestDto(CategoryIdEnum.REGION.getId(), BUSAN_REGION_OPTION_ID),
                        new OptionRequestDto(CategoryIdEnum.BENEFIT.getId(), MAX_INTEREST_BENEFIT_OPTION_ID)
                )),
                authenticatedUser());

        ProductMatchDto topRateCard = findMatch(result.bankRanked(), "청년우대적금");
        ProductDetailResponseDto topRateDetail = findDetail(result, topRateCard.productPropertyId());

        assertThat(topRateCard.benefitScore()).isPositive(); // 컷(4.5) 충족 → 동적 판정으로 혜택 점수를 받는다
        assertThat(topRateDetail.matchScore()).isCloseTo(topRateCard.totalScore(), offset(0.0001));
        assertThat(topRateDetail.keywords()).contains(KeywordValueEnum.BENEFIT_MAX_INTEREST);

        // 컷 미달 상품도 같은 기준으로 판정돼야 한다(양쪽 다 0점으로 일치).
        ProductMatchDto belowCutCard = findMatch(result.bankRanked(), "e-쎄이프 정기예금");
        ProductDetailResponseDto belowCutDetail = findDetail(result, belowCutCard.productPropertyId());

        assertThat(belowCutCard.benefitScore()).isZero();
        assertThat(belowCutDetail.matchScore()).isCloseTo(belowCutCard.totalScore(), offset(0.0001));
        assertThat(belowCutDetail.keywords()).doesNotContain(KeywordValueEnum.BENEFIT_MAX_INTEREST);
    }

    @Test
    void 적합도_대표옵션과_금리_대표옵션이_다르면_카드마다_자기_옵션_상세를_받는다() {
        // 청년우대적금에 36개월·고금리 옵션을 붙이면 탭A(적합도)는 12개월, 탭B(금리)는 36개월을 고른다.
        // 상세를 상품당 1건만 만들면 금리순 카드의 금리와 상세의 금리가 갈린다.
        insertLongTermHighRateOption();

        ProductSearchResultDto result = searchService.search(
                createRequest(50, List.of(
                        new OptionRequestDto(CategoryIdEnum.PERIOD.getId(), AROUND_1_YEAR_PERIOD_OPTION_ID)
                )),
                authenticatedUser());

        ProductMatchDto matchCard = findMatch(result.bankRanked(), "청년우대적금");
        ProductRateDto rateCard = findRate(result.bankRateRanked(), "청년우대적금");
        assertThat(matchCard.productPropertyId()).isNotEqualTo(rateCard.productPropertyId());

        ProductDetailResponseDto matchDetail = findDetail(result, matchCard.productPropertyId());
        ProductDetailResponseDto rateDetail = findDetail(result, rateCard.productPropertyId());

        assertThat(matchDetail.matchScore()).isCloseTo(matchCard.totalScore(), offset(0.0001));
        assertThat(rateDetail.bank().achievableRate()).isEqualTo(rateCard.achievableRate());
        assertThat(matchDetail.bank().achievableRate()).isNotEqualTo(rateDetail.bank().achievableRate());
    }

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

    // 상세 생성 대상이 되어야 하는 옵션 id 집합.
    // subscriptionProducts는 productPropertyId가 null이라 제외한다 — govRanked가 같은 상품을 커버한다.
    private Set<Long> rankedPropertyIds(ProductSearchResultDto result) {
        Set<Long> propertyIds = new HashSet<>();
        result.governmentRanked().forEach(card -> propertyIds.add(card.productPropertyId()));
        result.bankRanked().forEach(card -> propertyIds.add(card.productPropertyId()));
        result.governmentRateRanked().forEach(card -> propertyIds.add(card.productPropertyId()));
        result.bankRateRanked().forEach(card -> propertyIds.add(card.productPropertyId()));
        return propertyIds;
    }

    private ProductDetailResponseDto findDetail(ProductSearchResultDto result, Long productPropertyId) {
        return result.productDetails().stream()
                .filter(detail -> productPropertyId.equals(detail.productPropertyId()))
                .findFirst()
                .orElseThrow();
    }

    private long queryCountOf(Runnable action) {
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
        action.run();
        return statistics.getPrepareStatementCount();
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

    // 청년우대적금에 36개월·고금리 옵션 추가.
    // 기존 12개월 옵션(base 3.8)보다 금리는 높지만 1년 내외 선택 시 기간 점수는 0이라
    // 적합도 대표 옵션(12개월)과 금리 대표 옵션(36개월)이 갈린다.
    private void insertLongTermHighRateOption() {
        jdbcTemplate.update("""
                INSERT INTO product_properties (
                    product_id, provider_id, base_rate, max_rate, min_monthly_limit, max_monthly_limit,
                    min_age, max_age, requires_homeless, requires_householder,
                    is_joinable, intr_rate_type, save_trm
                )
                VALUES (
                    (SELECT id FROM product WHERE product_code = 'SEARCH_YOUTH_SAVING'),
                    (SELECT id FROM provider WHERE code = 'SEARCH_BANK_B'),
                    4.9, 5.0, 10, 50,
                    19, 29, false, false,
                    true, 'SINGLE_INTEREST', 36
                )
                """);
    }

    // 쿼리 수가 상품 수에 비례하는지 보기 위한 더미 은행상품. 만 27세 기준으로 모두 가입 가능하다.
    private void insertExtraEligibleBankProducts(int count) {
        for (int i = 0; i < count; i++) {
            jdbcTemplate.update("""
                    INSERT INTO product (source_id, type, product_code, product_name, content)
                    VALUES ((SELECT id FROM product_source WHERE code = 'FSS'), 'SAVING', ?, ?, '더미 상품')
                    """, "SEARCH_EXTRA_" + i, "더미적금" + i);
            jdbcTemplate.update("""
                    INSERT INTO product_properties (
                        product_id, provider_id, base_rate, max_rate, min_monthly_limit, max_monthly_limit,
                        min_age, max_age, requires_homeless, requires_householder,
                        is_joinable, intr_rate_type, save_trm
                    )
                    VALUES (
                        (SELECT id FROM product WHERE product_code = ?),
                        (SELECT id FROM provider WHERE code = 'SEARCH_BANK_A'),
                        2.0, 2.5, 10, 50,
                        19, 34, false, false,
                        true, 'SINGLE_INTEREST', 12
                    )
                    """, "SEARCH_EXTRA_" + i);
        }
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
