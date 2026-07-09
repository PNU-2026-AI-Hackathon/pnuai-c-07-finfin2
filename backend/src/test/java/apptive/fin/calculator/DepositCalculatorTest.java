package apptive.fin.calculator;

import apptive.fin.calculator.dto.CalculatorRequestDto;
import apptive.fin.calculator.dto.CalculatorResponseDto;
import apptive.fin.calculator.service.DepositRateCalculator;
import apptive.fin.search.InterestRateType;
import apptive.fin.search.ProductType;
import apptive.fin.search.TaxType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DepositRateCalculatorTest {

    private final DepositRateCalculator calculator = new DepositRateCalculator();

    @Test
    @DisplayName("단리 + 일반과세: 세전이자 40,000원 / 과세 6,160원 / 세후 1,033,840원")
    void simpleInterest_generalTax() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L,
                ProductType.DEPOSIT,
                InterestRateType.SINGLE_INTEREST,
                null,
                new BigDecimal("0.04"),
                new BigDecimal("1000000"),
                12,
                TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.principal()).isEqualByComparingTo("1000000.00");
        assertThat(response.maturityAmount()).isEqualByComparingTo("1040000.00");
        assertThat(response.preTaxInterest()).isEqualByComparingTo("40000.00");
        assertThat(response.interestTax()).isEqualByComparingTo("6160.00");
        assertThat(response.afterTaxAmount()).isEqualByComparingTo("1033840.00");
    }

    @Test
    @DisplayName("단리 + 비과세: 세전이자와 세후 실수령액이 동일 (과세 0원)")
    void simpleInterest_nonTax() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L,
                ProductType.DEPOSIT,
                InterestRateType.SINGLE_INTEREST,
                null,
                new BigDecimal("0.04"),
                new BigDecimal("1000000"),
                12,
                TaxType.NON_TAX
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.interestTax()).isEqualByComparingTo("0.00");
        assertThat(response.afterTaxAmount()).isEqualByComparingTo("1040000.00");
    }

    @Test
    @DisplayName("월복리 + 일반과세: 세전이자 40,741.54원 / 세후 1,034,467.34원")
    void compoundInterest_generalTax() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L,
                ProductType.DEPOSIT,
                InterestRateType.COMPOUND_INTEREST,
                null,
                new BigDecimal("0.04"),
                new BigDecimal("1000000"),
                12,
                TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.maturityAmount()).isEqualByComparingTo("1040741.54");
        assertThat(response.preTaxInterest()).isEqualByComparingTo("40741.54");
        assertThat(response.interestTax()).isEqualByComparingTo("6274.20");
        assertThat(response.afterTaxAmount()).isEqualByComparingTo("1034467.34");
    }

    @Test
    @DisplayName("월복리 이자가 단리 이자보다 항상 크거나 같다")
    void compoundInterestIsGreaterThanOrEqualToSimple() {
        CalculatorRequestDto simpleRequest = new CalculatorRequestDto(
                1L, ProductType.DEPOSIT, InterestRateType.SINGLE_INTEREST, null,
                new BigDecimal("0.04"), new BigDecimal("1000000"), 12, TaxType.GENERAL
        );
        CalculatorRequestDto compoundRequest = new CalculatorRequestDto(
                1L, ProductType.DEPOSIT, InterestRateType.COMPOUND_INTEREST, null,
                new BigDecimal("0.04"), new BigDecimal("1000000"), 12, TaxType.GENERAL
        );

        BigDecimal simpleInterest = calculator.calculate(simpleRequest).preTaxInterest();
        BigDecimal compoundInterest = calculator.calculate(compoundRequest).preTaxInterest();

        assertThat(compoundInterest).isGreaterThanOrEqualTo(simpleInterest);
    }

    @Test
    @DisplayName("금리 0%면 이자와 과세 모두 0원")
    void zeroRate_noInterest() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L, ProductType.DEPOSIT, InterestRateType.COMPOUND_INTEREST, null,
                BigDecimal.ZERO, new BigDecimal("1000000"), 12, TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.preTaxInterest()).isEqualByComparingTo("0.00");
        assertThat(response.interestTax()).isEqualByComparingTo("0.00");
        assertThat(response.maturityAmount()).isEqualByComparingTo("1000000.00");
        assertThat(response.afterTaxAmount()).isEqualByComparingTo("1000000.00");
    }
}
