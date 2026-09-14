package apptive.fin.apicollector.product.service;

import apptive.fin.apicollector.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDisplayNameResolveIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ProductSyncService productSyncService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private long providerId;

    @BeforeEach
    void setUp() {
        // 상품 테이블만 비운다(product 삭제 시 product_properties는 CASCADE). product_source는 Flyway 시드 유지.
        jdbcTemplate.update("DELETE FROM product");
        jdbcTemplate.update("DELETE FROM provider");
        Long fssSourceId = jdbcTemplate.queryForObject(
                "SELECT id FROM product_source WHERE code = 'FSS'", Long.class);
        jdbcTemplate.update(
                "INSERT INTO provider (source_id, code, name) VALUES (?, 'P1', '테스트은행')", fssSourceId);
        providerId = jdbcTemplate.queryForObject(
                "SELECT id FROM provider WHERE code = 'P1'", Long.class);
    }

    @Test
    void resolvesDisplayNamesAcrossActiveProducts() {
        // FSS 충돌쌍: 괄호를 떼면 겹치므로 둘 다 원본 유지
        seedProduct("FSS", "FSS-A1", "A적금(자유적립식)", true);
        seedProduct("FSS", "FSS-A2", "A적금(정액적립식)", true);
        // FSS 단독: 괄호 제거
        seedProduct("FSS", "FSS-B1", "B적금(자유적립식)", true);
        // 수기(ONTONG): 끝 괄호가 있어도 그대로
        seedProduct("ONTONG", "ONT-D1", "D적금(2024)", true);

        int updated = productSyncService.resolveDisplayNames();

        assertThat(displayName("FSS-A1")).isEqualTo("A적금(자유적립식)");
        assertThat(displayName("FSS-A2")).isEqualTo("A적금(정액적립식)");
        assertThat(displayName("FSS-B1")).isEqualTo("B적금");
        assertThat(displayName("ONT-D1")).isEqualTo("D적금(2024)");
        // 실제로 바뀐 것은 B1 하나
        assertThat(updated).isEqualTo(1);
    }

    @Test
    void excludesInactiveProductsFromCollisionAndUpdate() {
        // 비활성(가입 불가) 상품은 판정·갱신에서 빠진다.
        // C적금(정액)은 비활성이므로, 활성 C적금(자유)는 겹치지 않아 괄호가 제거된다.
        seedProduct("FSS", "FSS-C1", "C적금(자유적립식)", true);
        seedProduct("FSS", "FSS-C2", "C적금(정액적립식)", false);

        int updated = productSyncService.resolveDisplayNames();

        assertThat(displayName("FSS-C1")).isEqualTo("C적금");
        // 비활성 상품은 로드되지 않아 원본 그대로
        assertThat(displayName("FSS-C2")).isEqualTo("C적금(정액적립식)");
        assertThat(updated).isEqualTo(1);
    }

    private void seedProduct(String sourceCode, String productCode, String originalName, boolean joinable) {
        Long sourceId = jdbcTemplate.queryForObject(
                "SELECT id FROM product_source WHERE code = ?", Long.class, sourceCode);
        // normalize 직후 상태: product_name은 원본과 동일하게 시작하고, resolve가 디스플레이를 확정한다.
        jdbcTemplate.update(
                "INSERT INTO product (source_id, type, product_code, product_name, original_name) VALUES (?, 'SAVING', ?, ?, ?)",
                sourceId, productCode, originalName, originalName);
        Long productId = jdbcTemplate.queryForObject(
                "SELECT id FROM product WHERE product_code = ?", Long.class, productCode);
        jdbcTemplate.update(
                "INSERT INTO product_properties (product_id, provider_id, is_joinable) VALUES (?, ?, ?)",
                productId, providerId, joinable);
    }

    private String displayName(String productCode) {
        return jdbcTemplate.queryForObject(
                "SELECT product_name FROM product WHERE product_code = ?", String.class, productCode);
    }
}
