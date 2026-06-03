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
        var detail = request.detailedOptions();

        boolean isGov = p.getSource().getCode().equals("ONTONG");
        Map<String, Double> weights = distributeWeights(keywords, isGov);

        // 모든 property 점수 계산 후 최고 점수 선택
        ProductPropertyScore bestScore = p.getProperties().stream()
                .map(property -> scoreProperty(
                        property,
                        keywords.coreBenefits(),
                        keywords.identities(),
                        keywords.bankConditions(),
                        keywords.savingPeriod(),
                        detail.monthlySavingsGoal(),
                        isGov,
                        weights
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

    private ProductPropertyScore scoreProperty(
            ProductProperty property,
            List<KeywordValueEnum> coreBenefits,
            List<KeywordValueEnum> identities,
            List<KeywordValueEnum> bankConditions,
            KeywordValueEnum savingPeriod,
            Long monthlyDeposit,
            boolean isGov,
            Map<String, Double> weights
    ) {
        List<KeywordValueEnum> propertyKeywords = property.getKeywords().stream()
                .map(ProductKeyword::getKeywordCode)
                .toList();

        double benefitScore = calcBenefitScore(coreBenefits, propertyKeywords, isGov)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_BENEFITS, ScoreWeightEnum.BANK_BENEFITS));
        double periodScore = calcPeriodScore(savingPeriod, property)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_PERIOD, ScoreWeightEnum.BANK_PERIOD));
        double identityScore = calcIdentityScore(identities, propertyKeywords, isGov)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_IDENTITY, ScoreWeightEnum.BANK_IDENTITY));
        double depositScore = calcDepositScore(monthlyDeposit, property)
                * weights.get(weightKey(isGov, ScoreWeightEnum.GOV_DEPOSIT, ScoreWeightEnum.BANK_DEPOSIT));
        double bankCondScore = (isGov
                ? calcGovBankCondScore(bankConditions, propertyKeywords)
                : calcBankCondScore(bankConditions, propertyKeywords))
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

    private double calcBenefitScore(List<KeywordValueEnum> selected, List<KeywordValueEnum> propertyKeywords, boolean isGov) {
        if (selected.isEmpty()) return 0.0;

        List<KeywordValueEnum> applicable = isGov ? selected : selected.stream()
                .filter(kw -> kw == BENEFIT_MAX_INTEREST || kw == BENEFIT_EASY_CONDITION)
                .toList();
        if (applicable.isEmpty()) return 0.0;

        long matched = applicable.stream()
                .filter(propertyKeywords::contains)
                .count();
        return (double) matched / applicable.size();
    }

    private double calcPeriodScore(KeywordValueEnum selected, ProductProperty property) {
        if (selected == null || property.getSaveTrm() == null) return 0.0;

        int[] range = periodRange(selected);
        int saveTrm = property.getSaveTrm();
        if (saveTrm >= range[0] && saveTrm <= range[1]) return 1.0;
        return isAdjacentOption(property, selected) ? 0.5 : 0.0;
    }

    private double calcIdentityScore(List<KeywordValueEnum> selected, List<KeywordValueEnum> propertyKeywords, boolean isGov) {
        if (selected.isEmpty()) return isGov ? 0.25 : 0.4;

        if (isGov) {
            boolean specialized = selected.stream().anyMatch(kw ->
                    propertyKeywords.contains(kw) && isSpecializedKeyword(kw));
            boolean included = selected.stream().anyMatch(propertyKeywords::contains);
            if (specialized) return 1.0;
            if (included) return 0.5;
            return 0.25;
        }

        return selected.stream().anyMatch(propertyKeywords::contains) ? 1.0 : 0.4;
    }

    private double calcDepositScore(Long monthlyDeposit, ProductProperty property) {
        if (monthlyDeposit == null) return 0.0;

        Long maxMonthlyLimit = property.getMaxMonthlyLimit();
        if (maxMonthlyLimit == null) return 1.0;
        if (monthlyDeposit <= maxMonthlyLimit) return 1.0;
        return (double) maxMonthlyLimit / monthlyDeposit;
    }

    private double calcBankCondScore(List<KeywordValueEnum> selected, List<KeywordValueEnum> propertyKeywords) {
        if (selected.isEmpty()) return 0.0;
        long matched = selected.stream().filter(propertyKeywords::contains).count();
        return (double) matched / selected.size();
    }

    private double calcGovBankCondScore(List<KeywordValueEnum> selected, List<KeywordValueEnum> propertyKeywords) {
        boolean isSubscription = propertyKeywords.stream()
                .anyMatch(kw -> kw == INTEREST_SAVINGS);
        if (isSubscription) return 0.0;

        return calcBankCondScore(selected, propertyKeywords);
    }

    private Map<String, Double> distributeWeights(ResolvedKeywords keywords, boolean isGov) {
        Map<String, Double> weights = new HashMap<>(ScoreWeightEnum.baseWeights(isGov));

        List<String> inactive = new ArrayList<>();
        if (keywords.coreBenefits().isEmpty()) {
            inactive.add(weightKey(isGov, ScoreWeightEnum.GOV_BENEFITS, ScoreWeightEnum.BANK_BENEFITS));
        }
        if (keywords.savingPeriod() == null) {
            inactive.add(weightKey(isGov, ScoreWeightEnum.GOV_PERIOD, ScoreWeightEnum.BANK_PERIOD));
        }
        if (keywords.bankConditions().isEmpty()) {
            inactive.add(weightKey(isGov, ScoreWeightEnum.GOV_BANK_COND, ScoreWeightEnum.BANK_BANK_COND));
        }

        if (inactive.isEmpty()) return weights;

        double removedTotal = inactive.stream().mapToDouble(weights::get).sum();
        inactive.forEach(k -> weights.put(k, 0.0));
        double activeTotal = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        weights.replaceAll((k, v) -> v > 0 ? v + removedTotal * (v / activeTotal) : 0.0);

        return weights;
    }

    private int[] periodRange(KeywordValueEnum kw) {
        return switch (kw) {
            case TERM_AROUND_1_YEAR -> new int[]{6, 18};
            case TERM_2_TO_3_YEARS -> new int[]{19, 42};
            case TERM_OVER_5_YEARS -> new int[]{43, Integer.MAX_VALUE};
            default -> new int[]{0, 0};
        };
    }

    private boolean isAdjacentOption(ProductProperty property, KeywordValueEnum selected) {
        int trm = property.getSaveTrm();
        return switch (selected) {
            case TERM_AROUND_1_YEAR -> trm >= 19 && trm <= 42;
            case TERM_2_TO_3_YEARS -> (trm >= 6 && trm <= 18) || trm >= 43;
            case TERM_OVER_5_YEARS -> trm >= 19 && trm <= 42;
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
