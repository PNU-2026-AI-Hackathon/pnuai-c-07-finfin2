package apptive.fin.search.service;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

// 탭 B - 금리 높은 순
@Service
public class RateCalculatorService {
    public ProductRateDto calculate(Product p, SearchRequestDto request) {
        List<KeywordValueEnum> bankConditions = request.options().stream()
                .map(opt -> KeywordValueEnum.from(String.valueOf(opt.optionId())))
                .filter(kw -> kw == KeywordValueEnum.BANK_SALARY_TRANSFER
                        || kw == KeywordValueEnum.BANK_CARD_USAGE
                        || kw == KeywordValueEnum.BANK_FIRST_TRANSACTION)
                .toList();

        // FSS - 유저 조건에 맞는 상품 중 최고 금리
        if(!p.getOptions().isEmpty()){
            double bestRate = p.getOptions().stream()
                    .map(opt -> opt.getIntrRate2()!= null
                            ? opt.getIntrRate2().doubleValue()
                            : opt.getIntrRate().doubleValue())
                    .max(Comparator.naturalOrder())
                    .orElse(0.0);

            return ProductRateDto.builder()
                    .productId(p.getId())
                    .productName(p.getProductName())
                    .source(p.getSource().getCode())
                    .baseRate(p.getBaseRate() != null ? p.getBaseRate().doubleValue(): 0.0)
                    .achievableRate(bestRate)
                    .isSubscription(false)
                    .build();
        }

        // 온통청년 - 청약 여부 체크
        boolean isSubscription = p.getKeywords().stream()
                .anyMatch(k -> k.getKeywordCode() == KeywordValueEnum.INTEREST_SAVINGS);

        if(isSubscription){
            return ProductRateDto.builder()
                    .productId(p.getId())
                    .productName(p.getProductName())
                    .source(p.getSource().getCode())
                    .isSubscription(true)
                    .subscriptionNote("청약: 금리 비교 대상 아님")
                    .build();
        }

        double baseRate = p.getBaseRate() != null ? p.getBaseRate().doubleValue() : 0.0;
        double maxRate = p.getMaxRate() != null ? p.getMaxRate().doubleValue() : baseRate;

        return ProductRateDto.builder()
                .productId(p.getId())
                .productName(p.getProductName())
                .source(p.getSource().getCode())
                .baseRate(baseRate)
                .achievableRate(maxRate)
                .isSubscription(false)
                .build();

    }
}
