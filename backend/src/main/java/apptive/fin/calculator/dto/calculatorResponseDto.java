package apptive.fin.calculator.dto;

import java.math.BigDecimal;

public record calculatorResponseDto (
        BigDecimal principal,
        BigDecimal preTaxInterest,
        BigDecimal taxRate,
        BigDecimal interestTax,
        BigDecimal afterTaxAmount,
        String assumptionNote
){ }
