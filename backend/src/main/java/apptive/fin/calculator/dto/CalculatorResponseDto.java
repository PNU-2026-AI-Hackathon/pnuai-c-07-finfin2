package apptive.fin.calculator.dto;

import java.math.BigDecimal;

public record CalculatorResponseDto(
        BigDecimal principal,
        BigDecimal preTaxInterest,
        BigDecimal taxRate,
        BigDecimal interestTax,
        BigDecimal afterTaxAmount,
        String assumptionNote
){ }
