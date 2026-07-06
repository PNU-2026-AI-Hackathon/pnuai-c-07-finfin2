package apptive.fin.calculator.service;

import apptive.fin.calculator.dto.CalculatorRequestDto;
import apptive.fin.calculator.dto.CalculatorResponseDto;
import apptive.fin.search.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final RateCalculatorFactory calculatorFactory;

    public CalculatorResponseDto simulate(CalculatorRequestDto request) {
        validate(request);
        RateCalculator calculator = calculatorFactory.getCalculator(request.productType());
        return calculator.calculate(request);
    }

    private void validate(CalculatorRequestDto request) {
        if (request.productType() == ProductType.SAVING && request.reserveType() == null) {
            throw new IllegalArgumentException("적금 계산기는 reserveType(적립 방식: 정액 또는 자유)이 필요합니다.");
        }

    }
}
