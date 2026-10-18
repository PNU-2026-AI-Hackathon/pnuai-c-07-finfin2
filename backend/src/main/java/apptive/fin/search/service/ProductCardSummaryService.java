package apptive.fin.search.service;

import apptive.fin.search.dto.EligibleProductOption;
import apptive.fin.search.dto.GovernmentDetailDto;
import apptive.fin.search.dto.ProductCardSummaryDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class ProductCardSummaryService {

    private final MatchScoreService matchScoreService;
    private final RateCalculatorService rateCalculatorService;
    private final ProductDisplayKeywordService productDisplayKeywordService;

    public List<ProductCardSummaryDto> build(
            List<EligibleProductOption> eligible,
            List<ProductMatchDto> governmentRanked,
            List<ProductMatchDto> bankRanked,
            List<ProductRateDto> governmentRateRanked,
            List<ProductRateDto> bankRateRanked,
            SearchRequestDto request,
            ResolvedKeywords resolvedKeywords,
            boolean tabBEnabled,
            Double bankMaxInterestThreshold
    ) {
        List<EligibleProductOption> cardOptions = cardOptions(
                eligible,
                governmentRanked,
                bankRanked,
                governmentRateRanked,
                bankRateRanked
        );

        // productId, ProductProperty[]
        Map<Long, List<ProductProperty>> propertiesByProductId = new LinkedHashMap<>();
        for (EligibleProductOption option : eligible) {
            propertiesByProductId
                    .computeIfAbsent(option.product().getId(), ignored -> new ArrayList<>())
                    .add(option.property());
        }

        Map<Long, Double> matchScoreByPropertyId = indexMatchScoresByPropertyId(governmentRanked, bankRanked);
        Map<Long, ProductRateDto> rateByPropertyId = indexRatesByPropertyId(governmentRateRanked, bankRateRanked);

        List<ProductCardSummaryDto> summaries = new ArrayList<>();
        for (EligibleProductOption option : cardOptions) {
            summaries.add(buildSummary(
                    option,
                    propertiesByProductId,
                    matchScoreByPropertyId,
                    rateByPropertyId,
                    request,
                    resolvedKeywords,
                    tabBEnabled,
                    bankMaxInterestThreshold
            ));
        }
        return List.copyOf(summaries);
    }

    private Map<Long, ProductRateDto> indexRatesByPropertyId(List<ProductRateDto> goverment, List<ProductRateDto> bank) {
        Map<Long, ProductRateDto> rateByPropertyId = new LinkedHashMap<>();
        addRates(rateByPropertyId, goverment);
        addRates(rateByPropertyId, bank);

        return rateByPropertyId;
    }

    private Map<Long, Double> indexMatchScoresByPropertyId(List<ProductMatchDto> goverment, List<ProductMatchDto> bank) {
        Map<Long, Double> matchScoreByPropertyId = new LinkedHashMap<>();
        addMatchScores(matchScoreByPropertyId, goverment);
        addMatchScores(matchScoreByPropertyId, bank);

        return matchScoreByPropertyId;
    }

    private ProductCardSummaryDto buildSummary(
            EligibleProductOption option,
            Map<Long, List<ProductProperty>> propertiesByProductId,
            Map<Long, Double> matchScoreByPropertyId,
            Map<Long, ProductRateDto> rateByPropertyId,
            SearchRequestDto request,
            ResolvedKeywords resolvedKeywords,
            boolean tabBEnabled,
            Double bankMaxInterestThreshold
    ) {
        Product product = option.product();
        ProductProperty property = option.property();
        List<ProductProperty> productProperties = propertiesByProductId
                .getOrDefault(product.getId(), List.of());

        Double matchScore = matchScoreByPropertyId.get(property.getId());
        if (matchScore == null) {
            boolean includeTransactions = product.isBank() && tabBEnabled;
            matchScore = matchScoreService.score(
                    product,
                    property,
                    request,
                    resolvedKeywords,
                    includeTransactions,
                    bankMaxInterestThreshold
            ).totalScore();
        }

        ProductRateDto rate = rateByPropertyId.get(property.getId());
        if (rate == null && tabBEnabled && product.getType() != ProductType.SUBSCRIPTION) {
            rate = rateCalculatorService.calculate(product, property, request, resolvedKeywords);
        }

        GovernmentDetailDto government = null;
        if (tabBEnabled && product.isGovernment() && product.getType() != ProductType.SUBSCRIPTION) {
            government = rateCalculatorService.governmentDetail(property, request);
        }

        return new ProductCardSummaryDto(
                product.getId(),
                property.getId(),
                product.getType(),
                badgeKeywords(product, property, bankMaxInterestThreshold),
                saveTrms(productProperties),
                property.getMinMonthlyLimit(),
                property.getMaxMonthlyLimit(),
                matchScore,
                property.getBaseRate() != null ? property.getBaseRate().doubleValue() : null,
                property.getMaxRate() != null ? property.getMaxRate().doubleValue() : null,
                rate != null && rate.rateComparable() ? rate.achievableRate() : null,
                government != null ? government.expectedTotalContribution() : null,
                government != null ? government.effectiveMonthlyDeposit() : null,
                government != null ? government.contributionPeriodMonths() : null
        );
    }

    private List<EligibleProductOption> cardOptions(
            List<EligibleProductOption> eligible,
            List<ProductMatchDto> governmentRanked,
            List<ProductMatchDto> bankRanked,
            List<ProductRateDto> governmentRateRanked,
            List<ProductRateDto> bankRateRanked
    ) {
        Map<Long, EligibleProductOption> optionByPropertyId = new LinkedHashMap<>();
        for (EligibleProductOption option : eligible) {
            optionByPropertyId.putIfAbsent(option.property().getId(), option);
        }

        Set<Long> propertyIds = new LinkedHashSet<>();
        addPropertyIds(propertyIds, governmentRanked);
        addPropertyIds(propertyIds, bankRanked);
        addRatePropertyIds(propertyIds, governmentRateRanked);
        addRatePropertyIds(propertyIds, bankRateRanked);

        List<EligibleProductOption> options = new ArrayList<>();
        for (Long propertyId : propertyIds) {
            EligibleProductOption option = optionByPropertyId.get(propertyId);
            if (option != null) {
                options.add(option);
            }
        }
        return options;
    }

    private void addPropertyIds(Set<Long> propertyIds, List<ProductMatchDto> ranked) {
        for (ProductMatchDto card : ranked) {
            if (card.productPropertyId() != null) {
                propertyIds.add(card.productPropertyId());
            }
        }
    }

    private void addRatePropertyIds(Set<Long> propertyIds, List<ProductRateDto> ranked) {
        for (ProductRateDto card : ranked) {
            if (card.productPropertyId() != null) {
                propertyIds.add(card.productPropertyId());
            }
        }
    }

    private void addMatchScores(Map<Long, Double> scores, List<ProductMatchDto> ranked) {
        for (ProductMatchDto card : ranked) {
            if (card.productPropertyId() != null) {
                scores.put(card.productPropertyId(), card.totalScore());
            }
        }
    }

    private void addRates(Map<Long, ProductRateDto> rates, List<ProductRateDto> ranked) {
        for (ProductRateDto card : ranked) {
            if (card.productPropertyId() != null) {
                rates.put(card.productPropertyId(), card);
            }
        }
    }

    private List<KeywordValueEnum> badgeKeywords(
            Product product,
            ProductProperty property,
            Double bankMaxInterestThreshold
    ) {
        List<KeywordValueEnum> badges = new ArrayList<>();
        List<KeywordValueEnum> keywords = productDisplayKeywordService.resolve(
                product,
                List.of(property),
                bankMaxInterestThreshold
        );
        for (KeywordValueEnum keyword : keywords) {
            if (!keyword.name().startsWith("REGION_")) {
                badges.add(keyword);
            }
        }
        return List.copyOf(badges);
    }

    private List<Integer> saveTrms(List<ProductProperty> properties) {
        Set<Integer> terms = new TreeSet<>();
        for (ProductProperty property : properties) {
            if (property.getSaveTrm() != null) {
                terms.add(property.getSaveTrm());
            }
        }
        return List.copyOf(terms);
    }
}
