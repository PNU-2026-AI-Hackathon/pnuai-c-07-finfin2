package apptive.fin.calculator.dto;

import apptive.fin.search.InterestRateType;
import apptive.fin.search.ProductType;
import apptive.fin.search.ReserveType;
import apptive.fin.search.TaxType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record calculatorRequestDto (
    @NotNull ProductType productType,
    @NotNull InterestRateType interestRateType,
    ReserveType reserveType,

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1.0") // 소수 기준 (4% = 0.04)
    BigDecimal appliedRate, // 적용 금리 (연이율, 소수)

    @NotNull
    @Min(10_000) // 1만원 이상
    BigDecimal amount,

    @NotNull
    @Min(1)
    Integer saveTrm, // 저축 기간(개월)

    @NotNull
    TaxType taxType
){}

