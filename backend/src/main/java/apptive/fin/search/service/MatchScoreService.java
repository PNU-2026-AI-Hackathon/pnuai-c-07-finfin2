package apptive.fin.search.service;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.ScoreWeightEnum;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductKeyword;
import apptive.fin.search.entity.ProductProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static apptive.fin.search.KeywordValueEnum.*;

@Service
@RequiredArgsConstructor
public class MatchScoreService {

    private final ResolveKeywordService resolveKeywordService;

    public ProductMatchDto score(Product p, SearchRequestDto request) {
        return score(p, request, resolveKeywordService.resolveKeywords(request.options()));
    }

    public ProductMatchDto score(Product p, SearchRequestDto request, ResolvedKeywords keywords) {
        return score(p, request, keywords, false);
    }

    public ProductMatchDto score(
            Product p,
            SearchRequestDto request,
            ResolvedKeywords keywords,
            boolean includeTransactionHistory
    ) {
        var detail = request.detailedOptions();

        boolean isGov = p.getSource().getCode().equals("ONTONG");

        // 모든 property별 점수 계산 후 최고 점수인 property 선택
        ProductPropertyScore bestScore = p.getProperties().stream()
                .map(property -> scoreProperty(
                        property,
                        keywords.coreBenefits(),
                        keywords.identities(),
                        keywords.bankConditions(),
                        keywords.savingPeriod(),
                        detail != null ? detail.monthlySavingsGoal() : null,
                        isGov,
                        request,
                        includeTransactionHistory,
                        null
                ))
                .max((a, b) -> Double.compare(a.totalScore(), b.totalScore()))
                .orElseGet(() -> new ProductPropertyScore(null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));

        return ProductMatchDto.builder()
                .productId(p.getId())
                .productPropertyId(bestScore.property() != null ? bestScore.property().getId() : null)
                .productName(p.getProductName())
                .providerName(providerName(bestScore.property()))
                .source(p.getSource().getCode())
                .totalScore(bestScore.totalScore())
                .benefitScore(bestScore.benefitScore())
                .periodScore(bestScore.periodScore())
                .identityScore(bestScore.identityScore())
                .depositScore(bestScore.depositScore())
                .bankCondScore(bestScore.bankCondScore())
                .build();
    }

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
        var detail = request.detailedOptions();
        boolean isGov = product.getSource().getCode().equals("ONTONG");

        ProductPropertyScore score = scoreProperty(
                property,
                keywords.coreBenefits(),
                keywords.identities(),
                keywords.bankConditions(),
                keywords.savingPeriod(),
                detail != null ? detail.monthlySavingsGoal() : null,
                isGov,
                request,
                includeTransactionHistory,
                bankMaxInterestThreshold
        );

        return ProductMatchDto.builder()
                .productId(product.getId())
                .productPropertyId(property.getId())
                .productName(product.getProductName())
                .providerName(providerName(property))
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
        List<KeywordValueEnum> propertyKeywords = property.getKeywords().stream()
                .map(ProductKeyword::getKeywordCode)
                .toList();

        List<KeywordValueEnum> activeBankConditions = activeBankConditions(
                bankConditions,
                request,
                includeTransactionHistory
        );

        Map<String, Double> weights = distributeWeights(
                coreBenefits,
                identities,
                activeBankConditions,
                savingPeriod,
                monthlyDeposit,
                property,
                isGov
        );

        double benefitScore = calcBenefitScore(coreBenefits, property, propertyKeywords, isGov, bankMaxInterestThreshold)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_BENEFITS, ScoreWeightEnum.BANK_BENEFITS));
        double periodScore = calcPeriodScore(savingPeriod, property)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_PERIOD, ScoreWeightEnum.BANK_PERIOD));
        double identityScore = calcIdentityScore(identities, propertyKeywords, isGov)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_IDENTITY, ScoreWeightEnum.BANK_IDENTITY));
        double depositScore = calcDepositScore(monthlyDeposit, property)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_DEPOSIT, ScoreWeightEnum.BANK_DEPOSIT));
        double bankCondScore = (isGovBankConditionExcluded(isGov, property)
                ? 0.0
                : calcBankCondScore(activeBankConditions, propertyKeywords, property, request))
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_BANK_COND, ScoreWeightEnum.BANK_BANK_COND));
        double totalScore = benefitScore + periodScore + identityScore + depositScore + bankCondScore;

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

    private String weightKey(boolean isGov, ScoreWeightEnum govWeight, ScoreWeightEnum bankWeight) {
        return isGov ? govWeight.getKey() : bankWeight.getKey();
    }

    private double calcBenefitScore(
            List<KeywordValueEnum> selected,
            ProductProperty property,
            List<KeywordValueEnum> propertyKeywords,
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

    private boolean isBenefitMatched(
            KeywordValueEnum keyword,
            ProductProperty property,
            List<KeywordValueEnum> propertyKeywords,
            boolean isGov,
            Double bankMaxInterestThreshold
    ) {
        // 은행 #최고이율_중심: 정적 태그가 아니라 결과셋 최고금리(maxRate) 상위 30% 임계값 이상인지로 동적 판정 (PRD A-2).
        // 정부 상품/임계값 미제공 시에는 기존 태그 방식 유지 (정부는 금리 대부분 미공시).
        if (keyword == BENEFIT_MAX_INTEREST && !isGov && bankMaxInterestThreshold != null) {
            return property.getMaxRate() != null
                    && property.getMaxRate().doubleValue() >= bankMaxInterestThreshold;
        }
        return propertyKeywords.contains(keyword);
    }

    private double calcPeriodScore(KeywordValueEnum selected, ProductProperty property) {
        if (selected == null || property.getSaveTrm() == null) return 0.0;

        int[] range = periodRange(selected);
        int saveTrm = property.getSaveTrm();
        if (saveTrm >= range[0] && saveTrm <= range[1]) return 1.0;
        return isAdjacentOption(property, selected) ? 0.5 : 0.0;
    }

    private double calcIdentityScore(List<KeywordValueEnum> selected, List<KeywordValueEnum> propertyKeywords, boolean isGov) {
        if (selected.isEmpty()) return 0;

        if (isGov) {
            boolean specialized = selected.stream().anyMatch(kw ->
                    propertyKeywords.contains(kw) && isSpecializedKeyword(kw));
            boolean included = selected.stream().anyMatch(propertyKeywords::contains);
            if (specialized) return 1.0;
            if (included) return 0.5;
            return 0;
        }

        return selected.stream().anyMatch(propertyKeywords::contains) ? 1.0 : 0;
    }

    private double calcDepositScore(Long monthlyDeposit, ProductProperty property) {
        if (monthlyDeposit == null) return 0.0;

        Long maxMonthlyLimit = property.getMaxMonthlyLimit();
        if (maxMonthlyLimit == null) return 1.0;
        if (monthlyDeposit <= maxMonthlyLimit) return 1.0;
        return (double) maxMonthlyLimit / monthlyDeposit;
    }

    private double calcBankCondScore(
            List<KeywordValueEnum> selected,
            List<KeywordValueEnum> propertyKeywords,
            ProductProperty property,
            SearchRequestDto request
    ) {
        if (selected.isEmpty()) return 0.0;
        long matched = selected.stream()
                .filter(keyword -> matchesBankCondition(keyword, propertyKeywords, property, request))
                .count();
        return (double) matched / selected.size();
    }

    private boolean matchesBankCondition(
            KeywordValueEnum keyword,
            List<KeywordValueEnum> propertyKeywords,
            ProductProperty property,
            SearchRequestDto request
    ) {
        if (!propertyKeywords.contains(keyword)) {
            return false;
        }

        // 첫거래·재예치는 단계2 거래이력으로 추론된 은행에만 매칭 (PRD: 단계2 자동 추론 전용).
        // 은행 리스트가 비면 매칭 인정하지 않는다.
        if (keyword == BANK_FIRST_TRANSACTION) {
            return isProviderSelected(property, neverUsedBanks(request));
        }

        if (keyword == BANK_REDEPOSIT) {
            return isProviderSelected(property, maturedSavingBanks(request));
        }

        return true;
    }

    private List<KeywordValueEnum> activeBankConditions(
            List<KeywordValueEnum> selected,
            SearchRequestDto request,
            boolean includeTransactionHistory
    ) {
        List<KeywordValueEnum> active = new ArrayList<>(selected);
        if (!includeTransactionHistory) {
            return active;
        }

        if (hasAny(neverUsedBanks(request)) && !active.contains(BANK_FIRST_TRANSACTION)) {
            active.add(BANK_FIRST_TRANSACTION);
        }

        if (hasAny(maturedSavingBanks(request)) && !active.contains(BANK_REDEPOSIT)) {
            active.add(BANK_REDEPOSIT);
        }

        return active;
    }

    private boolean hasAny(List<String> values) {
        return values != null && !values.isEmpty();
    }

    private boolean isProviderSelected(ProductProperty property, List<String> selectedProviders) {
        if (property == null || property.getProvider() == null || selectedProviders == null) {
            return false;
        }

        String providerCode = property.getProvider().getCode();
        return selectedProviders.stream()
                .anyMatch(selected -> selected != null && selected.equals(providerCode));
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
        if (bankConditions.isEmpty() || isGovBankConditionExcluded(isGov, property)) {
            inactive.add(weightKey(isGov, ScoreWeightEnum.GOV_BANK_COND, ScoreWeightEnum.BANK_BANK_COND));
        }

        if (inactive.isEmpty()) return weights;

        double removedTotal = inactive.stream().mapToDouble(weights::get).sum();
        inactive.forEach(k -> weights.put(k, 0.0));
        double activeTotal = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        weights.replaceAll((k, v) -> v > 0 ? v + removedTotal * (v / activeTotal) : 0.0);

        return weights;
    }

    private List<KeywordValueEnum> applicableBenefitKeywords(List<KeywordValueEnum> selected, boolean isGov) {
        if (isGov) return selected;

        return selected.stream()
                .filter(kw -> kw == BENEFIT_MAX_INTEREST || kw == BENEFIT_EASY_CONDITION)
                .toList();
    }

    private boolean isGovBankConditionExcluded(boolean isGov, ProductProperty property) {
        return isGov;
    }

    private int[] periodRange(KeywordValueEnum kw) {
        return switch (kw) {
            case TERM_AROUND_1_YEAR -> new int[]{6, 12};
            case TERM_2_TO_3_YEARS -> new int[]{24, 36};
            case TERM_OVER_3_YEARS -> new int[]{37, Integer.MAX_VALUE};
            default -> new int[]{0, 0};
        };
    }

    private boolean isAdjacentOption(ProductProperty property, KeywordValueEnum selected) {
        int trm = property.getSaveTrm();
        return switch (selected) {
            case TERM_AROUND_1_YEAR -> trm == 24;
            case TERM_2_TO_3_YEARS  -> trm == 12;
            case TERM_OVER_3_YEARS  -> trm == 36;
            default -> false;
        };
    }

    private boolean isSpecializedKeyword(KeywordValueEnum kw) {
        return kw == STATUS_MILITARY || kw == STATUS_SME_WORKER || kw == STATUS_UNEMPLOYED;
    }

    private String providerName(ProductProperty property) {
        return property != null && property.getProvider() != null
                ? property.getProvider().getName()
                : null;
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
