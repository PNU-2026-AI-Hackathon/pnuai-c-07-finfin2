package apptive.fin.search.service;

import apptive.fin.search.ContributionType;
import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.ProductType;
import apptive.fin.search.dto.BankDetailDto;
import apptive.fin.search.dto.GovernmentDetailDto;
import apptive.fin.search.dto.PreferentialConditionDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductPreferentialRate;
import apptive.fin.search.entity.ProductProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

@Service
public class RateCalculatorService {

    private static final String ONTONG_SOURCE = "ONTONG";

    public ProductRateDto calculate(Product product, SearchRequestDto request) {
        return calculate(product, request, emptyKeywords());
    }

    public ProductRateDto calculate(Product product, SearchRequestDto request, ResolvedKeywords keywords) {
        ResolvedKeywords resolvedKeywords = keywords != null ? keywords : emptyKeywords();
        if (product.getType() == ProductType.SUBSCRIPTION) {
            return subscriptionDto(product);
        }

        if (isGovernmentProduct(product)) {
            return calculateGovernmentProduct(product, request);
        }

        return calculateBankProduct(product, request, resolvedKeywords);
    }

    public ProductRateDto calculate(
            Product product,
            ProductProperty property,
            SearchRequestDto request,
            ResolvedKeywords keywords
    ) {
        ResolvedKeywords resolvedKeywords = keywords != null ? keywords : emptyKeywords();
        if (product.getType() == ProductType.SUBSCRIPTION) {
            return subscriptionDto(product);
        }

        if (isGovernmentProduct(product)) {
            return calculateGovernmentProperty(product, property, request);
        }

        return calculateBankProperty(product, property, request, resolvedKeywords);
    }

    // ===== 상품 상세용 (Y4-1) =====

    // 정부 상세: 선택 property의 환산수익률 + 예상 만기 기여금 총액. 리스트(calculate)와 동일 헬퍼를 재사용해 값 일치.
    public GovernmentDetailDto governmentDetail(ProductProperty property, SearchRequestDto request) {
        if (property == null) {
            return null;
        }

        Long monthlyGoal = monthlySavingsGoal(request);
        Long effectiveDeposit = (monthlyGoal != null && monthlyGoal > 0)
                ? effectiveMonthlyDeposit(monthlyGoal, property.getMaxMonthlyLimit())
                : null;

        return new GovernmentDetailDto(
                calculateGovernmentYield(property, request),
                expectedTotalContribution(property, monthlyGoal),
                property.getGovContributionType(),
                effectiveDeposit,
                contributionMonths(property)
        );
    }

    // 예상 만기 기여금 총액 = 외부(정부·지자체·기업) 매칭 총액. Y3-2 B-1 명세 기준.
    private Long expectedTotalContribution(ProductProperty property, Long monthlySavingsGoal) {
        ContributionType type = property.getGovContributionType();
        Integer months = contributionMonths(property);
        if (type == null || type == ContributionType.NONE || months == null) {
            return null;
        }

        return switch (type) {
            // 정액: 월정액매칭 × 개월 (본인 납입과 무관하게 고정)
            case FIXED_AMOUNT -> property.getGovMonthlyFixedContribution() == null
                    ? null
                    : property.getGovMonthlyFixedContribution() * months;
            // 정률: 매칭배수 × 본인 납입 총액(min(희망월납입, 한도) × 개월)
            case RATIO -> {
                if (monthlySavingsGoal == null || monthlySavingsGoal <= 0 || property.getGovMatchingRatio() == null) {
                    yield null;
                }
                long ownDeposit = effectiveMonthlyDeposit(monthlySavingsGoal, property.getMaxMonthlyLimit());
                yield Math.round(property.getGovMatchingRatio().doubleValue() * ownDeposit * months);
            }
            case NONE -> null;
        };
    }

    // 은행 상세: 선택 property의 기본/최고/실질금리 + 충족/미충족 우대조건. calculate와 동일 헬퍼 재사용.
    public BankDetailDto bankDetail(ProductProperty property, SearchRequestDto request, ResolvedKeywords keywords) {
        if (property == null) {
            return null;
        }

        ResolvedKeywords resolvedKeywords = keywords != null ? keywords : emptyKeywords();
        Set<KeywordValueEnum> applicable = applicableBankConditions(property, request, resolvedKeywords);

        List<PreferentialConditionDto> met = new ArrayList<>();
        List<PreferentialConditionDto> unmet = new ArrayList<>();
        for (ProductPreferentialRate rate : property.getPreferentialRates()) {
            boolean satisfied = applicable.contains(rate.getKeywordCode()) && isAgeConditionSatisfied(rate, request);
            (satisfied ? met : unmet).add(toConditionDto(rate));
        }

        return new BankDetailDto(
                property.getBaseRate() != null ? property.getBaseRate().doubleValue() : null,
                property.getMaxRate() != null ? property.getMaxRate().doubleValue() : null,
                achievableBankRate(property, request, resolvedKeywords),
                met,
                unmet
        );
    }

    private PreferentialConditionDto toConditionDto(ProductPreferentialRate rate) {
        return new PreferentialConditionDto(
                rate.getKeywordCode(),
                rate.getRate() != null ? rate.getRate().doubleValue() : null,
                rate.getDescription()
        );
    }

    // productPropertyId 미전달 시 대표 property 선정(정부=max 수익률, 은행=max 실질금리). 리스트가 고르는 것과 동일 로직.
    public ProductProperty selectRepresentativeProperty(Product product, SearchRequestDto request, ResolvedKeywords keywords) {
        ResolvedKeywords resolvedKeywords = keywords != null ? keywords : emptyKeywords();

        if (isGovernmentProduct(product)) {
            return product.getProperties().stream()
                    .filter(property -> calculateGovernmentYield(property, request) != null)
                    .max(Comparator.comparingDouble(property -> calculateGovernmentYield(property, request)))
                    .orElseGet(() -> product.getProperties().stream().findFirst().orElse(null));
        }

        return product.getProperties().stream()
                .max(Comparator.comparingDouble(property -> achievableBankRate(property, request, resolvedKeywords)))
                .orElse(null);
    }

    private ProductRateDto subscriptionDto(Product product) {
        return ProductRateDto.builder()
                .productId(product.getId())
                .productPropertyId(null)
                .productName(product.getProductName())
                .providerName(null)
                .source(product.getSource().getCode())
                .rateComparable(false)
                .isSubscription(true)
                .subscriptionNote("청약: 금리 비교 대상 아님")
                .build();
    }

    private ProductRateDto calculateGovernmentProduct(Product product, SearchRequestDto request) {
        Optional<GovYieldScore> bestScore = product.getProperties().stream()
                .map(property -> new GovYieldScore(property, calculateGovernmentYield(property, request)))
                .filter(score -> score.yield() != null)
                .max(Comparator.comparingDouble(GovYieldScore::yield));

        if (bestScore.isEmpty()) {
            ProductProperty firstProperty = product.getProperties().stream().findFirst().orElse(null);
            return baseDto(product, firstProperty)
                    .rateComparable(false)
                    .isSubscription(false)
                    .build();
        }

        GovYieldScore score = bestScore.get();
        return baseDto(product, score.property())
                .baseRate(0.0)
                .achievableRate(score.yield())
                .rateComparable(true)
                .isSubscription(false)
                .build();
    }

    private ProductRateDto calculateBankProduct(Product product, SearchRequestDto request, ResolvedKeywords keywords) {
        ProductProperty bestProperty = product.getProperties().stream()
                .max(Comparator.comparingDouble(property -> achievableBankRate(property, request, keywords)))
                .orElse(null);

        return calculateBankProperty(product, bestProperty, request, keywords);
    }

    private ProductRateDto calculateGovernmentProperty(
            Product product,
            ProductProperty property,
            SearchRequestDto request
    ) {
        Double yield = calculateGovernmentYield(property, request);
        if (yield == null) {
            return baseDto(product, property)
                    .rateComparable(false)
                    .isSubscription(false)
                    .build();
        }

        return baseDto(product, property)
                .baseRate(0.0)
                .achievableRate(yield)
                .rateComparable(true)
                .isSubscription(false)
                .build();
    }

    private ProductRateDto calculateBankProperty(
            Product product,
            ProductProperty property,
            SearchRequestDto request,
            ResolvedKeywords keywords
    ) {
        return baseDto(product, property)
                .baseRate(baseRate(property))
                .achievableRate(achievableBankRate(property, request, keywords))
                .rateComparable(true)
                .isSubscription(false)
                .build();
    }

    private ProductRateDto.ProductRateDtoBuilder baseDto(Product product, ProductProperty property) {
        return ProductRateDto.builder()
                .productId(product.getId())
                .productPropertyId(property != null ? property.getId() : null)
                .productName(product.getProductName())
                .providerName(providerName(property))
                .source(product.getSource().getCode());
    }

    private Double calculateGovernmentYield(ProductProperty property, SearchRequestDto request) {
        if (property == null || Boolean.TRUE.equals(property.getExcludeFromRateComparison())) {
            return null;
        }

        ContributionType contributionType = property.getGovContributionType();
        if (contributionType == null || contributionType == ContributionType.NONE) {
            return null;
        }

        Double years = contributionYears(property);
        if (years == null || years <= 0) {
            return null;
        }

        return switch (contributionType) {
            case RATIO -> ratioYield(property.getGovMatchingRatio(), years);
            case FIXED_AMOUNT -> fixedAmountYield(property, monthlySavingsGoal(request), years);
            case NONE -> null;
        };
    }

    private Double ratioYield(BigDecimal matchingRatio, double years) {
        if (matchingRatio == null) {
            return null;
        }

        return matchingRatio.doubleValue() / years * 100;
    }

    private Double fixedAmountYield(ProductProperty property, Long monthlySavingsGoal, double years) {
        Long monthlyFixedContribution = property.getGovMonthlyFixedContribution();
        if (monthlyFixedContribution == null || monthlySavingsGoal == null || monthlySavingsGoal <= 0) {
            return null;
        }

        Long effectiveMonthlyDeposit = effectiveMonthlyDeposit(monthlySavingsGoal, property.getMaxMonthlyLimit());
        return ((double) monthlyFixedContribution / effectiveMonthlyDeposit) / years * 100;
    }

    private Long effectiveMonthlyDeposit(Long monthlySavingsGoal, Long maxMonthlyLimit) {
        if (maxMonthlyLimit == null || maxMonthlyLimit <= 0) {
            return monthlySavingsGoal;
        }

        return Math.min(monthlySavingsGoal, maxMonthlyLimit);
    }

    private Integer contributionMonths(ProductProperty property) {
        Integer periodMonths = property.getGovContributionPeriodMonths() != null
                ? property.getGovContributionPeriodMonths()
                : property.getSaveTrm();

        if (periodMonths == null || periodMonths <= 0) {
            return null;
        }

        return periodMonths;
    }

    private Double contributionYears(ProductProperty property) {
        Integer months = contributionMonths(property);
        return months == null ? null : months / 12.0;
    }

    private Long monthlySavingsGoal(SearchRequestDto request) {
        return request.detailedOptions() != null
                ? request.detailedOptions().monthlySavingsGoal()
                : null;
    }

    private boolean isGovernmentProduct(Product product) {
        return product.getSource().getCode().equals(ONTONG_SOURCE);
    }

    private double baseRate(ProductProperty property) {
        return property != null && property.getBaseRate() != null
                ? property.getBaseRate().doubleValue()
                : 0.0;
    }

    private double achievableBankRate(ProductProperty property, SearchRequestDto request, ResolvedKeywords keywords) {
        if (property == null) {
            return 0.0;
        }

        double calculatedRate = baseRate(property) + preferentialRateSum(property, request, keywords);
        if (property.getMaxRate() == null) {
            return calculatedRate;
        }

        return Math.min(calculatedRate, property.getMaxRate().doubleValue());
    }

    private double preferentialRateSum(ProductProperty property, SearchRequestDto request, ResolvedKeywords keywords) {
        Set<KeywordValueEnum> applicableConditions = applicableBankConditions(property, request, keywords);
        return property.getPreferentialRates().stream()
                .filter(rate -> applicableConditions.contains(rate.getKeywordCode()))
                .filter(rate -> isAgeConditionSatisfied(rate, request))
                .map(ProductPreferentialRate::getRate)
                .filter(rate -> rate != null)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    private Set<KeywordValueEnum> applicableBankConditions(
            ProductProperty property,
            SearchRequestDto request,
            ResolvedKeywords keywords
    ) {
        Set<KeywordValueEnum> conditions = new HashSet<>(keywords.bankConditions());
        conditions.add(KeywordValueEnum.BANK_ONLINE_JOIN);

        if (isProviderSelected(property, neverUsedBanks(request))) {
            conditions.add(KeywordValueEnum.BANK_FIRST_TRANSACTION);
        }

        if (isProviderSelected(property, maturedSavingBanks(request))) {
            conditions.add(KeywordValueEnum.BANK_REDEPOSIT);
        }

        if (age(request) != null) {
            conditions.add(KeywordValueEnum.BANK_AGE);
        }

        return conditions;
    }

    private boolean isAgeConditionSatisfied(ProductPreferentialRate rate, SearchRequestDto request) {
        if (rate.getKeywordCode() != KeywordValueEnum.BANK_AGE) {
            return true;
        }

        Integer age = age(request);
        if (age == null) {
            return false;
        }

        boolean minSatisfied = rate.getMinAge() == null || rate.getMinAge() <= age;
        boolean maxSatisfied = rate.getMaxAge() == null || rate.getMaxAge() >= age;
        return minSatisfied && maxSatisfied;
    }

    private boolean isProviderSelected(ProductProperty property, List<String> selectedProviders) {
        if (property == null || property.getProvider() == null || selectedProviders == null) {
            return false;
        }

        String providerCode = property.getProvider().getCode();
        String providerName = property.getProvider().getName();
        return selectedProviders.stream()
                .anyMatch(selected -> selected != null && (selected.equals(providerCode) || selected.equals(providerName)));
    }

    private List<String> neverUsedBanks(SearchRequestDto request) {
        return request.detailedOptions() != null
                ? request.detailedOptions().neverUsedBanks()
                : null;
    }

    private List<String> maturedSavingBanks(SearchRequestDto request) {
        return request.detailedOptions() != null
                ? request.detailedOptions().maturedSavingBanks()
                : null;
    }

    private Integer age(SearchRequestDto request) {
        if (request.detailedOptions() == null || request.detailedOptions().birthdate() == null) {
            return null;
        }

        return Period.between(request.detailedOptions().birthdate(), LocalDate.now()).getYears();
    }

    private String providerName(ProductProperty property) {
        return property != null && property.getProvider() != null
                ? property.getProvider().getName()
                : null;
    }

    private record GovYieldScore(ProductProperty property, Double yield) {
    }

    private ResolvedKeywords emptyKeywords() {
        return new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of());
    }
}
