package apptive.fin.search.service;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductKeyword;
import apptive.fin.search.entity.ProductProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RateCalculatorService {
    public ProductRateDto calculate(Product p, SearchRequestDto request) {
        boolean isGov = p.getSource().getCode().equals("ONTONG");

        boolean isSubscription = p.getProperties().stream()
                .anyMatch(property -> hasKeyword(property,KeywordValueEnum.INTEREST_SAVINGS));
        if (isSubscription) {
            return ProductRateDto.builder()
                    .productId(p.getId())
                    .productPropertyId(null)
                    .productName(p.getProductName())
                    .providerName(null)
                    .source(p.getSource().getCode())
                    .isSubscription(true)
                    .subscriptionNote("청약: 금리 비교 대상 아님")
                    .build();
        }
        // 최고 금리 property 선택
        ProductProperty bestProperty = p.getProperties().stream()
                .max(Comparator.comparingDouble(this::effectiveRate))
                .orElse(null);

        double base = baseRate(bestProperty);
        double achievableRate = isGov
                ? base + contributionRate(bestProperty) + group1BankBonus(bestProperty)
                : effectiveRate(bestProperty);

        return ProductRateDto.builder()
                .productId(p.getId())
                .productPropertyId(bestProperty != null ? bestProperty.getId() : null)
                .productName(p.getProductName())
                .providerName(providerName(bestProperty))
                .source(p.getSource().getCode())
                .baseRate(base)
                .achievableRate(achievableRate)
                .isSubscription(false)
                .build();
    }

    private boolean hasKeyword(ProductProperty property, KeywordValueEnum keyword) {
        return property.getKeywords().stream()
                .map(ProductKeyword::getKeywordCode)
                .anyMatch(keyword::equals);
    }

    private boolean isGroup1Product(ProductProperty p) {
        return p.getKeywords().stream()
                .map(ProductKeyword::getKeywordCode)
                .anyMatch(keyword -> keyword == KeywordValueEnum.STATUS_MILITARY
                        || keyword == KeywordValueEnum.STATUS_SME_WORKER);
    }

    private double group1BankBonus(ProductProperty p) {
        if (!isGroup1Product(p) || p.getMaxRate() == null || p.getBaseRate() == null)
            return 0.0;

        return p.getMaxRate().doubleValue() - p.getBaseRate().doubleValue();
    }

    private double contributionRate(ProductProperty property) {
        return property != null && property.getGovContributionRate() != null
                ? property.getGovContributionRate().doubleValue()
                : 0.0;
    }

    private double baseRate(ProductProperty property) {
        return property != null && property.getBaseRate() != null
                ? property.getBaseRate().doubleValue()
                : 0.0;
    }

    private double effectiveRate(ProductProperty property) {
        if (property.getMaxRate() != null) {
            return property.getMaxRate().doubleValue();
        }
        if (property.getBaseRate() != null) {
            return property.getBaseRate().doubleValue();
        }
        return 0.0;
    }

    private String providerName(ProductProperty property) {
        return property != null && property.getProvider() != null
                ? property.getProvider().getName()
                : null;
    }
}
