package apptive.fin.search.service;

import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ScoreWeightEnum;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static apptive.fin.search.enums.KeywordValueEnum.*;

@Service
public class MatchScoreService {

    public ProductMatchDto score(
            Product product,
            ProductProperty property,
            SearchRequestDto request,
            ResolvedKeywords keywords,
            boolean includeTransactionHistory
    ) {
        return score(product, property, request, keywords, includeTransactionHistory, null);
    }

    // bankMaxInterestThreshold: 은행 #최고이율 상위 30% 판정용 임계 금리(결과셋 기준, SearchService에서 사전 계산). null이면 미적용.
    public ProductMatchDto score(
            Product product,
            ProductProperty property,
            SearchRequestDto request,
            ResolvedKeywords keywords,
            boolean includeTransactionHistory,
            Double bankMaxInterestThreshold
    ) {
        boolean isGov = product.isGovernment();  // 정부상품 여부

        // ProductProperty에 대한 점수 계산
        ProductPropertyScore score = scoreProperty(
                property,
                keywords.coreBenefits(),
                keywords.identities(),
                keywords.bankConditions(),
                keywords.savingPeriod(),
                request.monthlySavingsGoal(),
                isGov,
                request,
                includeTransactionHistory,
                bankMaxInterestThreshold
        );

        return ProductMatchDto.builder()
                .productId(product.getId())
                .productPropertyId(property.getId())
                .productName(product.getDisplayProductName())
                .providerName(property.providerName())
                .source(product.getSource().getCode())
                .totalScore(score.totalScore())
                .benefitScore(score.benefitScore())
                .periodScore(score.periodScore())
                .identityScore(score.identityScore())
                .depositScore(score.depositScore())
                .bankCondScore(score.bankCondScore())
                .build();
    }

    private ProductPropertyScore scoreProperty(
            ProductProperty property,
            List<KeywordValueEnum> coreBenefits,
            List<KeywordValueEnum> identities,
            List<KeywordValueEnum> bankConditions,
            KeywordValueEnum savingPeriod,
            Long monthlyDeposit,
            boolean isGov,
            SearchRequestDto request,
            boolean includeTransactionHistory,
            Double bankMaxInterestThreshold
    ) {
        // 상품 속성의 키워드
        Set<KeywordValueEnum> propertyKeywords = property.keywordCodes();

        // 활성화된 은행 관련 조건
        List<KeywordValueEnum> activeBankConditions = activeBankConditions(
                bankConditions,
                property,
                request,
                includeTransactionHistory,
                isGov
        );

        // 가중치 분배
        Map<String, Double> weights = distributeWeights(
                coreBenefits,
                identities,
                activeBankConditions,
                savingPeriod,
                monthlyDeposit,
                property,
                isGov
        );
        
        // metric 별로 점수계산, 가중치 적용
        double benefitScore = calcBenefitScore(coreBenefits, property, propertyKeywords, isGov, bankMaxInterestThreshold)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_BENEFITS, ScoreWeightEnum.BANK_BENEFITS));
        double periodScore = calcPeriodScore(savingPeriod, property)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_PERIOD, ScoreWeightEnum.BANK_PERIOD));
        double identityScore = calcIdentityScore(identities, propertyKeywords, isGov)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_IDENTITY, ScoreWeightEnum.BANK_IDENTITY));
        double depositScore = calcDepositScore(monthlyDeposit, property)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_DEPOSIT, ScoreWeightEnum.BANK_DEPOSIT));
        double bankCondScore = (isGovBankConditionExcluded(isGov)
                ? 0.0
                : calcBankCondScore(activeBankConditions, propertyKeywords, property, request))
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_BANK_COND, ScoreWeightEnum.BANK_BANK_COND));
        double totalScore = benefitScore + periodScore + identityScore + depositScore + bankCondScore;

        // 점수를 ProductPropertyScore Dto 형태로 반환
        return new ProductPropertyScore(
                property,
                totalScore,
                benefitScore,
                periodScore,
                identityScore,
                depositScore,
                bankCondScore
        );
    }

    // 정부상품 여부에 따라 맞는 weightKey를 계산해 반환
    private String weightKey(boolean isGov, ScoreWeightEnum govWeight, ScoreWeightEnum bankWeight) {
        return isGov ? govWeight.getKey() : bankWeight.getKey();
    }

    // 혜택 점수 계산
    private double calcBenefitScore(
            List<KeywordValueEnum> selected,
            ProductProperty property,
            Set<KeywordValueEnum> propertyKeywords,
            boolean isGov,
            Double bankMaxInterestThreshold
    ) {
        List<KeywordValueEnum> applicable = applicableBenefitKeywords(selected, isGov);
        if (applicable.isEmpty()) return 0.0;

        long matched = applicable.stream()
                .filter(kw -> isBenefitMatched(kw, property, propertyKeywords, isGov, bankMaxInterestThreshold))
                .count();
        return (double) matched / applicable.size();
    }

    // 혜택 매칭 여부 계산
    private boolean isBenefitMatched(
            KeywordValueEnum keyword,
            ProductProperty property,
            Set<KeywordValueEnum> propertyKeywords,
            boolean isGov,
            Double bankMaxInterestThreshold
    ) {
        // 은행 #최고이율_중심: 정적 태그가 아니라 결과셋 최고금리(maxRate) 상위 30% 임계값 이상인지로 동적 판정 (PRD A-2).
        // 정부 상품/임계값 미제공 시에는 기존 태그 방식 유지 (정부는 금리 대부분 미공시).
        if (keyword == BENEFIT_MAX_INTEREST && !isGov && bankMaxInterestThreshold != null) {
            return BankMaxInterestPolicy.qualifies(property, bankMaxInterestThreshold);
        }
        return propertyKeywords.contains(keyword);
    }

    // 저축기간 점수 계산
    private double calcPeriodScore(KeywordValueEnum selected, ProductProperty property) {
        // 선택된 키워드가 없거나 상품 속성의 saveTrm이 null이면 0점
        if (selected == null || property.getSaveTrm() == null) return 0.0;

        
        int[] range = periodRange(selected); // 사용자가 선택한 키워드의 범위
        int saveTrm = property.getSaveTrm(); // 상품 속성의 저축기간 
        if (saveTrm >= range[0] && saveTrm <= range[1]) return 1.0;  // 범위 내이면 100%
        return isAdjacentOption(property, selected) ? 0.5 : 0.0;     // 범위 밖이고 인접기간이면 50%, 아니면 0%
    }

    // 신분 특화도 계산
    private double calcIdentityScore(List<KeywordValueEnum> selected, Set<KeywordValueEnum> propertyKeywords, boolean isGov) {
        if (selected.isEmpty()) return 0;

        // 정부상품이면
        if (isGov) {
            // 상품속성이 선택한 키워드를 포함하고 있고 그 키워드가 특화 키워드이면 특화상품
            boolean specialized = selected.stream().anyMatch(kw ->
                    propertyKeywords.contains(kw) && isSpecializedKeyword(kw));
            // 특화 키워드가 아니고 키워드만 포함하고 있으면 포함상품
            boolean included = selected.stream().anyMatch(propertyKeywords::contains);
            if (specialized) return 1.0;  // 특화이면 100%
            if (included) return 0.5;     // 포함이면 50%
            return 0; // 둘 다 아니면 0%
        }

        // 은행상품이면 키워드 포함이면 100%, 아니면 0%
        return selected.stream().anyMatch(propertyKeywords::contains) ? 1.0 : 0;
    }

    // 납입 한도 적합도 계산
    private double calcDepositScore(Long monthlyDeposit, ProductProperty property) {
        if (monthlyDeposit == null) return 0.0;

        Long maxMonthlyLimit = property.getMaxMonthlyLimit();
        // 납입 한도가 없거나 월최대납입한도보다 희망납입액이 적으면 100%
        if (maxMonthlyLimit == null) return 1.0;
        if (monthlyDeposit <= maxMonthlyLimit) return 1.0;
        // 아니면 월최대납입한도 / 희망납입액
        return (double) maxMonthlyLimit / monthlyDeposit;
    }

    // 은행 거래 조건 점수 계산
    private double calcBankCondScore(
            List<KeywordValueEnum> selected,
            Set<KeywordValueEnum> propertyKeywords,
            ProductProperty property,
            SearchRequestDto request
    ) {
        if (selected.isEmpty()) return 0.0;
        long matched = selected.stream()
                .filter(keyword -> matchesBankCondition(keyword, propertyKeywords, property, request))
                .count();
        return (double) matched / selected.size();
    }

    // 은행 상품의 우대금리 매칭되는지 검사
    private boolean matchesBankCondition(
            KeywordValueEnum keyword,
            Set<KeywordValueEnum> propertyKeywords,
            ProductProperty property,
            SearchRequestDto request
    ) {
        if (keyword == BANK_AGE) {
            return BankConditionMatcher.hasYouthAgeCondition(property);
        }

        // 상품 속성이 해당 키워드를 포함하고 있지 않으면 false
        if (!propertyKeywords.contains(keyword)) {
            return false;
        }

        // 첫거래·재예치는 단계2 거래이력으로 추론된 은행에만 매칭 (PRD: 단계2 자동 추론 전용).
        // 은행 리스트가 비면 매칭 인정하지 않는다.

        // 첫거래
        if (keyword == BANK_FIRST_TRANSACTION) {
            return property.matchesAnyProvider(request.neverUsedBanks());
        }

        // 재예치
        if (keyword == BANK_REDEPOSIT) {
            return property.matchesAnyProvider(request.maturedSavingBanks());
        }

        return true;
    }

    // 활성화된 은행 키워드 계산
    private List<KeywordValueEnum> activeBankConditions(
            List<KeywordValueEnum> selected,
            ProductProperty property,
            SearchRequestDto request,
            boolean includeTransactionHistory,
            boolean isGov
    ) {
        LinkedHashSet<KeywordValueEnum> active = new LinkedHashSet<>();
        selected.stream()
                .filter(keyword -> !keyword.isTransactionHistoryCondition())
                .filter(keyword -> keyword != BANK_ETC)
                .forEach(active::add);
        if (!isGov) {
            if (property.keywordCodes().contains(BANK_ONLINE_JOIN)) {
                active.add(BANK_ONLINE_JOIN);
            }
            if (BankConditionMatcher.hasYouthAgeCondition(property)) {
                active.add(BANK_AGE);
            }
        }

        // 거래 내역 미포함시 계산하지 않음
        if (!includeTransactionHistory || !request.hasTransactionHistory()) {
            return new ArrayList<>(active);
        }
        
        // neverUsedBanks가 하나 이상 선택되었고 BANK_FIRST_TRANSACTION이 포함되어 있지 않다면
        if (hasAny(request.neverUsedBanks())) {
            active.add(BANK_FIRST_TRANSACTION);
        }

        // maturedSavingBanks가 하나 이상 선택되었고 BANK_REDEPOSITE이 포함되어 있지 않다면
        if (hasAny(request.maturedSavingBanks())) {
            active.add(BANK_REDEPOSIT);
        }

        return new ArrayList<>(active);
    }

    // 리스트가 값을 가지고 있는지 검사
    private boolean hasAny(List<String> values) {
        return values != null && !values.isEmpty();
    }

    // 가중치 분배
    private Map<String, Double> distributeWeights(
            List<KeywordValueEnum> coreBenefits,
            List<KeywordValueEnum> identities,
            List<KeywordValueEnum> bankConditions,
            KeywordValueEnum savingPeriod,
            Long monthlyDeposit,
            ProductProperty property,
            boolean isGov
    ) {
        Map<String, Double> weights = new HashMap<>(ScoreWeightEnum.baseWeights(isGov));

        List<String> inactive = new ArrayList<>();

        // 적용 가능한 혜택 키워드가 없으면
        if (applicableBenefitKeywords(coreBenefits, isGov).isEmpty()) {
            inactive.add(weightKey(isGov, ScoreWeightEnum.GOV_BENEFITS, ScoreWeightEnum.BANK_BENEFITS));
        }

        // 저축 기간이 없으면
        if (savingPeriod == null) {
            inactive.add(weightKey(isGov, ScoreWeightEnum.GOV_PERIOD, ScoreWeightEnum.BANK_PERIOD));
        }

        // 현재 신분이 선택되지 않았으면
        if (identities.isEmpty()) {
            inactive.add(weightKey(isGov, ScoreWeightEnum.GOV_IDENTITY, ScoreWeightEnum.BANK_IDENTITY));
        }

        // 월 납입 희망액이 없으면 납입한도 점수를 산출할 수 없으므로 재배분 대상 (A-3)
        if (monthlyDeposit == null) {
            inactive.add(weightKey(isGov, ScoreWeightEnum.GOV_DEPOSIT, ScoreWeightEnum.BANK_DEPOSIT));
        }

        // 은행 거래 조건이 비었거나, 유형 1(은행취급상품)이 아닌 경우
        if (bankConditions.isEmpty() || isGovBankConditionExcluded(isGov)) {
            inactive.add(weightKey(isGov, ScoreWeightEnum.GOV_BANK_COND, ScoreWeightEnum.BANK_BANK_COND));
        }

        // inactive 없으면 기본 가중치 바로 반환
        if (inactive.isEmpty()) return weights;

        // inactive로 인해 채점되지 않는 점수들의 총합
        double removedTotal = inactive.stream().mapToDouble(weights::get).sum();
        // inactive들의 가중치를 0으로 설정
        inactive.forEach(k -> weights.put(k, 0.0));
        // 활성화된 점수 총합
        double activeTotal = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        // active들의 가중치를 비율대로 조정
        // new_weight = weight + ( removedTotal * (v / activeTotal) )
        weights.replaceAll((k, v) -> v > 0 ? v + removedTotal * (v / activeTotal) : 0.0);

        return weights;
    }


    // 정부상품 여부에 따라 적용가능한 핵심 혜택 키워드를 반환
    // 상품 유형별로 실제 매칭 가능한 혜택 키워드만 남긴다.
    // 정부: 정부기여금·비과세·우대조건간편 (최고이율은 정부 금리 미공시로 매칭 불가하여 제외)
    // 은행: 최고이율·우대조건간편 (정부기여금·비과세는 은행 상품에 해당 없음)
    private static final Set<KeywordValueEnum> GOV_APPLICABLE_BENEFITS =
            Set.of(BENEFIT_GOV_SUBSIDY, BENEFIT_TAX_FREE, BENEFIT_EASY_CONDITION);
    private static final Set<KeywordValueEnum> BANK_APPLICABLE_BENEFITS =
            Set.of(BENEFIT_MAX_INTEREST, BENEFIT_EASY_CONDITION);

    private List<KeywordValueEnum> applicableBenefitKeywords(List<KeywordValueEnum> selected, boolean isGov) {
        Set<KeywordValueEnum> applicable = isGov ? GOV_APPLICABLE_BENEFITS : BANK_APPLICABLE_BENEFITS;
        return selected.stream()
                .filter(applicable::contains)
                .toList();
    }

    private boolean isGovBankConditionExcluded(boolean isGov) {
        return isGov;
    }


    // 저축기간을 반환
    private int[] periodRange(KeywordValueEnum kw) {
        return switch (kw) {
            case TERM_AROUND_1_YEAR -> new int[]{6, 12};
            case TERM_2_TO_3_YEARS -> new int[]{24, 36};
            case TERM_OVER_3_YEARS -> new int[]{37, Integer.MAX_VALUE};
            default -> new int[]{0, 0};
        };
    }

    // ProductProperty의 저축 기간이 사용자가 선택한 기간과 인접한지 여부를 판별
    private boolean isAdjacentOption(ProductProperty property, KeywordValueEnum selected) {
        int trm = property.getSaveTrm();
        return switch (selected) {
            case TERM_AROUND_1_YEAR -> trm == 24;
            case TERM_2_TO_3_YEARS  -> trm == 12;
            case TERM_OVER_3_YEARS  -> trm == 36;
            default -> false;
        };
    }

    // 특화 키워드 여부를 판별
    private boolean isSpecializedKeyword(KeywordValueEnum kw) {
        return kw == STATUS_MILITARY || kw == STATUS_SME_WORKER || kw == STATUS_UNEMPLOYED;
    }

    private record ProductPropertyScore(
            ProductProperty property,
            double totalScore,
            double benefitScore,
            double periodScore,
            double identityScore,
            double depositScore,
            double bankCondScore
    ) {
    }
}
