package apptive.fin.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayMigrationIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 빈_DB는_Flyway_마이그레이션으로_초기화된다() {
        List<String> versions = jdbcTemplate.queryForList(
                "SELECT version FROM flyway_schema_history WHERE success = TRUE ORDER BY installed_rank",
                String.class
        );
        Integer monthlyIncome = jdbcTemplate.queryForObject(
                "SELECT monthly_income FROM median_incomes "
                        + "WHERE year = 2026 AND household_size = 1 AND earn_percent = 100",
                Integer.class
        );

        assertThat(versions).contains("1", "2", "3", "4");
        assertThat(monthlyIncome).isEqualTo(256);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT to_regclass('public.product_raw')::text",
                String.class
        )).isEqualTo("product_raw");
    }
}
