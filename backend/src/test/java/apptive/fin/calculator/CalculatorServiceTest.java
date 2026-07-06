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
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.repository.ProductPropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

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

    @Mock
    private ProductPropertyRepository productPropertyRepository;

    @InjectMocks
    private CalculatorService calculatorService;

    private CalculatorResponseDto dummyResponse;
    private ProductProperty dummyProperty;

    @BeforeEach
    void setUp() {
        dummyResponse = new CalculatorResponseDto(
                new BigDecimal("1000000.00"),
                new BigDecimal("1040000.00"),
                new BigDecimal("40000.00"),
                new BigDecimal("0.154"),
                new BigDecimal("6160.00"),
                new BigDecimal("1033840.00"),
                null
        );

        dummyProperty = new ProductProperty();
        ReflectionTestUtils.setField(dummyProperty, "saveTrm", 12);
        ReflectionTestUtils.setField(dummyProperty, "maxRate", new BigDecimal("5.00")); // 5%
        ReflectionTestUtils.setField(dummyProperty, "maxMonthlyLimit", 10000000L); // 1천만원
    }

    @Test
    @DisplayName("예금 요청은 reserveType 없이도 정상적으로 팩토리에 위임된다")
    void deposit_withoutReserveType_delegatesToFactory() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L, ProductType.DEPOSIT, InterestRateType.SINGLE_INTEREST, null,
                new BigDecimal("0.04"), new BigDecimal("1000000"), 12, TaxType.GENERAL
        );
        when(productPropertyRepository.findByProductId(1L)).thenReturn(List.of(dummyProperty));
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
                1L, ProductType.SAVING, InterestRateType.SINGLE_INTEREST, null,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );
        when(productPropertyRepository.findByProductId(1L)).thenReturn(List.of(dummyProperty));

        assertThatThrownBy(() -> calculatorService.simulate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reserveType");
    }

    @Test
    @DisplayName("적금 요청에 reserveType이 있으면 정상적으로 팩토리에 위임된다")
    void saving_withReserveType_delegatesToFactory() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L, ProductType.SAVING, InterestRateType.SINGLE_INTEREST, ReserveType.FIXED,
                new BigDecimal("0.04"), new BigDecimal("100000"), 12, TaxType.GENERAL
        );
        when(productPropertyRepository.findByProductId(1L)).thenReturn(List.of(dummyProperty));
        when(calculatorFactory.getCalculator(ProductType.SAVING)).thenReturn(rateCalculator);
        when(rateCalculator.calculate(request)).thenReturn(dummyResponse);

        CalculatorResponseDto result = calculatorService.simulate(request);

        assertThat(result).isEqualTo(dummyResponse);
        verify(calculatorFactory).getCalculator(ProductType.SAVING);
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 요청하면 IllegalArgumentException을 던진다")
    void nonExistentProductId_throwsException() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                999L, ProductType.DEPOSIT, InterestRateType.SINGLE_INTEREST, null,
                new BigDecimal("0.04"), new BigDecimal("1000000"), 12, TaxType.GENERAL
        );
        when(productPropertyRepository.findByProductId(999L)).thenReturn(List.of());

        assertThatThrownBy(() -> calculatorService.simulate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 상품");
    }

    @Test
    @DisplayName("상품에서 지원하지 않는 저축 기간으로 요청하면 IllegalArgumentException을 던진다")
    void unsupportedSaveTrm_throwsException() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L, ProductType.DEPOSIT, InterestRateType.SINGLE_INTEREST, null,
                new BigDecimal("0.04"), new BigDecimal("1000000"), 24, TaxType.GENERAL
        );
        when(productPropertyRepository.findByProductId(1L)).thenReturn(List.of(dummyProperty)); // saveTrm=12만 지원

        assertThatThrownBy(() -> calculatorService.simulate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 저축 기간");
    }

    @Test
    @DisplayName("상품 최고금리를 초과하면 IllegalArgumentException을 던진다")
    void exceedMaxRate_throwsException() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L, ProductType.DEPOSIT, InterestRateType.SINGLE_INTEREST, null,
                new BigDecimal("0.10"), // 10% (상품 최고금리 5% 초과)
                new BigDecimal("1000000"), 12, TaxType.GENERAL
        );
        when(productPropertyRepository.findByProductId(1L)).thenReturn(List.of(dummyProperty));

        assertThatThrownBy(() -> calculatorService.simulate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최고금리");
    }

    @Test
    @DisplayName("상품 최대 한도를 초과하면 IllegalArgumentException을 던진다")
    void exceedMaxMonthlyLimit_throwsException() {
        CalculatorRequestDto request = new CalculatorRequestDto(
                1L, ProductType.DEPOSIT, InterestRateType.SINGLE_INTEREST, null,
                new BigDecimal("0.04"),
                new BigDecimal("20000000"), // 2천만원 (상품 최대 한도 1천만원 초과)
                12, TaxType.GENERAL
        );
        when(productPropertyRepository.findByProductId(1L)).thenReturn(List.of(dummyProperty));

        assertThatThrownBy(() -> calculatorService.simulate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최대 한도");
    }
}
