package apptive.fin.search.service;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductKeyword;
import apptive.fin.search.entity.ProductProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RateCalculatorService {
    public List<ProductRateDto> calculate(Product p, SearchRequestDto request) {
        List<ProductRateDto> result = new ArrayList<>();
        boolean isGov = p.getSource().getCode().equals("ONTONG");
        for (ProductProperty property : p.getProperties()) {
            if (hasKeyword(property, KeywordValueEnum.INTEREST_SAVINGS)) {
                result.add(ProductRateDto.builder()
                        .productId(p.getId())
                        .productPropertyId(property.getId())
                        .productName(p.getProductName())
                        .providerName(providerName(property))
                        .source(p.getSource().getCode())
                        .isSubscription(true)
                        .subscriptionNote("청약: 금리 비교 대상 아님")
                        .build());
                continue;
            }

            double base = baseRate(property);
            double achievableRate = isGov
                    ? base + contributionRate(property) + group1BankBonus(property)
                    : (effectiveRate(property));
            ProductRateDto productRate = ProductRateDto
                    .builder()
                    .productId(p.getId())
                    .productPropertyId(property.getId())
                    .productName(p.getProductName())
                    .providerName(providerName(property))
                    .source(p.getSource().getCode())
                    .baseRate(base)
                    .achievableRate(achievableRate)
                    .isSubscription(false)
                    .build();

            result.add(productRate);
        }

        return result;
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
