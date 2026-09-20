package apptive.fin.calculator.service;

import apptive.fin.calculator.dto.CalculatorRequestDto;
import apptive.fin.calculator.dto.CalculatorResponseDto;
import apptive.fin.search.enums.InterestRateType;
import apptive.fin.search.enums.ProductType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/** 예금 계산기 **/

@Component
public class DepositRateCalculator implements  RateCalculator {
    private static final int SCALE = 2;
    private static final MathContext MC = new MathContext(20);
    private static final BigDecimal TWELVE = new BigDecimal("12");

    @Override
    public ProductType supportedType() {
        return ProductType.DEPOSIT;
    }

    @Override
    public CalculatorResponseDto calculate(CalculatorRequestDto request){
        BigDecimal principal = request.amount();
        BigDecimal r = request.appliedRate();
        int n = request.saveTrm();

        BigDecimal preTaxInterest;
        BigDecimal maturityAmount;

        if (request.interestRateType() == InterestRateType.SINGLE_INTEREST) {
            // P × r × (n/12)
            BigDecimal nOverTwelve = BigDecimal.valueOf(n).divide(TWELVE, MC);
            preTaxInterest = principal.multiply(r, MC).multiply(nOverTwelve, MC);
            maturityAmount = principal.add(preTaxInterest);
        } else {
            // 월복리: 만기금액 = P × (1 + r/12)^n
            BigDecimal monthlyRate = r.divide(TWELVE, MC);
            BigDecimal growthFactor = BigDecimal.ONE.add(monthlyRate).pow(n, MC);
            maturityAmount = principal.multiply(growthFactor, MC);
            preTaxInterest = maturityAmount.subtract(principal);
        }

        preTaxInterest = preTaxInterest.setScale(SCALE, RoundingMode.HALF_UP);
        maturityAmount = maturityAmount.setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal taxRate = request.taxType().getRate();
        BigDecimal interestTax = preTaxInterest.multiply(taxRate, MC).setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal afterTaxAmount = principal.add(preTaxInterest).subtract(interestTax);

        return new CalculatorResponseDto(
                principal.setScale(SCALE, RoundingMode.HALF_UP),
                maturityAmount,
                preTaxInterest,
                taxRate,
                interestTax,
                afterTaxAmount.setScale(SCALE, RoundingMode.HALF_UP),
                null // 예금은 거치식이라 계산 가정 문구 불필요
        );

    }
}
