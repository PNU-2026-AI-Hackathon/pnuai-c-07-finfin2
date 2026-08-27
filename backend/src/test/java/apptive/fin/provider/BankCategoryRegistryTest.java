package apptive.fin.provider;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankCategoryRegistryTest {

    private final BankCategoryRegistry registry = new BankCategoryRegistry();

    @Test
    void 은행_코드로_카테고리를_반환한다() {
        assertThat(registry.categoryOrFallback("0010927")).isEqualTo("시중");
        assertThat(registry.categoryOrFallback("0015130")).isEqualTo("인터넷");
        assertThat(registry.categoryOrFallback("0013175")).isEqualTo("특수");
        assertThat(registry.categoryOrFallback("0010017")).isEqualTo("지방");
    }

    @Test
    void 매핑에_없는_코드는_기타로_폴백한다() {
        assertThat(registry.categoryOrFallback("9999999")).isEqualTo("기타");
        assertThat(registry.categoryOrFallback(null)).isEqualTo("기타");
    }
}
