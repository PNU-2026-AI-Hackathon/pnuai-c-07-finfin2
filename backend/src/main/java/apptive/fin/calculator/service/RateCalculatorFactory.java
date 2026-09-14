package apptive.fin.calculator.service;

import apptive.fin.search.enums.ProductType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RateCalculatorFactory {

    private final Map<ProductType, RateCalculator> calculatorMap;

    public RateCalculatorFactory(List<RateCalculator> calculators) {
        this.calculatorMap = calculators.stream()
                .collect(Collectors.toMap(RateCalculator::supportedType, Function.identity()));
    }

    public RateCalculator getCalculator(ProductType productType) {
        RateCalculator calculator = calculatorMap.get(productType);
        if (calculator == null) {
            throw new IllegalArgumentException("지원하지 않는 상품 타입입니다: " + productType);
        }
        return calculator;
    }
}