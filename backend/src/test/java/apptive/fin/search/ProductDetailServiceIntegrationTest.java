package apptive.fin.search;

import apptive.fin.search.enums.CategoryIdEnum;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductApplyStatus;
import apptive.fin.search.enums.ProductType;
import apptive.fin.search.enums.ReserveType;
import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ProductDetailRequestDto;
import apptive.fin.search.dto.ProductDetailResponseDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ProductSearchResultDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.service.ProductDetailService;
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

@Sql(scripts = "/sql/search-products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup-product-fixtures.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ProductDetailServiceIntegrationTest extends IntegrationTestSupport {

    // data.sql의 category_option 삽입 순서로 결정되는 옵션 id
    private static final Long MAX_INTEREST_BENEFIT_OPTION_ID = 25L; // BENEFIT_MAX_INTEREST

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 정부상품_상세는_로그인시_환산수익률과_예상만기기여금총액을_반환한다() {
        Long productId = productId("SEARCH_YOUTH_EMPLOYMENT");
        Long propertyId = propertyId("SEARCH_YOUTH_EMPLOYMENT");

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, 100L), authenticatedUser());

        assertThat(detail.metricsLocked()).isFalse();
        assertThat(detail.productType()).isEqualTo(ProductType.POLICY);
        assertThat(detail.sourceCode()).isEqualTo("ONTONG");
        assertThat(detail.bank()).isNull();
        assertThat(detail.government()).isNotNull();
        assertThat(detail.government().annualizedYield()).isEqualTo(50.0);          // 배수 1.0 / 24개월
        assertThat(detail.government().effectiveMonthlyDeposit()).isEqualTo(50L);   // min(100, 한도 50)
        assertThat(detail.government().expectedTotalContribution()).isEqualTo(1_200L); // 1.0 x 50 x 24
        assertThat(detail.rateTable()).hasSize(1); // base_rate 공시 → 노출
    }

    @Test
    void 정부상품_상세는_비로그인시_수익지표를_잠근다() {
        Long productId = productId("SEARCH_YOUTH_EMPLOYMENT");

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(null, 100L), null);

        assertThat(detail.metricsLocked()).isTrue();
        assertThat(detail.lockMessage()).isNotBlank();
        assertThat(detail.government()).isNull();
        assertThat(detail.bank()).isNull();
        assertThat(detail.rateTable()).isNull();
        assertThat(detail.providerName()).isEqualTo("금융위원회"); // 공개 정보는 노출
    }

    @Test
    void 청약상품_상세는_로그인해도_간소화되어_수익지표가_없다() {
        Long productId = productId("SEARCH_SUBSCRIPTION");
        Long propertyId = propertyId("SEARCH_SUBSCRIPTION");

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, 100L), authenticatedUser());

        assertThat(detail.productType()).isEqualTo(ProductType.SUBSCRIPTION);
        assertThat(detail.government()).isNull();
        assertThat(detail.bank()).isNull();
        assertThat(detail.rateTable()).isNull();
    }

    @Test
    void 은행상품_상세는_금리와_provider_대표_아웃링크를_반환한다() {
        Long productId = productId("SEARCH_YOUTH_SAVING");
        Long propertyId = propertyId("SEARCH_YOUTH_SAVING");
        jdbcTemplate.update("""
                INSERT INTO product_property_required_keyword
                    (product_property_id, keyword_code, effect, confidence)
                VALUES (?, 'STATUS_MILITARY', 'REQUIRE', 'HIGH')
                """, propertyId);
        jdbcTemplate.update("UPDATE provider SET apply_url = ? WHERE code = ?",
                "https://bank.example/apply", "SEARCH_BANK_B");

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, null), authenticatedUser());

        assertThat(detail.government()).isNull();
        assertThat(detail.bank()).isNotNull();
        assertThat(detail.bank().baseRate()).isEqualTo(3.8);
        assertThat(detail.bank().maxRate()).isEqualTo(4.5);
        assertThat(detail.bank().achievableRate()).isEqualTo(3.8); // 우대조건 없음 → 기본금리
        assertThat(detail.bank().metConditions()).isEmpty();
        assertThat(detail.bank().unmetConditions()).isEmpty();
        assertThat(detail.keywords())
                .contains(KeywordValueEnum.STATUS_MILITARY, KeywordValueEnum.REGION_BUSAN);
        assertThat(detail.applyUrl()).isEqualTo("https://bank.example/apply"); // FSS → provider 대표 URL
    }

    @Test
    void BANK_ETC는_자동충족되지_않지만_상세_키워드와_조건과_금리표에_노출된다() {
        Long productId = productId("SEARCH_YOUTH_SAVING");
        Long propertyId = propertyId("SEARCH_YOUTH_SAVING");
        jdbcTemplate.update("""
                INSERT INTO product_preferential_rates
                    (product_property_id, keyword_code, rate, description)
                VALUES (?, 'BANK_ETC', 0.70, '기타 우대조건')
                """, propertyId);

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId,
                request(propertyId, null),
                authenticatedUser()
        );

        assertThat(detail.keywords()).contains(KeywordValueEnum.BANK_ETC);
        assertThat(detail.bank().achievableRate()).isEqualTo(3.8);
        assertThat(detail.bank().unmetConditions())
                .extracting(condition -> condition.keywordCode())
                .contains(KeywordValueEnum.BANK_ETC);
        assertThat(detail.rateTable())
                .flatExtracting(row -> row.preferentialRates())
                .extracting(condition -> condition.keywordCode())
                .contains(KeywordValueEnum.BANK_ETC);
    }

    @Test
    void fssDetailPrefersPropertyApplyUrlOverProviderApplyUrl() {
        Long productId = productId("SEARCH_YOUTH_SAVING");
        Long propertyId = propertyId("SEARCH_YOUTH_SAVING");
        jdbcTemplate.update("UPDATE provider SET apply_url = ? WHERE code = ?",
                "https://bank.example/apply", "SEARCH_BANK_B");
        jdbcTemplate.update("UPDATE product_properties SET apply_url = ? WHERE id = ?",
                "https://product.example/apply", propertyId);

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, null), authenticatedUser());

        assertThat(detail.applyUrl()).isEqualTo("https://product.example/apply");
    }

    @Test
    void ontongDetailFallsBackToProviderWithoutUsingAnotherPropertyUrl() {
        Long productId = productId("SEARCH_YOUTH_EMPLOYMENT");
        Long propertyId = propertyId("SEARCH_YOUTH_EMPLOYMENT");
        jdbcTemplate.update("UPDATE provider SET apply_url = ? WHERE code = ?",
                "https://provider.example/apply", "SEARCH_GOV");
        jdbcTemplate.update("""
                INSERT INTO product_properties
                    (product_id, provider_id, is_joinable, requires_homeless,
                     requires_householder, allows_military_age_extension,
                     exclude_from_rate_comparison, apply_url)
                VALUES (?, (SELECT id FROM provider WHERE code = 'SEARCH_GOV'),
                        true, false, false, false, false, 'https://other-property.example/apply')
                """, productId);

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, null), authenticatedUser());

        assertThat(detail.applyUrl()).isEqualTo("https://provider.example/apply");
    }

    @Test
    void 은행상품_상세는_전체은행_상위30퍼센트면_최고이율_칩을_동적으로_붙인다() {
        // FSS 은행상품 maxRate = {4.5(SEARCH_YOUTH_SAVING), 3.45(SEARCH_SAFE_DEPOSIT)} → 상위 30% 컷 = 4.5.
        // 정적 태그가 아니라 동적 판정으로 최고이율 칩이 붙어야 한다.
        Long productId = productId("SEARCH_YOUTH_SAVING");
        Long propertyId = propertyId("SEARCH_YOUTH_SAVING");

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, null), authenticatedUser());

        assertThat(detail.keywords()).contains(KeywordValueEnum.BENEFIT_MAX_INTEREST);
    }

    @Test
    void 은행상품_상세는_상위30퍼센트_미만이면_최고이율_칩이_없다() {
        // SEARCH_SAFE_DEPOSIT maxRate 3.45 < 컷 4.5 → 칩 없음(정적 태그도 없음).
        Long productId = productId("SEARCH_SAFE_DEPOSIT");
        Long propertyId = propertyId("SEARCH_SAFE_DEPOSIT");

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, null), authenticatedUser());

        assertThat(detail.keywords()).doesNotContain(KeywordValueEnum.BENEFIT_MAX_INTEREST);
    }

    @Test
    void 은행상품_상세는_영속된_정적_최고이율_태그가_있어도_동적기준_미달이면_칩이_없다() {
        // 레거시 정적 BENEFIT_MAX_INTEREST 행이 남아있어도(재정규화 전 상황 모사),
        // 동적 컷(4.5) 미달인 SAFE_DEPOSIT(3.45)은 칩이 노출되면 안 된다 — 배지는 동적 결과만 반영.
        Long productId = productId("SEARCH_SAFE_DEPOSIT");
        Long propertyId = propertyId("SEARCH_SAFE_DEPOSIT");
        jdbcTemplate.update(
                "INSERT INTO product_property_keyword (product_property_id, keyword_code) VALUES (?, 'BENEFIT_MAX_INTEREST')",
                propertyId);

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, null), authenticatedUser());

        assertThat(detail.keywords()).doesNotContain(KeywordValueEnum.BENEFIT_MAX_INTEREST);
    }

    @Test
    void 상세_최고이율_컷은_비활성_상품을_제외한다() {
        // 비활성(is_joinable=false) 고금리(9.99) 속성을 추가해도 컷 계산에서 제외되어야 한다.
        // 활성 FSS maxRate = {4.5, 3.45} → 컷 4.5 유지 → YOUTH_SAVING(4.5)은 여전히 칩을 받는다.
        jdbcTemplate.update("""
                INSERT INTO product_properties
                    (product_id, provider_id, max_rate, is_joinable,
                     requires_homeless, requires_householder, allows_military_age_extension, exclude_from_rate_comparison)
                VALUES ((SELECT id FROM product WHERE product_code = 'SEARCH_SAFE_DEPOSIT'),
                        (SELECT id FROM provider WHERE code = 'SEARCH_BANK_A'),
                        9.99, false, false, false, false, false)
                """);

        Long productId = productId("SEARCH_YOUTH_SAVING");
        Long propertyId = propertyId("SEARCH_YOUTH_SAVING");
        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, null), authenticatedUser());

        assertThat(detail.keywords()).contains(KeywordValueEnum.BENEFIT_MAX_INTEREST);
    }

    @Test
    void 상품안내_필드는_비로그인_잠금과_무관하게_항상_노출된다() {
        Long productId = productId("SEARCH_YOUTH_EMPLOYMENT");
        Long propertyId = propertyId("SEARCH_YOUTH_EMPLOYMENT");
        jdbcTemplate.update(
                "UPDATE product SET join_method = ?, eligibility_text = ?, caution_text = ?, recruitment_period = ? WHERE id = ?",
                "읍·면·동 행정복지센터", "만 15세 이상 ~ 39세 이하", "3년 통장 유지 필요", "2026.5.4. ~ 2026.5.20.", productId);
        jdbcTemplate.update(
                "UPDATE product_properties SET rsrv_type = ? WHERE id = ?",
                ReserveType.FIXED.name(), propertyId);

        ProductDetailResponseDto locked = productDetailService.getProductDetail(
                productId, request(propertyId, 100L), null);
        ProductDetailResponseDto unlocked = productDetailService.getProductDetail(
                productId, request(propertyId, 100L), authenticatedUser());

        assertThat(locked.metricsLocked()).isTrue();
        for (ProductDetailResponseDto detail : List.of(locked, unlocked)) {
            assertThat(detail.joinMethod()).isEqualTo("읍·면·동 행정복지센터");
            assertThat(detail.eligibilityText()).isEqualTo("만 15세 이상 ~ 39세 이하");
            assertThat(detail.cautionText()).isEqualTo("3년 통장 유지 필요");
            assertThat(detail.recruitmentPeriod()).isEqualTo("2026.5.4. ~ 2026.5.20.");
            assertThat(detail.reserveType()).isEqualTo(ReserveType.FIXED);
            assertThat(detail.reserveTypeName()).isEqualTo("정액적립식");
        }
    }

    @Test
    void 상세_적합도는_리스트의_totalScore와_일치한다() {
        List<OptionRequestDto> options = List.of(new OptionRequestDto(CategoryIdEnum.REGION.getId(), 2L));
        DetailedOptionsDto detailedOptions = detailedOptions(50L);

        // 리스트(탭A)에서 해당 카드의 totalScore와 productPropertyId 확보
        ProductSearchResultDto list = searchService.search(
                new SearchRequestDto(options, detailedOptions), authenticatedUser());
        ProductMatchDto card = list.governmentRanked().stream()
                .filter(match -> match.productName().equals("청년내일채움공제"))
                .findFirst()
                .orElseThrow();

        // 같은 옵션 + 같은 property로 상세 조회 → matchScore가 리스트 값과 동일해야 함
        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                card.productId(),
                new ProductDetailRequestDto(card.productPropertyId(), options, detailedOptions),
                authenticatedUser());

        assertThat(detail.matchScore()).isNotNull();
        assertThat(detail.matchScore()).isCloseTo(card.totalScore(), offset(0.0001));
    }

    @Test
    void 최고이율_중심을_선택해도_상세_적합도는_리스트의_totalScore와_일치한다() {
        // #최고이율_중심은 정적 태그가 아니라 상위 30% 컷으로 동적 판정한다(PRD A-2).
        // 상세가 그 임계값 없이 채점하면 정적 태그 폴백으로 떨어져 카드보다 낮은 점수가 나온다.
        //
        // 이 픽스처에서는 전체 은행 컷과 결과셋 컷이 모두 4.5로 같아서 두 경로가 일치한다.
        // 상세 API가 아직 '전체 은행 joinable' 모집단으로 컷을 계산하므로, 두 모집단이 갈리는
        // 상황에서는 여전히 어긋날 수 있다 — 컷 기준 일원화는 별도 작업이다.
        List<OptionRequestDto> options = List.of(
                new OptionRequestDto(CategoryIdEnum.REGION.getId(), 2L),
                new OptionRequestDto(CategoryIdEnum.BENEFIT.getId(), MAX_INTEREST_BENEFIT_OPTION_ID)
        );
        DetailedOptionsDto detailedOptions = detailedOptions(50L);

        ProductSearchResultDto list = searchService.search(
                new SearchRequestDto(options, detailedOptions), authenticatedUser());
        ProductMatchDto card = list.bankRanked().stream()
                .filter(match -> match.productName().equals("청년우대적금"))
                .findFirst()
                .orElseThrow();
        assertThat(card.benefitScore()).isPositive(); // 컷(4.5) 충족 → 동적 판정으로 혜택 점수를 받는다

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                card.productId(),
                new ProductDetailRequestDto(card.productPropertyId(), options, detailedOptions),
                authenticatedUser());

        assertThat(detail.matchScore()).isCloseTo(card.totalScore(), offset(0.0001));
    }

    @Test
    void 은행상품의_자동조건은_추천목록과_상세에_동일하게_반영된다() {
        Long propertyId = propertyId("SEARCH_YOUTH_SAVING");
        jdbcTemplate.update("""
                INSERT INTO product_preferential_rates
                    (product_property_id, keyword_code, rate, description, min_age, max_age)
                VALUES
                    (?, 'BANK_ONLINE_JOIN', 0.10, '온라인 가입', NULL, NULL),
                    (?, 'BANK_AGE', 0.20, '청년 우대', 20, 30)
                """, propertyId, propertyId);

        List<OptionRequestDto> options =
                List.of(new OptionRequestDto(CategoryIdEnum.REGION.getId(), 2L));
        DetailedOptionsDto detailedOptions = detailedOptions(50L);

        ProductSearchResultDto list = searchService.search(
                new SearchRequestDto(options, detailedOptions),
                authenticatedUser()
        );
        ProductMatchDto card = list.bankRanked().stream()
                .filter(match -> match.productName().equals("청년우대적금"))
                .findFirst()
                .orElseThrow();

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                card.productId(),
                new ProductDetailRequestDto(card.productPropertyId(), options, detailedOptions),
                authenticatedUser()
        );

        assertThat(detail.matchScore()).isCloseTo(card.totalScore(), offset(0.0001));
        assertThat(detail.bank().achievableRate()).isEqualTo(4.1);
        assertThat(detail.bank().metConditions())
                .extracting(condition -> condition.keywordCode())
                .containsExactlyInAnyOrder(
                        KeywordValueEnum.BANK_ONLINE_JOIN,
                        KeywordValueEnum.BANK_AGE
                );
        assertThat(detail.bank().unmetConditions()).isEmpty();
    }

    @Test
    void 옵션없이_직접진입하면_적합도는_null이다() {
        Long productId = productId("SEARCH_YOUTH_EMPLOYMENT");
        Long propertyId = propertyId("SEARCH_YOUTH_EMPLOYMENT");

        // request(...) 헬퍼는 options를 비워 보냄 → 채점 근거 없음
        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(propertyId, 100L), authenticatedUser());

        assertThat(detail.matchScore()).isNull();
    }

    @Test
    void 비로그인_대표_property는_거래이력과_무관하게_최고_공시금리로_선택한다() {
        Long productId = productId("SEARCH_YOUTH_SAVING");
        Long originalPropertyId = propertyId("SEARCH_YOUTH_SAVING");
        jdbcTemplate.update(
                "UPDATE product_properties SET base_rate = 2.00, max_rate = 4.50 WHERE id = ?",
                originalPropertyId
        );
        jdbcTemplate.update("""
                INSERT INTO product_preferential_rates
                    (product_property_id, keyword_code, rate, description)
                VALUES (?, 'BANK_FIRST_TRANSACTION', 3.00, '첫거래 우대')
                """, originalPropertyId);
        jdbcTemplate.update("""
                INSERT INTO provider (source_id, code, name, apply_url)
                VALUES (
                    (SELECT id FROM product_source WHERE code = 'FSS'),
                    'DETAIL_PUBLIC_BANK',
                    '공개대표은행',
                    'https://public.example/apply'
                )
                """);
        jdbcTemplate.update("""
                INSERT INTO product_properties
                    (product_id, provider_id, base_rate, max_rate, save_trm)
                VALUES (
                    ?,
                    (SELECT id FROM provider WHERE code = 'DETAIL_PUBLIC_BANK'),
                    4.00,
                    5.00,
                    12
                )
                """, productId);

        DetailedOptionsDto usedBank = new DetailedOptionsDto(
                null, null, null, null, null, null, null, null,
                null, null, List.of("SEARCH_BANK_B"), List.of(), List.of()
        );
        DetailedOptionsDto unusedBank = new DetailedOptionsDto(
                null, null, null, null, null, null, null, null,
                null, null, List.of(), List.of(), List.of()
        );

        ProductDetailResponseDto withHistory = productDetailService.getProductDetail(
                productId,
                new ProductDetailRequestDto(null, List.of(), usedBank),
                null
        );
        ProductDetailResponseDto withoutHistory = productDetailService.getProductDetail(
                productId,
                new ProductDetailRequestDto(null, List.of(), unusedBank),
                null
        );

        for (ProductDetailResponseDto detail : List.of(withHistory, withoutHistory)) {
            assertThat(detail.providerName()).isEqualTo("공개대표은행");
            assertThat(detail.applyUrl()).isEqualTo("https://public.example/apply");
            assertThat(detail.metricsLocked()).isTrue();
        }
    }

    @Test
    void 로그인_기본상세는_가입가능한_속성만_대표값과_집계에_사용한다() {
        Long productId = productId("SEARCH_YOUTH_SAVING");
        jdbcTemplate.update("""
                INSERT INTO product_properties
                    (product_id, provider_id, base_rate, max_rate, save_trm, is_joinable)
                VALUES (
                    ?,
                    (SELECT id FROM provider WHERE code = 'SEARCH_BANK_B'),
                    9.00,
                    9.90,
                    24,
                    false
                )
                """, productId);
        jdbcTemplate.update("""
                INSERT INTO product_property_keyword (product_property_id, keyword_code)
                SELECT pp.id, 'STATUS_MILITARY'
                FROM product_properties pp
                WHERE pp.product_id = ? AND pp.save_trm = 24
                """, productId);

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(null, null), authenticatedUser());

        assertThat(detail.bank().baseRate()).isEqualTo(3.8);
        assertThat(detail.saveTrms()).containsExactly(12);
        assertThat(detail.rateTable())
                .extracting(row -> row.saveTrm())
                .containsExactly(12);
        assertThat(detail.keywords()).doesNotContain(KeywordValueEnum.STATUS_MILITARY);
    }

    @Test
    void 비로그인_기본상세도_가입가능한_속성만_대표값과_집계에_사용한다() {
        Long productId = productId("SEARCH_YOUTH_SAVING");
        jdbcTemplate.update("""
                INSERT INTO product_properties
                    (product_id, provider_id, base_rate, max_rate, save_trm, is_joinable)
                VALUES (
                    ?,
                    (SELECT id FROM provider WHERE code = 'SEARCH_BANK_B'),
                    9.00,
                    9.90,
                    24,
                    false
                )
                """, productId);
        jdbcTemplate.update("""
                INSERT INTO product_property_required_keyword
                    (product_property_id, keyword_code, effect, confidence)
                SELECT pp.id, 'STATUS_MILITARY', 'REQUIRE', 'HIGH'
                FROM product_properties pp
                WHERE pp.product_id = ? AND pp.save_trm = 24
                """, productId);

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(null, null), null);

        assertThat(detail.providerName()).isEqualTo("국민은행");
        assertThat(detail.saveTrms()).containsExactly(12);
        assertThat(detail.keywords()).doesNotContain(KeywordValueEnum.STATUS_MILITARY);
        assertThat(detail.metricsLocked()).isTrue();
    }

    @Test
    void 모든_속성이_비활성이면_종료된_과거상품_상세를_반환한다() {
        Long productId = productId("SEARCH_YOUTH_SAVING");
        jdbcTemplate.update(
                "UPDATE product_properties SET is_joinable = false WHERE product_id = ?",
                productId
        );
        jdbcTemplate.update("UPDATE provider SET apply_url = ? WHERE code = ?",
                "https://bank.example/apply", "SEARCH_BANK_B");
        jdbcTemplate.update("""
                INSERT INTO product_properties
                    (product_id, provider_id, base_rate, max_rate, save_trm, is_joinable)
                VALUES (
                    ?,
                    (SELECT id FROM provider WHERE code = 'SEARCH_BANK_B'),
                    2.00,
                    9.90,
                    24,
                    false
                )
                """, productId);

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(null, null), authenticatedUser());
        ProductDetailResponseDto locked = productDetailService.getProductDetail(
                productId, request(null, null), null);

        assertThat(detail.applyStatus()).isEqualTo(ProductApplyStatus.RECRUIT_CLOSED);
        assertThat(detail.applyUrl()).isNull();
        assertThat(detail.bank()).isNotNull();
        assertThat(detail.bank().maxRate()).isEqualTo(9.9);
        assertThat(detail.saveTrms()).containsExactly(12, 24);
        assertThat(locked.applyStatus()).isEqualTo(ProductApplyStatus.RECRUIT_CLOSED);
        assertThat(locked.applyUrl()).isNull();
        assertThat(locked.metricsLocked()).isTrue();
        assertThat(locked.bank()).isNull();
        assertThat(locked.rateTable()).isNull();
    }

    @Test
    void 비활성_property를_직접_조회하면_선택한_과거옵션만_반환한다() {
        Long productId = productId("SEARCH_YOUTH_SAVING");
        jdbcTemplate.update("UPDATE provider SET apply_url = ? WHERE code = ?",
                "https://bank.example/apply", "SEARCH_BANK_B");
        jdbcTemplate.update("""
                INSERT INTO product_properties
                    (product_id, provider_id, base_rate, max_rate, save_trm, is_joinable)
                VALUES (
                    ?,
                    (SELECT id FROM provider WHERE code = 'SEARCH_BANK_B'),
                    9.00,
                    9.90,
                    24,
                    false
                )
                """, productId);
        Long inactivePropertyId = jdbcTemplate.queryForObject(
                "SELECT id FROM product_properties WHERE product_id = ? AND save_trm = 24",
                Long.class,
                productId
        );
        jdbcTemplate.update("""
                INSERT INTO product_property_required_keyword
                    (product_property_id, keyword_code, effect, confidence)
                VALUES (?, 'STATUS_MILITARY', 'REQUIRE', 'HIGH')
                """, inactivePropertyId);

        ProductDetailResponseDto detail = productDetailService.getProductDetail(
                productId, request(inactivePropertyId, null), authenticatedUser());

        assertThat(detail.applyStatus()).isEqualTo(ProductApplyStatus.RECRUIT_CLOSED);
        assertThat(detail.applyUrl()).isNull();
        assertThat(detail.bank().maxRate()).isEqualTo(9.9);
        assertThat(detail.saveTrms()).containsExactly(24);
        assertThat(detail.rateTable())
                .extracting(row -> row.saveTrm())
                .containsExactly(24);
        assertThat(detail.keywords()).containsExactly(KeywordValueEnum.STATUS_MILITARY);
    }

    @Test
    void 존재하지_않는_상품은_예외를_던진다() {
        assertThatThrownBy(() -> productDetailService.getProductDetail(
                999_999L, request(null, null), authenticatedUser()))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(SearchErrorCode.PRODUCT_NOT_FOUND));
    }

    private Long productId(String productCode) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM product WHERE product_code = ?", Long.class, productCode);
    }

    private Long propertyId(String productCode) {
        return jdbcTemplate.queryForObject(
                "SELECT pp.id FROM product_properties pp JOIN product p ON p.id = pp.product_id WHERE p.product_code = ?",
                Long.class, productCode);
    }

    private AuthUserDetails authenticatedUser() {
        return new AuthUserDetails(1L, UserRole.RECOMMENDATION);
    }

    private ProductDetailRequestDto request(Long productPropertyId, Long monthlySavingsGoal) {
        return new ProductDetailRequestDto(
                productPropertyId,
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null, null, null, null,
                        monthlySavingsGoal, null, List.of()
                )
        );
    }

    // neverUsedBanks/maturedSavingBanks 비어있지 않게 채워 리스트 탭B(=includeTx)와 조건을 맞춘다.
    private DetailedOptionsDto detailedOptions(long monthlySavingsGoal) {
        return new DetailedOptionsDto(
                LocalDate.now().minusYears(27),
                30_000_000L, 3, 100, 12, null, true, null,
                monthlySavingsGoal, null,
                List.of(),
                List.of(),
                List.of()
        );
    }
}
