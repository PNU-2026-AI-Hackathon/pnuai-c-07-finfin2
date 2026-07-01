package apptive.fin.provider;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankDisplayRegistryTest {

    private final BankDisplayRegistry registry = new BankDisplayRegistry();

    @Test
    void FSS_등기명이_실제_브랜드명으로_정규화된다() {
        assertThat(registry.displayNameOrFallback("0013909", "주식회사 하나은행")).isEqualTo("하나은행");
        assertThat(registry.displayNameOrFallback("0015130", "주식회사 카카오뱅크")).isEqualTo("카카오뱅크");
        assertThat(registry.displayNameOrFallback("0017801", "토스뱅크 주식회사")).isEqualTo("토스뱅크");
        assertThat(registry.displayNameOrFallback("0013175", "농협은행주식회사")).isEqualTo("NH농협은행");
        assertThat(registry.displayNameOrFallback("0010002", "한국스탠다드차타드은행")).isEqualTo("SC제일은행");
    }

    @Test
    void 매핑에_없는_코드는_원본명으로_폴백한다() {
        assertThat(registry.displayNameOrFallback("9999999", "알수없는은행")).isEqualTo("알수없는은행");
        assertThat(registry.categoryOrFallback("9999999")).isEqualTo("기타");
        assertThat(registry.region("9999999")).isNull();
    }

    @Test
    void 코드가_null이어도_예외없이_원본명으로_폴백한다() {
        assertThat(registry.displayNameOrFallback(null, "이름")).isEqualTo("이름");
        assertThat(registry.find(null)).isEmpty();
    }

    @Test
    void 지방은행은_지역_코드를_갖는다() {
        assertThat(registry.region("0010017")).isEqualTo("reg_05");
        assertThat(registry.categoryOrFallback("0010017")).isEqualTo("지방");
    }
}
