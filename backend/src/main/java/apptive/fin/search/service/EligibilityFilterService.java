package apptive.fin.search.service;

import apptive.fin.search.ExtractionConfidence;
import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.RequiredKeywordEffect;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.EligibleProductOption;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductRequiredKeyword;
import apptive.fin.search.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityFilterService {

    private final ProductRepository productRepository;
    private final ResolveKeywordService resolveKeywordService;

    @Transactional(readOnly = true)
    public List<Product> filterEligible(SearchRequestDto request){
        var detail = request.detailedOptions();
        // TODO : QueryDSL 도입
        if (detail == null) return List.of();

        ResolvedKeywords keywords = resolveKeywordService.resolveKeywords(request.options());
        EligibilityCriteria criteria = eligibilityCriteria(detail, keywords);

        return productRepository.findEligibleProducts(
                criteria.age(),
                criteria.annualIncome(),
                criteria.householdIncomePercent(),
                criteria.incomeProofUnavailable(),
                criteria.militaryAgeExtensionRequested(),
                criteria.isHomeless(),
                criteria.isHouseholder(),
                criteria.tenureMonths(),
                criteria.monthlyDeposit()
        ).stream()
                .filter(product -> hasEligibleIdentityProperty(product, keywords.identities()))
                .toList();

    }

    @Transactional(readOnly = true)
    public List<EligibleProductOption> filterEligibleOptions(SearchRequestDto request) {
        var detail = request.detailedOptions();
        if (detail == null) return List.of();

        ResolvedKeywords keywords = resolveKeywordService.resolveKeywords(request.options());
        EligibilityCriteria criteria = eligibilityCriteria(detail, keywords);

        return productRepository.findEligibleProducts(
                        criteria.age(),
                        criteria.annualIncome(),
                        criteria.householdIncomePercent(),
                        criteria.incomeProofUnavailable(),
                        criteria.militaryAgeExtensionRequested(),
                        criteria.isHomeless(),
                        criteria.isHouseholder(),
                        criteria.tenureMonths(),
                        criteria.monthlyDeposit()
                ).stream()
                .flatMap(product -> product.getProperties().stream()
                        .filter(property -> isPropertyEligible(property, criteria, keywords.identities()))
                        .map(property -> new EligibleProductOption(product, property)))
                .toList();
    }

    private EligibilityCriteria eligibilityCriteria(
            DetailedOptionsDto detail,
            ResolvedKeywords keywords
    ) {
        Integer age = detail.birthdate() != null
                ? Period.between(detail.birthdate(), LocalDate.now()).getYears()
                : null;
        Long annualIncome = detail.annualIncome();
        Integer tenureMonths = new HashSet<>(keywords.identities())
                .contains(KeywordValueEnum.STATUS_UNEMPLOYED)
                ? Integer.valueOf(0)
                : detail.tenureMonths();

        return new EligibilityCriteria(
                age,
                annualIncome,
                detail.householdIncomePercent(),
                annualIncome != null && annualIncome == 0L,
                keywords.identities().contains(KeywordValueEnum.STATUS_MILITARY),
                detail.isHomeless(),
                detail.isHouseholder(),
                tenureMonths,
                detail.monthlySavingsGoal()
        );
    }

    private boolean isPropertyEligible(
            ProductProperty property,
            EligibilityCriteria criteria,
            List<KeywordValueEnum> identities
    ) {
        return Boolean.TRUE.equals(property.getIsJoinable())
                && isAgeEligible(property, criteria.age(), criteria.militaryAgeExtensionRequested())
                && isIncomeEligible(property, criteria.annualIncome(), criteria.householdIncomePercent(), criteria.incomeProofUnavailable())
                && isResidenceEligible(property, criteria.isHomeless(), criteria.isHouseholder())
                && isTenureEligible(property, criteria.tenureMonths())
                && isMonthlyDepositEligible(property, criteria.monthlyDeposit())
                && isIdentityEligible(property, identities);
    }

    private boolean isAgeEligible(ProductProperty property, Integer age, Boolean militaryAgeExtensionRequested) {
        if (age == null) {
            return true;
        }
        if (property.getMinAge() != null && property.getMinAge() > age) {
            return false;
        }
        if (property.getMaxAge() == null || property.getMaxAge() >= age) {
            return true;
        }

        int militaryMaxAge = property.getMilitaryMaxAge() != null ? property.getMilitaryMaxAge() : 39;
        return Boolean.TRUE.equals(militaryAgeExtensionRequested)
                && age >= 35
                && age <= 39
                && Boolean.TRUE.equals(property.getAllowsMilitaryAgeExtension())
                && militaryMaxAge >= age;
    }

    private boolean isIncomeEligible(
            ProductProperty property,
            Long annualIncome,
            Integer householdIncomePercent,
            Boolean incomeProofUnavailable
    ) {
        if (Boolean.TRUE.equals(incomeProofUnavailable)
                && (property.getEarnMaxAmt() != null || property.getEarnPercent() != null)) {
            return false;
        }
        if (!Boolean.TRUE.equals(incomeProofUnavailable)
                && annualIncome != null
                && property.getEarnMaxAmt() != null
                && property.getEarnMaxAmt() < annualIncome) {
            return false;
        }

        return householdIncomePercent == null
                || property.getEarnPercent() == null
                || property.getEarnPercent() >= householdIncomePercent;
    }

    private boolean isResidenceEligible(ProductProperty property, Boolean isHomeless, Boolean isHouseholder) {
        boolean homelessEligible = isHomeless == null
                || !Boolean.TRUE.equals(property.getRequiresHomeless())
                || Boolean.TRUE.equals(isHomeless);
        boolean householderEligible = isHouseholder == null
                || !Boolean.TRUE.equals(property.getRequiresHouseholder())
                || Boolean.TRUE.equals(isHouseholder);

        return homelessEligible && householderEligible;
    }

    private boolean isTenureEligible(ProductProperty property, Integer tenureMonths) {
        return tenureMonths == null
                || property.getMinTenureMonths() == null
                || property.getMinTenureMonths() <= tenureMonths;
    }

    private boolean isMonthlyDepositEligible(ProductProperty property, Long monthlyDeposit) {
        return monthlyDeposit == null
                || property.getMinMonthlyLimit() == null
                || property.getMinMonthlyLimit() <= monthlyDeposit;
    }

    private boolean hasEligibleIdentityProperty(Product product, List<KeywordValueEnum> identities) {
        return product.getProperties().stream()
                .anyMatch(property -> isIdentityEligible(property, identities));
    }

    private boolean isIdentityEligible(ProductProperty property, List<KeywordValueEnum> identities) {
        List<ProductRequiredKeyword> identityConditions = property.getRequiredKeywords().stream()
                .filter(this::isHighConfidenceIdentityCondition)
                .toList();

        boolean excluded = identityConditions.stream()
                .filter(condition -> condition.getEffect() == RequiredKeywordEffect.EXCLUDE)
                .map(ProductRequiredKeyword::getKeywordCode)
                .anyMatch(identities::contains);

        if (excluded) {
            return false;
        }

        List<KeywordValueEnum> requiredIdentities = identityConditions.stream()
                .filter(condition -> condition.getEffect() == RequiredKeywordEffect.REQUIRE)
                .map(ProductRequiredKeyword::getKeywordCode)
                .toList();

        return requiredIdentities.isEmpty() || requiredIdentities.stream().anyMatch(identities::contains);
    }

    private boolean isHighConfidenceIdentityCondition(ProductRequiredKeyword condition) {
        KeywordValueEnum keyword = condition.getKeywordCode();
        return condition.getConfidence() == ExtractionConfidence.HIGH
                && keyword != null
                && keyword.name().startsWith("STATUS_");
    }

    private record EligibilityCriteria(
            Integer age,
            Long annualIncome,
            Integer householdIncomePercent,
            Boolean incomeProofUnavailable,
            Boolean militaryAgeExtensionRequested,
            Boolean isHomeless,
            Boolean isHouseholder,
            Integer tenureMonths,
            Long monthlyDeposit
    ) {
    }
}
