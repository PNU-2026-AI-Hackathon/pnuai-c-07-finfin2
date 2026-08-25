package apptive.fin.apicollector.bankurl;

import apptive.fin.apicollector.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class BankProductUrlRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private BankProductUrlRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findsDistinctActiveFssTargetAndUpdatesAllActiveVariants() {
        Long sourceId = jdbcTemplate.queryForObject(
                "select id from product_source where code = 'FSS'", Long.class
        );
        Long providerId = jdbcTemplate.queryForObject("""
                insert into provider(source_id, code, name)
                values (?, 'TEST_BANK_URL', '테스트은행')
                returning id
                """, Long.class, sourceId);
        Long productId = jdbcTemplate.queryForObject("""
                insert into product(source_id, type, product_code, product_name)
                values (?, 'DEPOSIT', 'TEST_URL_PRODUCT', '테스트정기예금')
                returning id
                """, Long.class, sourceId);
        jdbcTemplate.update("""
                insert into product_properties(product_id, provider_id, is_joinable, save_trm)
                values (?, ?, true, 6), (?, ?, true, 12), (?, ?, false, 24)
                """, productId, providerId, productId, providerId, productId, providerId);

        var targets = repository.findActiveFssTargets().stream()
                .filter(target -> target.productId().equals(productId))
                .toList();
        int updated = repository.updateActiveFssProductUrl(
                productId, "TEST_BANK_URL", "https://bank.example/product"
        );

        assertThat(targets).singleElement().satisfies(target ->
                assertThat(target.productName()).isEqualTo("테스트정기예금")
        );
        assertThat(updated).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("""
                select count(*) from product_properties
                where product_id = ? and apply_url = 'https://bank.example/product'
                """, Integer.class, productId)).isEqualTo(2);
    }
}
