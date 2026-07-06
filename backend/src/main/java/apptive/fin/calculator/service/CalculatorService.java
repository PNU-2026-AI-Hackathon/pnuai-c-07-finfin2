package apptive.fin.calculator.service;

import apptive.fin.calculator.dto.CalculatorRequestDto;
import apptive.fin.calculator.dto.CalculatorResponseDto;
import apptive.fin.search.ProductType;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.repository.ProductPropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final RateCalculatorFactory calculatorFactory;
    private final ProductPropertyRepository productPropertyRepository;

    public CalculatorResponseDto simulate(CalculatorRequestDto request) {
        List<ProductProperty> properties = productPropertyRepository.findByProductId(request.productId());
        if (properties.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다: " + request.productId());
        }

        ProductProperty property = findMatchingProperty(properties, request.saveTrm());
        validateWithProductProperty(request, property);

        RateCalculator calculator = calculatorFactory.getCalculator(request.productType());
        return calculator.calculate(request);
    }

    private ProductProperty findMatchingProperty(List<ProductProperty> properties, Integer requestedSaveTrm) {
        // 요청한 저축 기간과 일치하는 property 찾기
        return properties.stream()
                .filter(p -> p.getSaveTrm() != null && p.getSaveTrm().equals(requestedSaveTrm))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 상품에서 지원하지 않는 저축 기간입니다: " + requestedSaveTrm + "개월. " +
                        "지원 가능한 기간: " + properties.stream()
                                .map(ProductProperty::getSaveTrm)
                                .filter(t -> t != null)
                                .distinct()
                                .sorted()
                                .toList()
                ));
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
