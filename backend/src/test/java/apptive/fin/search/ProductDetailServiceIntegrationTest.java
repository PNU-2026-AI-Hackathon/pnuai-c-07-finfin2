package apptive.fin.search;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductDetailRequestDto;
import apptive.fin.search.dto.ProductDetailResponseDto;
import apptive.fin.search.service.ProductDetailService;
import apptive.fin.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = "/sql/search-products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup-product-fixtures.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ProductDetailServiceIntegrationTest {

    @Autowired
    private ProductDetailService productDetailService;

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
}
