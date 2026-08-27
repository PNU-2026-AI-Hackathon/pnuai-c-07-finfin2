package apptive.fin.calculator.service;

import apptive.fin.calculator.dto.CalculatorRequestDto;
import apptive.fin.calculator.dto.CalculatorResponseDto;
import apptive.fin.search.enums.ProductType;

public interface RateCalculator {

    ProductType supportedType();

    CalculatorResponseDto calculate(CalculatorRequestDto request);
}
