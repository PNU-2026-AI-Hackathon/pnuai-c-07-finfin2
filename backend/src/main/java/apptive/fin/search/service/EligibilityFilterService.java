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

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityFilterService {

    private final ProductRepository productRepository;
    private final ResolveKeywordService resolveKeywordService;

    // 사용자가 가입가능한 상품만 필터링하는 메서드
    @Transactional(readOnly = true)
    public List<Product> filterEligible(SearchRequestDto request){
        var detail = request.detailedOptions();
        if (detail == null) return List.of(); // 상세옵션 선택안하면 빈 리스트 
				
        // 키워드 Resolve
        ResolvedKeywords keywords = resolveKeywordService.resolveKeywords(request.options());
        EligibilityCriteria criteria = eligibilityCriteria(detail, keywords); // 사용자의 가입 기준 정리
				
        // 가입가능한 상품 반환	
        return productRepository.findEligibleProducts(
                criteria.age(),
                criteria.annualIncome(),
                criteria.householdIncomePercent(),
                criteria.incomeProofUnavailable(),
                criteria.isHomeless(),
                criteria.isHouseholder(),
                criteria.tenureMonths(),
                criteria.monthlyDeposit()
        ).stream()
                .filter(product -> hasEligibleIdentityProperty(product, keywords.identities()))
                .toList();

    }

    // 사용자가 가입가능한 상품 옵션만 필터링하는 메서드
    @Transactional(readOnly = true)
    public List<EligibleProductOption> filterEligibleOptions(SearchRequestDto request) {
        var detail = request.detailedOptions();
        if (detail == null) return List.of(); // 상세옵션 선택안하면 빈 리스트

        // 키워드 Resolve
        ResolvedKeywords keywords = resolveKeywordService.resolveKeywords(request.options());
        EligibilityCriteria criteria = eligibilityCriteria(detail, keywords);  // 사용자의 가입 기준 정리
        
        // 가입가능한 상품 옵션(product, productProperty) 반환
        return productRepository.findEligibleProducts(
                        criteria.age(),
                        criteria.annualIncome(),
                        criteria.householdIncomePercent(),
                        criteria.incomeProofUnavailable(),
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

    // 사용자의 상품 가입기준을 정리하는 메서드
    private EligibilityCriteria eligibilityCriteria(
            DetailedOptionsDto detail,
            ResolvedKeywords keywords
    ) {
        Integer age = detail.age();
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
                detail.isHomeless(),
                detail.isHouseholder(),
                tenureMonths,
                detail.monthlySavingsGoal()
        );
    }
    // ProductProperty의 자격요건에 사용자의 조건이 부합하는지 검사하는 함수
    private boolean isPropertyEligible(
            ProductProperty property,
            EligibilityCriteria criteria,
            List<KeywordValueEnum> identities
    ) {
        return Boolean.TRUE.equals(property.getIsJoinable())
                && isAgeEligible(property, criteria.age())
                && isIncomeEligible(property, criteria.annualIncome(), criteria.householdIncomePercent(), criteria.incomeProofUnavailable())
                && isResidenceEligible(property, criteria.isHomeless(), criteria.isHouseholder())
                && isTenureEligible(property, criteria.tenureMonths())
                && isMonthlyDepositEligible(property, criteria.monthlyDeposit())
                && isIdentityEligible(property, identities);
    }

    // 나이 조건 확인
    private boolean isAgeEligible(ProductProperty property, Integer age) {
        if (age == null) {
            return true;
        }
        if (property.getMinAge() != null && property.getMinAge() > age) {
            return false;
        }
        return property.getMaxAge() == null || property.getMaxAge() >= age;
    }

    // 소득 조건 확인 
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

    // 거주 조건 확인
    private boolean isResidenceEligible(ProductProperty property, Boolean isHomeless, Boolean isHouseholder) {
        boolean homelessEligible = isHomeless == null
                || !Boolean.TRUE.equals(property.getRequiresHomeless())
                || Boolean.TRUE.equals(isHomeless);
        boolean householderEligible = isHouseholder == null
                || !Boolean.TRUE.equals(property.getRequiresHouseholder())
                || Boolean.TRUE.equals(isHouseholder);

        return homelessEligible && householderEligible;
    }

    // 근속년수 조건 확인
    private boolean isTenureEligible(ProductProperty property, Integer tenureMonths) {
        return tenureMonths == null
                || property.getMinTenureMonths() == null
                || property.getMinTenureMonths() <= tenureMonths;
    }

    // 월저축희망액 조건 확인
    private boolean isMonthlyDepositEligible(ProductProperty property, Long monthlyDeposit) {
        return monthlyDeposit == null
                || property.getMinMonthlyLimit() == null
                || property.getMinMonthlyLimit() <= monthlyDeposit;
    }

    // 상품에 대해 사용자의 현재 신분 기준 가입 가능 여부를 반환하는 함수
    private boolean hasEligibleIdentityProperty(Product product, List<KeywordValueEnum> identities) {
        return product.getProperties().stream()
                .anyMatch(property -> isIdentityEligible(property, identities));
    }

    // 현재 신분 기준으로 ProductProperty 가입 자격이 되는지 여부를 반환하는 함수
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

    // 신분 가입조건 Confidence가 HIGH인지 확인하는 함수
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
            Boolean isHomeless,
            Boolean isHouseholder,
            Integer tenureMonths,
            Long monthlyDeposit
    ) {
    }
}
