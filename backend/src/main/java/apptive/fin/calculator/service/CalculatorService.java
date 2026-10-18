package apptive.fin.calculator.service;

import apptive.fin.calculator.dto.CalculatorRequestDto;
import apptive.fin.calculator.dto.CalculatorResponseDto;
import apptive.fin.search.enums.ProductType;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.repository.ProductPropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final RateCalculatorFactory calculatorFactory;
    private final ProductPropertyRepository productPropertyRepository;

    public CalculatorResponseDto simulate(CalculatorRequestDto request) {
        ProductProperty property = productPropertyRepository.findById(request.productPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 옵션입니다: " + request.productPropertyId()));

        validateWithProductProperty(request, property);

        RateCalculator calculator = calculatorFactory.getCalculator(request.productType());
        return calculator.calculate(request);
    }

    private void validateWithProductProperty(CalculatorRequestDto request, ProductProperty property) {
        // 적금인 경우 reserveType 필수
        if (request.productType() == ProductType.SAVING && request.reserveType() == null) {
            throw new IllegalArgumentException("적금 계산기는 reserveType(적립 방식: 정액 또는 자유)이 필요합니다.");
        }

        // 금리 상한 검증 (maxRate는 % 단위, appliedRate는 소수 단위)
        if (property.getMaxRate() != null) {
            BigDecimal maxRateDecimal = property.getMaxRate().divide(BigDecimal.valueOf(100));
            if (request.appliedRate().compareTo(maxRateDecimal) > 0) {
                throw new IllegalArgumentException(
                        "적용 금리가 상품 최고금리를 초과합니다. 최고금리: " + property.getMaxRate() + "%"
                );
            }
        }

        // 금액 상한 검증 (예금: 예치금, 적금: 월 납입액)
        if (property.getMaxMonthlyLimit() != null) {
            BigDecimal maxLimit = BigDecimal.valueOf(property.getMaxMonthlyLimit());
            if (request.amount().compareTo(maxLimit) > 0) {
                throw new IllegalArgumentException(
                        "금액이 상품 최대 한도를 초과합니다. 최대 한도: " + property.getMaxMonthlyLimit() + "원"
                );
            }
        }
    }
}
