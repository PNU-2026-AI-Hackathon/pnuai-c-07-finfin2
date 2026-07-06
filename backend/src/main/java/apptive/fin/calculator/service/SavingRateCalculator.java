package apptive.fin.calculator.service;

import apptive.fin.calculator.dto.CalculatorRequestDto;
import apptive.fin.calculator.dto.CalculatorResponseDto;
import apptive.fin.search.InterestRateType;
import apptive.fin.search.ProductType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/** 적금 계산기 **/

@Component
public class SavingRateCalculator implements RateCalculator {
    private static final int SCALE = 2;
    private static final MathContext MC = new MathContext(20);
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    private static final String ASSUMPTION_NOTE = "매월 초 동일액을 납입하는 것으로 가정한 계산이며, 실제 납입 시점·금액에 따라 수령액이 달라질 수 있어요.";

    @Override
    public ProductType supportedType() {
        return ProductType.SAVING;
    }

    @Override
    public CalculatorResponseDto calculate(CalculatorRequestDto request) {
        BigDecimal monthlyPayment = request.amount();
        BigDecimal r = request.appliedRate();
        int n = request.saveTrm();

        BigDecimal principal = monthlyPayment.multiply(BigDecimal.valueOf(n)); // A × n
        BigDecimal monthlyRate = r.divide(TWELVE, MC); // i = r / 12

        BigDecimal preTaxInterest;

        if (request.interestRateType() == InterestRateType.SINGLE_INTEREST) {
            // A × i × [n(n+1)/2]
            BigDecimal sumOfMonths = BigDecimal.valueOf((long) n * (n + 1))
                    .divide(BigDecimal.valueOf(2), MC);
            preTaxInterest = monthlyPayment.multiply(monthlyRate, MC).multiply(sumOfMonths, MC);
        } else {
            if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
                // 금리 0%면 이자 없음 (0으로 나누기 방지)
                preTaxInterest = BigDecimal.ZERO;
            } else {
                // 만기금액 = A × (1+i) × {(1+i)^n − 1} / i
                BigDecimal onePlusI = BigDecimal.ONE.add(monthlyRate);
                BigDecimal growthFactor = onePlusI.pow(n, MC).subtract(BigDecimal.ONE);
                BigDecimal maturityAmount = monthlyPayment
                        .multiply(onePlusI, MC)
                        .multiply(growthFactor, MC)
                        .divide(monthlyRate, MC);
                preTaxInterest = maturityAmount.subtract(principal);
            }
        }

        preTaxInterest = preTaxInterest.setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal taxRate = request.taxType().getRate();
        BigDecimal interestTax = preTaxInterest.multiply(taxRate, MC).setScale(SCALE, RoundingMode.HALF_UP);
        BigDecimal afterTaxAmount = principal.add(preTaxInterest).subtract(interestTax);

        return new CalculatorResponseDto(
                principal.setScale(SCALE, RoundingMode.HALF_UP),
                preTaxInterest,
                taxRate,
                interestTax,
                afterTaxAmount.setScale(SCALE, RoundingMode.HALF_UP),
                ASSUMPTION_NOTE
        );
    }
}
