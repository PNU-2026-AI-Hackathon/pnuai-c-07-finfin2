package apptive.fin.search.service;

import apptive.fin.search.enums.ContributionType;
import apptive.fin.search.enums.InterestRateType;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductType;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Comparator;
import java.util.Set;

@Service
public class RateCalculatorService {

    // 이자소득세율 (소득세 14% + 지방소득세 1.4% = 15.4%)
    private static final double TAX_RATE = 0.154;

    // 정부 수익률 혹은 적용금리를 계산
    public ProductRateDto calculate(
            Product product,
            ProductProperty property,
            SearchRequestDto request,
            ResolvedKeywords keywords
    ) {

        ResolvedKeywords resolvedKeywords = keywords != null ? keywords : ResolvedKeywords.emptyKeywords();
        // 청약상품
        if (product.getType() == ProductType.SUBSCRIPTION) {
            return subscriptionDto(product);
        }

        // 정부상품
        if (product.isGovernment()) {
            return calculateGovernmentProperty(product, property, request);
        }
        // 은행상품
        return calculateBankProperty(product, property, request, resolvedKeywords);
    }

    // ===== 상품 상세용 (Y4-1) =====

    // 정부 상세: 선택 property의 환산수익률 + 예상 만기 기여금 총액. 리스트(calculate)와 동일 헬퍼를 재사용해 값 일치.
    public GovernmentDetailDto governmentDetail(ProductProperty property, SearchRequestDto request) {
        if (property == null) {
            return null;
        }

        // 월저축목표
        Long monthlyGoal = request.monthlySavingsGoal();
        // 유효저축액 계산
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
                // 월 유효납입액
                long ownDeposit = effectiveMonthlyDeposit(monthlySavingsGoal, property.getMaxMonthlyLimit());
                // 매칭율 * 납입액 * 개월
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

        ResolvedKeywords resolvedKeywords = keywords != null ? keywords : ResolvedKeywords.emptyKeywords();
        Set<KeywordValueEnum> applicable = applicableBankConditions(property, request, resolvedKeywords);
        
        // met, unmet 리스트를 만들고, 각 우대금리 조건에 대해 사용자의 조건을 검사하여 분류함
        List<PreferentialConditionDto> met = new ArrayList<>();
        List<PreferentialConditionDto> unmet = new ArrayList<>();
        for (ProductPreferentialRate rate : property.getPreferentialRates()) {
            boolean satisfied = applicable.contains(rate.getKeywordCode()) && passesAgeRangeFilter(rate);
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

    // (ProductPreferentialRate) -> PreferentialConditionDto
    private PreferentialConditionDto toConditionDto(ProductPreferentialRate rate) {
        return new PreferentialConditionDto(
                rate.getKeywordCode(),
                rate.getRate() != null ? rate.getRate().doubleValue() : null,
                rate.getDescription()
        );
    }

    // productPropertyId 미전달 시 대표 property 선정(정부=max 수익률, 은행=max 실질금리). 리스트가 고르는 것과 동일 로직.
    public ProductProperty selectRepresentativeProperty(Product product, SearchRequestDto request, ResolvedKeywords keywords) {
        return selectRepresentativeProperty(product, product.getProperties(), request, keywords);
    }

    public ProductProperty selectRepresentativeProperty(
            Product product,
            List<ProductProperty> properties,
            SearchRequestDto request,
            ResolvedKeywords keywords
    ) {
        ResolvedKeywords resolvedKeywords = keywords != null ? keywords : ResolvedKeywords.emptyKeywords();

        if (product.isGovernment()) {
            return properties.stream()
                    .filter(property -> calculateGovernmentYield(property, request) != null)
                    .max(Comparator.comparingDouble(property -> calculateGovernmentYield(property, request)))
                    .orElseGet(() -> properties.stream().findFirst().orElse(null));
        }

        return properties.stream()
                .max(Comparator.comparingDouble(property -> achievableBankRate(property, request, resolvedKeywords)))
                .orElse(null);
    }

    // 청약상품을 ProductRateDto로 변환
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

    // 정부상품 수익률 계산해 반환
    private ProductRateDto calculateGovernmentProperty(
            Product product,
            ProductProperty property,
            SearchRequestDto request
    ) {
        Double yield = calculateGovernmentYield(property, request);
        Long netReturn = calculateGovernmentNetReturn(product, property, request);
        Long principal = calculateGovernmentPrincipal(property, request);

        if (yield == null) {
            return baseDto(product, property)
                    .rateComparable(false)
                    .isSubscription(false)
                    .netReturn(netReturn)
                    .principal(principal)
                    .saveTrm(contributionMonths(property))
                    .productType(product.getType() != null ? product.getType().name() : null)
                    .build();
        }

        return baseDto(product, property)
                .baseRate(0.0)
                .achievableRate(yield)
                .rateComparable(true)
                .isSubscription(false)
                .netReturn(netReturn)
                .principal(principal)
                .saveTrm(contributionMonths(property))
                .productType(product.getType() != null ? product.getType().name() : null)
                .build();
    }

    // 정부상품 원금 계산
    private Long calculateGovernmentPrincipal(ProductProperty property, SearchRequestDto request) {
        if (property == null) {
            return null;
        }

        Long monthlyGoal = request.monthlySavingsGoal();
        if (monthlyGoal == null || monthlyGoal <= 0) {
            return null;
        }

        Integer months = contributionMonths(property);
        if (months == null || months <= 0) {
            return null;
        }

        Long effectiveMonthly = effectiveMonthlyDeposit(monthlyGoal, property.getMaxMonthlyLimit());
        return effectiveMonthly * months;
    }

    // 은행상품 금리 계산해 반환
    private ProductRateDto calculateBankProperty(
            Product product,
            ProductProperty property,
            SearchRequestDto request,
            ResolvedKeywords keywords
    ) {
        Long netReturn = calculateBankNetReturn(product, property, request, keywords);
        Long principal = calculatePrincipal(product, property, request);

        return baseDto(product, property)
                .baseRate(baseRate(property))
                .achievableRate(achievableBankRate(property, request, keywords))
                .rateComparable(true)
                .isSubscription(false)
                .netReturn(netReturn)
                .principal(principal)
                .saveTrm(property != null ? property.getSaveTrm() : null)
                .productType(product.getType() != null ? product.getType().name() : null)
                .build();
    }

    // 원금 계산
    private Long calculatePrincipal(Product product, ProductProperty property, SearchRequestDto request) {
        if (property == null) {
            return null;
        }

        Integer months = property.getSaveTrm();
        if (months == null || months <= 0) {
            return null;
        }

        ProductType type = product.getType();
        if (type == ProductType.DEPOSIT) {
            return request.depositAmount();
        } else if (type == ProductType.SAVING) {
            Long monthlyGoal = request.monthlySavingsGoal();
            if (monthlyGoal == null || monthlyGoal <= 0) {
                return null;
            }
            Long effectiveMonthly = effectiveMonthlyDeposit(monthlyGoal, property.getMaxMonthlyLimit());
            return effectiveMonthly * months;
        }
        return null;
    }

    // ProductRateDto의 기본형태를 반환하는 헬퍼함수
    private ProductRateDto.ProductRateDtoBuilder baseDto(Product product, ProductProperty property) {
        return ProductRateDto.builder()
                .productId(product.getId())
                .productPropertyId(property != null ? property.getId() : null)
                .productName(product.getProductName())
                .providerName(property != null ? property.providerName() : null)
                .source(product.getSource().getCode());
    }

    // 정부상품 수익률 계산
    private Double calculateGovernmentYield(ProductProperty property, SearchRequestDto request) {
        // 속성 자체가 null이거나 이율 계산에서 제외되면 null 반환
        if (property == null || Boolean.TRUE.equals(property.getExcludeFromRateComparison())) {
            return null;
        }

        // 기여타입이 없으면 null 반환
        ContributionType contributionType = property.getGovContributionType();
        if (contributionType == null || contributionType == ContributionType.NONE) {
            return null;
        }

        // 기여연수가 null이거나 0이하이면 null 반환
        Double years = contributionYears(property);
        if (years == null || years <= 0) {
            return null;
        }

        // 기여 타입에 따른 수익률 계산
        return switch (contributionType) {
            case RATIO -> ratioYield(property.getGovMatchingRatio(), years);
            case FIXED_AMOUNT -> fixedAmountYield(property, request.monthlySavingsGoal(), years);
            case NONE -> null;
        };
    }

    // 정률매칭 수익률계산
    private Double ratioYield(BigDecimal matchingRatio, double years) {
        if (matchingRatio == null) {
            return null;
        }

        // (매칭율 / 가입기간) * 100
        return matchingRatio.doubleValue() / years * 100;
    }

    // 정액매칭 수익률계산
    private Double fixedAmountYield(ProductProperty property, Long monthlySavingsGoal, double years) {
        Long monthlyFixedContribution = property.getGovMonthlyFixedContribution();
        if (monthlyFixedContribution == null || monthlySavingsGoal == null || monthlySavingsGoal <= 0) {
            return null;
        }

        Long effectiveMonthlyDeposit = effectiveMonthlyDeposit(monthlySavingsGoal, property.getMaxMonthlyLimit());
        // (월 기여금액 / 월납입액) / 가입기간 * 100
        return ((double) monthlyFixedContribution / effectiveMonthlyDeposit) / years * 100;
    }

    // 유효한 월납입액을 반환
    private Long effectiveMonthlyDeposit(Long monthlySavingsGoal, Long maxMonthlyLimit) {
        if (maxMonthlyLimit == null || maxMonthlyLimit <= 0) {
            return monthlySavingsGoal;
        }

        return Math.min(monthlySavingsGoal, maxMonthlyLimit);
    }

    // 기여기간(월)을 반환
    private Integer contributionMonths(ProductProperty property) {
        // property.getGovContributionPeriodMonths() != null 이면 기여기간(월)을 사용하고, 아니면 saveTrm을 사용
        Integer periodMonths = property.getGovContributionPeriodMonths() != null
                ? property.getGovContributionPeriodMonths()
                : property.getSaveTrm();

        if (periodMonths == null || periodMonths <= 0) {
            return null;
        }

        return periodMonths;
    }

    // 기여년수 반환
    private Double contributionYears(ProductProperty property) {
        Integer months = contributionMonths(property);
        return months == null ? null : months / 12.0;
    }

    // 기본금리 반환
    private double baseRate(ProductProperty property) {
        return property != null && property.getBaseRate() != null
                ? property.getBaseRate().doubleValue()
                : 0.0;
    }

    // 달성 가능한 최대 금리 반환
    private double achievableBankRate(ProductProperty property, SearchRequestDto request, ResolvedKeywords keywords) {
        if (property == null) {
            return 0.0;
        }

        return baseRate(property) + preferentialRateSum(property, request, keywords);
    }

    // 우대금리의 총합
    private double preferentialRateSum(ProductProperty property, SearchRequestDto request, ResolvedKeywords keywords) {
        Set<KeywordValueEnum> applicableConditions = applicableBankConditions(property, request, keywords);
        return property.getPreferentialRates().stream()
                .filter(rate -> applicableConditions.contains(rate.getKeywordCode()))
                .filter(this::passesAgeRangeFilter)
                .map(ProductPreferentialRate::getRate)
                .filter(rate -> rate != null)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    // 적용 가능한 은행 우대금리 조건들의 집합을 반환
    private Set<KeywordValueEnum> applicableBankConditions(
            ProductProperty property,
            SearchRequestDto request,
            ResolvedKeywords keywords
    ) {
        Set<KeywordValueEnum> conditions = new HashSet<>();
        keywords.bankConditions().stream()
                .filter(keyword -> !keyword.isTransactionHistoryCondition())
                .filter(keyword -> keyword != KeywordValueEnum.BANK_ETC)
                .forEach(conditions::add);
        conditions.add(KeywordValueEnum.BANK_ONLINE_JOIN);
        conditions.add(KeywordValueEnum.BANK_AGE);

        if (request == null || !request.hasTransactionHistory()) {
            return conditions;
        }

        if (property.matchesAnyProvider(request.neverUsedBanks())) {
            conditions.add(KeywordValueEnum.BANK_FIRST_TRANSACTION);
        }

        if (property.matchesAnyProvider(request.maturedSavingBanks())) {
            conditions.add(KeywordValueEnum.BANK_REDEPOSIT);
        }

        return conditions;
    }

    // BANK_AGE 우대금리는 상품의 우대구간이 청년 구간과 겹칠 때만 인정한다(사용자 나이와 무관, MatchScoreService와 동일 규칙).
    // 그 외 키워드는 applicableBankConditions에서 이미 판정되므로 여기서는 통과시킨다.
    private boolean passesAgeRangeFilter(ProductPreferentialRate rate) {
        if (rate.getKeywordCode() != KeywordValueEnum.BANK_AGE) {
            return true;
        }

        return BankConditionMatcher.matchesYouthRange(rate);
    }

    // ===== 세후 실수령액 계산 (PRD 개정) =====

    /**
     * 세후 실수령액 계산.
     * @param productType 상품유형 (DEPOSIT=예금, SAVING=적금)
     * @param principal 원금 (예금: 예치액, 적금: 월납입액 × 개월)
     * @param monthlyDeposit 월납입액 (적금용)
     * @param rate 연이율 (%)
     * @param months 저축기간 (개월)
     * @param isCompound 복리 여부
     * @return 세후 실수령액 (원)
     */
    public Long calculateNetReturn(
            ProductType productType,
            Long principal,
            Long monthlyDeposit,
            double rate,
            int months,
            boolean isCompound
    ) {
        if (principal == null || principal <= 0 || rate <= 0 || months <= 0) {
            return null;
        }

        double interest = switch (productType) {
            case DEPOSIT -> calculateDepositInterest(principal, rate, months, isCompound);
            case SAVING -> calculateSavingInterest(monthlyDeposit, rate, months, isCompound);
            default -> 0.0;
        };

        double tax = interest * TAX_RATE;
        double netReturn = principal + interest - tax;
        return Math.round(netReturn);
    }

    /**
     * 예금 이자 계산 (일시 예치).
     * - 단리: 원금 × 연이율 × (개월/12)
     * - 복리: 원금 × ((1 + 연이율/12)^개월 - 1)
     */
    private double calculateDepositInterest(long principal, double rate, int months, boolean isCompound) {
        double annualRate = rate / 100.0;

        if (isCompound) {
            // 월복리
            double monthlyRate = annualRate / 12.0;
            return principal * (Math.pow(1 + monthlyRate, months) - 1);
        } else {
            // 단리
            return principal * annualRate * months / 12.0;
        }
    }

    /**
     * 적금 이자 계산 (정액적립).
     * - 단리: 월납입 × 연이율 × n(n+1)/24 (n=개월수)
     * - 복리: Σ(월납입 × (1 + 월이율)^(n-i+1) - 월납입) for i=1..n
     */
    private double calculateSavingInterest(Long monthlyDeposit, double rate, int months, boolean isCompound) {
        if (monthlyDeposit == null || monthlyDeposit <= 0) {
            return 0.0;
        }

        double annualRate = rate / 100.0;

        if (isCompound) {
            // 월복리 적금
            double monthlyRate = annualRate / 12.0;
            double totalAmount = 0.0;
            for (int i = 1; i <= months; i++) {
                // i번째 월 납입금이 (months - i + 1)개월 동안 복리
                totalAmount += monthlyDeposit * Math.pow(1 + monthlyRate, months - i + 1);
            }
            long totalPrincipal = monthlyDeposit * months;
            return totalAmount - totalPrincipal;
        } else {
            // 단리 적금: 월납입 × 연이율 × n(n+1)/24
            return monthlyDeposit * annualRate * months * (months + 1) / 24.0;
        }
    }

    /**
     * 은행상품 세후 실수령액 계산.
     */
    public Long calculateBankNetReturn(
            Product product,
            ProductProperty property,
            SearchRequestDto request,
            ResolvedKeywords keywords
    ) {
        if (property == null || property.getSaveTrm() == null) {
            return null;
        }

        double achievable = achievableBankRate(property, request, keywords);
        int months = property.getSaveTrm();
        boolean isCompound = property.getIntrRateType() == InterestRateType.COMPOUND_INTEREST;

        ProductType type = product.getType();
        if (type == ProductType.DEPOSIT) {
            // 예금: depositAmount 사용
            Long depositAmount = request.depositAmount();
            if (depositAmount == null || depositAmount <= 0) {
                return null;
            }
            return calculateNetReturn(type, depositAmount, null, achievable, months, isCompound);
        } else if (type == ProductType.SAVING) {
            // 적금: monthlySavingsGoal 사용
            Long monthlyGoal = request.monthlySavingsGoal();
            if (monthlyGoal == null || monthlyGoal <= 0) {
                return null;
            }
            Long effectiveMonthly = effectiveMonthlyDeposit(monthlyGoal, property.getMaxMonthlyLimit());
            Long principal = effectiveMonthly * months;
            return calculateNetReturn(type, principal, effectiveMonthly, achievable, months, isCompound);
        }

        return null;
    }

    /**
     * 정부상품 세후 실수령액 계산.
     * 정부 기여금은 비과세이므로 본인 납입금 이자에만 세금 적용.
     */
    public Long calculateGovernmentNetReturn(
            Product product,
            ProductProperty property,
            SearchRequestDto request
    ) {
        if (property == null) {
            return null;
        }

        Long monthlyGoal = request.monthlySavingsGoal();
        if (monthlyGoal == null || monthlyGoal <= 0) {
            return null;
        }

        Integer months = contributionMonths(property);
        if (months == null || months <= 0) {
            return null;
        }

        Long effectiveMonthly = effectiveMonthlyDeposit(monthlyGoal, property.getMaxMonthlyLimit());
        Long principal = effectiveMonthly * months;
        Long contribution = expectedTotalContribution(property, monthlyGoal);

        // 정부상품: 본인 원금 + 정부 기여금 (비과세)
        // 실수령액 = 원금 + 기여금 (은행 이자 계산은 별도)
        if (contribution != null) {
            return principal + contribution;
        }
        return principal;
    }
}
