package apptive.fin.calculator;

import apptive.fin.calculator.dto.CalculatorRequestDto;
import apptive.fin.calculator.dto.CalculatorResponseDto;
import apptive.fin.calculator.service.CalculatorService;
import apptive.fin.calculator.service.RateCalculator;
import apptive.fin.calculator.service.RateCalculatorFactory;
import apptive.fin.search.InterestRateType;
import apptive.fin.search.ProductType;
import apptive.fin.search.ReserveType;
import apptive.fin.search.TaxType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {

    @Mock
    private RateCalculatorFactory calculatorFactory;

    @Mock
    private RateCalculator rateCalculator;

    @InjectMocks
    private CalculatorService calculatorService;

    private CalculatorResponseDto dummyResponse;

    @BeforeEach
    void setUp() {
        dummyResponse = new CalculatorResponseDto(
                new BigDecimal("1000000.00"),
                new BigDecimal("40000.00"),
                new BigDecimal("0.154"),
                new BigDecimal("6160.00"),
                new BigDecimal("1033840.00"),
                null
        );
    }

    @Test
    @DisplayName("예금 요청은 reserveType 없이도 정상적으로 팩토리에 위임된다")
    void deposit_withoutReserveType_delegatesToFactory() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                ProductType.DEPOSIT, InterestRateType.SINGLE_INTEREST, null,
                new BigDecimal("0.04"), new BigDecimal("1000000"), 12, TaxType.GENERAL
        );
        when(calculatorFactory.getCalculator(ProductType.DEPOSIT)).thenReturn(rateCalculator);
        when(rateCalculator.calculate(request)).thenReturn(dummyResponse);

        CalculatorResponseDto result = calculatorService.simulate(request);

        assertThat(result).isEqualTo(dummyResponse);
        verify(calculatorFactory).getCalculator(ProductType.DEPOSIT);
        verify(rateCalculator).calculate(request);
    }

    @Test
    @DisplayName("적금 요청에 reserveType이 없으면 IllegalArgumentException을 던진다")
    void saving_withoutReserveType_throwsException() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                ProductType.SAVING, InterestRateType.SINGLE_INTEREST, null,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );

        assertThatThrownBy(() -> calculatorService.simulate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reserveType");
    }

    @Test
    @DisplayName("적금 요청에 reserveType이 있으면 정상적으로 팩토리에 위임된다")
    void saving_withReserveType_delegatesToFactory() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                ProductType.SAVING, InterestRateType.SINGLE_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );
        when(calculatorFactory.getCalculator(ProductType.SAVING)).thenReturn(rateCalculator);
        when(rateCalculator.calculate(request)).thenReturn(dummyResponse);

        CalculatorResponseDto result = calculatorService.simulate(request);

        assertThat(result).isEqualTo(dummyResponse);
        verify(calculatorFactory).getCalculator(ProductType.SAVING);
    }
}
