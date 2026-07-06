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

public record CalculatorRequestDto(
    @NotNull Long productId, // 상품 ID (바운더리 검증용)

    @NotNull ProductType productType,
    @NotNull InterestRateType interestRateType,
    ReserveType reserveType,

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    BigDecimal appliedRate, // 적용 금리 (연이율, 소수) - 상품 max_rate 기준으로 검증

    @NotNull
    @Min(10_000) // 1만원 이상
    BigDecimal amount, // 월 납입액(원) - 상품 max_monthly_limit 기준으로 검증

    @NotNull
    @Min(1)
    Integer saveTrm, // 저축 기간(개월) - 상품 save_trm 옵션으로 검증

    @NotNull
    TaxType taxType // 과세 or 비과세
){}

