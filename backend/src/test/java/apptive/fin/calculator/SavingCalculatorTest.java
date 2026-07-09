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
                1L,
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
        assertThat(response.maturityAmount()).isEqualByComparingTo("1226000.00");
        assertThat(response.preTaxInterest()).isEqualByComparingTo("26000.00");
        assertThat(response.interestTax()).isEqualByComparingTo("4004.00");
        assertThat(response.afterTaxAmount()).isEqualByComparingTo("1221996.00");
    }

    @Test
    @DisplayName("월복리 + 일반과세: 세전이자 26,320.44 / 세후 1,222,267.09원")
    void compoundInterest_generalTax() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L,
                ProductType.SAVING,
                InterestRateType.COMPOUND_INTEREST,
                ReserveType.FIXED,
                new BigDecimal("0.04"),
                new BigDecimal("100000"),
                12,
                TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.maturityAmount()).isEqualByComparingTo("1226320.44");
        assertThat(response.preTaxInterest()).isEqualByComparingTo("26320.44");
        assertThat(response.interestTax()).isEqualByComparingTo("4053.35");
        assertThat(response.afterTaxAmount()).isEqualByComparingTo("1222267.09");
    }

    @Test
    @DisplayName("정액/자유 적립방식은 계산 결과가 동일하다 (같은 산식 적용)")
    void fixedAndFreeReserveType_produceSameResult() {
        CalculatorRequestDto fixedRequest = new CalculatorRequestDto(
                1L, ProductType.SAVING, InterestRateType.COMPOUND_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );
        CalculatorRequestDto freeRequest = new CalculatorRequestDto(
                1L, ProductType.SAVING, InterestRateType.COMPOUND_INTEREST, ReserveType.FREE,
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
                1L, ProductType.SAVING, InterestRateType.SINGLE_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.assumptionNote()).isNotBlank();
    }

    @Test
    @DisplayName("금리 0%인 월복리는 0으로 나누기 없이 이자 0원을 반환한다")
    void zeroRate_compound_doesNotThrow() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L, ProductType.SAVING, InterestRateType.COMPOUND_INTEREST, ReserveType.FIXED,
                BigDecimal.ZERO, new BigDecimal("100000"), 12, TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.preTaxInterest()).isEqualByComparingTo("0.00");
        assertThat(response.principal()).isEqualByComparingTo("1200000.00");
        assertThat(response.maturityAmount()).isEqualByComparingTo("1200000.00");
    }

    @Test
    @DisplayName("납입기간 1개월(n=1) 경계: 세전이자 333.33원 (= 100000 x (0.04/12) x 1)")
    void simpleInterest_oneMonthBoundary() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L, ProductType.SAVING, InterestRateType.SINGLE_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 1, TaxType.GENERAL
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.principal()).isEqualByComparingTo("100000.00");
        assertThat(response.preTaxInterest()).isEqualByComparingTo("333.33");
        assertThat(response.maturityAmount()).isEqualByComparingTo("100333.33");
    }

    @Test
    @DisplayName("비과세: 이자과세 0원, 세후 실수령액 = 원금 + 세전이자")
    void nonTax_noInterestTax() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L, ProductType.SAVING, InterestRateType.SINGLE_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.NON_TAX
        );

        CalculatorResponseDto response = calculator.calculate(request);

        assertThat(response.interestTax()).isEqualByComparingTo("0.00");
        assertThat(response.afterTaxAmount())
                .isEqualByComparingTo(response.principal().add(response.preTaxInterest()));
    }

    @Test
    @DisplayName("동일 입력이면 월복리 이자가 단리 이자보다 항상 크거나 같다")
    void compoundInterestIsGreaterThanOrEqualToSimple() {
        CalculatorRequestDto simpleRequest = new CalculatorRequestDto(
                1L, ProductType.SAVING, InterestRateType.SINGLE_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );
        CalculatorRequestDto compoundRequest = new CalculatorRequestDto(
                1L, ProductType.SAVING, InterestRateType.COMPOUND_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );

        BigDecimal simpleInterest = calculator.calculate(simpleRequest).preTaxInterest();
        BigDecimal compoundInterest = calculator.calculate(compoundRequest).preTaxInterest();

        assertThat(compoundInterest).isGreaterThanOrEqualTo(simpleInterest);
    }
}
