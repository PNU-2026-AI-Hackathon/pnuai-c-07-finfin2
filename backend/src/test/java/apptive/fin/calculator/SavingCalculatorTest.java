package apptive.fin.calculator;

import apptive.fin.calculator.dto.CalculatorRequestDto;
import apptive.fin.calculator.dto.CalculatorResponseDto;
import apptive.fin.calculator.service.SavingRateCalculator;
import apptive.fin.search.InterestRateType;
import apptive.fin.search.ProductType;
import apptive.fin.search.ReserveType;
import apptive.fin.search.TaxType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SavingRateCalculatorTest {

    private final SavingRateCalculator calculator = new SavingRateCalculator();

    @Test
    @DisplayName("단리 + 일반과세: 납입원금 1,200,000 / 세전이자 26,000 / 세후 1,221,996원")
    void simpleInterest_generalTax() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                ProductType.SAVING,
                InterestRateType.SINGLE_INTEREST,
                ReserveType.FIXED,
                new BigDecimal("0.04"),
                new BigDecimal("100000"),
                12,
                TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.principal()).isEqualByComparingTo("1200000.00");
        assertThat(response.preTaxInterest()).isEqualByComparingTo("26000.00");
        assertThat(response.interestTax()).isEqualByComparingTo("4004.00");
        assertThat(response.afterTaxAmount()).isEqualByComparingTo("1221996.00");
    }

    @Test
    @DisplayName("월복리 + 일반과세: 세전이자 26,320.44 / 세후 1,222,267.09원")
    void compoundInterest_generalTax() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                ProductType.SAVING,
                InterestRateType.COMPOUND_INTEREST,
                ReserveType.FIXED,
                new BigDecimal("0.04"),
                new BigDecimal("100000"),
                12,
                TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.preTaxInterest()).isEqualByComparingTo("26320.44");
        assertThat(response.interestTax()).isEqualByComparingTo("4053.35");
        assertThat(response.afterTaxAmount()).isEqualByComparingTo("1222267.09");
    }

    @Test
    @DisplayName("정액/자유 적립방식은 계산 결과가 동일하다 (같은 산식 적용)")
    void fixedAndFreeReserveType_produceSameResult() {
        CalculatorRequestDto fixedRequest = new CalculatorRequestDto(
                ProductType.SAVING, InterestRateType.COMPOUND_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );
        CalculatorRequestDto freeRequest = new CalculatorRequestDto(
                ProductType.SAVING, InterestRateType.COMPOUND_INTEREST, ReserveType.FREE,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );

        CalculatorResponseDto fixedResponse = calculator.calculate(fixedRequest);
        CalculatorResponseDto freeResponse = calculator.calculate(freeRequest);

        assertThat(fixedResponse.afterTaxAmount()).isEqualByComparingTo(freeResponse.afterTaxAmount());
    }

    @Test
    @DisplayName("계산 가정 안내 문구가 항상 포함된다 (금소법 오인유발 방어)")
    void alwaysIncludesAssumptionNote() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                ProductType.SAVING, InterestRateType.SINGLE_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.assumptionNote()).isNotBlank();
    }

    @Test
    @DisplayName("금리 0%인 월복리는 0으로 나누기 없이 이자 0원을 반환한다")
    void zeroRate_compound_doesNotThrow() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                ProductType.SAVING, InterestRateType.COMPOUND_INTEREST, ReserveType.FIXED,
                BigDecimal.ZERO, new BigDecimal("100000"), 12, TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.preTaxInterest()).isEqualByComparingTo("0.00");
        assertThat(response.principal()).isEqualByComparingTo("1200000.00");
    }
}
