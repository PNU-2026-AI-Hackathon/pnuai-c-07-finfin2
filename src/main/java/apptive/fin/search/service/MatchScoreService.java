package apptive.fin.search.service;

import apptive.fin.search.CategoryIdEnum;
import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.ScoreWeightEnum;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.ProductKeyword;
import org.springframework.stereotype.Service;
import apptive.fin.search.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static apptive.fin.search.KeywordValueEnum.*;

// 탭 A - 나에게 맞는 순
@Service
public class MatchScoreService {

    public ProductMatchDto score(Product p, SearchRequestDto request) {
        var keywords = request.options();
        var detail = request.detailedOptions();

        boolean isGov = p.getSource().getCode().equals("ONTONG");
        Map<String, Double> weights = distributeWeights(keywords,isGov);

        List<KeywordValueEnum> productKeywords= p.getKeywords().stream().map(ProductKeyword::getKeywordCode).toList();

        Map<Long, KeywordValueEnum> mapping = getKeywordMapping(keywords);
        List<KeywordValueEnum> coreBenefits   = filterByCategory(mapping, CategoryIdEnum.BENEFIT.getId());
        List<KeywordValueEnum> identities     = filterByCategory(mapping, CategoryIdEnum.IDENTITY.getId());
        List<KeywordValueEnum> bankConditions = filterByCategory(mapping, CategoryIdEnum.BANK_COND.getId());
        KeywordValueEnum savingPeriod = mapping.values().stream()
                .filter(kw -> kw == TERM_AROUND_1_YEAR || kw == TERM_2_TO_3_YEARS || kw == TERM_OVER_5_YEARS)
                .findFirst().orElse(null);

        double benefitScore  = calcBenefitScore(coreBenefits, productKeywords, isGov)
                            * weights.get(ScoreWeightEnum.GOV_BENEFITS.getKey());
        double periodScore   = calcPeriodScore(savingPeriod, p)
                            * weights.get(ScoreWeightEnum.GOV_PERIOD.getKey());
        double identityScore = calcIdentityScore(identities, productKeywords, isGov)
                            * weights.get(ScoreWeightEnum.GOV_IDENTITY.getKey());
        double depositScore  = calcDepositScore(detail.monthlySavingsGoal(), p)
                            * weights.get(ScoreWeightEnum.GOV_DEPOSIT.getKey());
        double bankCondScore = calcBankCondScore(bankConditions, productKeywords)
                            * weights.get(ScoreWeightEnum.GOV_DEPOSIT.getKey());

        return ProductMatchDto.builder()
                .productId(p.getId())
                .productName(p.getProductName())
                .source(p.getSource().getCode())
                .totalScore(benefitScore + periodScore + identityScore + depositScore + bankCondScore)
                .benefitScore(benefitScore)
                .periodScore(periodScore)
                .identityScore(identityScore)
                .depositScore(depositScore)
                .bankCondScore(bankCondScore)
                .build();
    }

    // 점수 계산

    private double calcBenefitScore(List<KeywordValueEnum> selected, List<KeywordValueEnum> productKeywords, boolean isGov) {
        if (selected.isEmpty()) return 0.0;

        // 시중은행은 정부 전용 혜택 제외
        List<KeywordValueEnum> applicable = isGov ? selected : selected.stream()
                .filter(kw -> kw == BENEFIT_MAX_INTEREST || kw == BENEFIT_EASY_CONDITION)
                .toList();
        if (applicable.isEmpty()) return 0.0;

        long matched = applicable.stream()
                .filter(productKeywords::contains)
                .count();
        return (double) matched / applicable.size();
    }

    private double calcPeriodScore(KeywordValueEnum selected, Product p) {
        if (selected == null) return 0.0;

        // 금감원 상품은 options에서 기간 확인
        if (!p.getOptions().isEmpty()) {
            return p.getOptions().stream().anyMatch(opt -> {
                int[] range = periodRange(selected);
                return opt.getSaveTrm() >= range[0] && opt.getSaveTrm() <= range[1];
            }) ? 1.0 : isAdjacentOption(p, selected) ? 0.5 : 0.0;
        }

        return 0.0;
    }

    private double calcIdentityScore(List<KeywordValueEnum> selected, List<KeywordValueEnum> productKeywords, boolean isGov) {
        if (selected.isEmpty()) return isGov ? 0.25 : 0.4;

        if (isGov) {
            boolean specialized = selected.stream().anyMatch(kw ->
                    productKeywords.contains(kw) && isSpecializedKeyword(kw));
            boolean included = selected.stream().anyMatch(productKeywords::contains);
            if (specialized) return 1.0;
            if (included)    return 0.5;
            return 0.25;
        }

        return selected.stream().anyMatch(productKeywords::contains) ? 1.0 : 0.4;
    }

    private double calcDepositScore(long monthlyDeposit, Product p) {
        if (p.getMaxMonthlyLimit() == null) return 1.0;
        if (monthlyDeposit <= p.getMaxMonthlyLimit()) return 1.0;
        return (double) p.getMaxMonthlyLimit() / monthlyDeposit;
    }

    private double calcBankCondScore(List<KeywordValueEnum> selected, List<KeywordValueEnum> productKeywords) {
        if (selected.isEmpty()) return 0.0;
        long matched = selected.stream().filter(productKeywords::contains).count();
        return (double) matched / selected.size();
    }

    //가중치 재배분

    private Map<String, Double> distributeWeights(
            List<apptive.fin.search.dto.OptionRequestDto> options, boolean isGov
    ) {
        Map<String, Double> weights = new HashMap<>(ScoreWeightEnum.baseWeights(isGov));

        Map<Long, KeywordValueEnum> mapping = getKeywordMapping(options);

        List<String> inactive = new ArrayList<>();
        if (filterByCategory(mapping, CategoryIdEnum.BENEFIT.getId()).isEmpty()) inactive.add(ScoreWeightEnum.GOV_BENEFITS.getKey());
        if (filterByCategory(mapping, CategoryIdEnum.PERIOD.getId()).isEmpty()) inactive.add(ScoreWeightEnum.GOV_PERIOD.getKey());
        if (filterByCategory(mapping, CategoryIdEnum.BANK_COND.getId()).isEmpty()) inactive.add(ScoreWeightEnum.GOV_BANK_COND.getKey());

        if (inactive.isEmpty()) return weights;

        double removedTotal = inactive.stream().mapToDouble(weights::get).sum();
        inactive.forEach(k -> weights.put(k, 0.0));
        double activeTotal  = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        weights.replaceAll((k, v) -> v > 0 ? v + removedTotal * (v / activeTotal) : 0.0);

        return weights;
    }

    private Map<Long, KeywordValueEnum> getKeywordMapping(List<OptionRequestDto> options) {
        Map<Long, KeywordValueEnum> map = new HashMap<>();

        for (var opt : options) {
            KeywordValueEnum kw = KeywordValueEnum.from(String.valueOf(opt.optionId()));
            if (kw != null) map.put(opt.categoryId(), kw);
        }
        return map;
    }

    private List<KeywordValueEnum> filterByCategory(Map<Long, KeywordValueEnum> mapping, Long categoryId) {
        return mapping.entrySet().stream()
                .filter(e -> e.getKey().equals(categoryId))
                .map(Map.Entry::getValue)
                .toList();
    }

    private int[] periodRange(KeywordValueEnum kw) {
        return switch (kw) {
            case TERM_AROUND_1_YEAR -> new int[]{6,  18};
            case TERM_2_TO_3_YEARS  -> new int[]{19, 42};
            case TERM_OVER_5_YEARS  -> new int[]{43, Integer.MAX_VALUE};
            default -> new int[]{0, 0};
        };
    }

    private boolean isAdjacentOption(Product p, KeywordValueEnum selected) {
        return p.getOptions().stream().anyMatch(opt -> {
            int trm = opt.getSaveTrm();
            return switch (selected) {
                case TERM_AROUND_1_YEAR -> trm >= 19 && trm <= 42;
                case TERM_2_TO_3_YEARS  -> (trm >= 6 && trm <= 18) || trm >= 43;
                case TERM_OVER_5_YEARS  -> trm >= 19 && trm <= 42;
                default -> false;
            };
        });
    }

    private boolean isSpecializedKeyword(KeywordValueEnum kw) {
        return kw == STATUS_MILITARY || kw == STATUS_SME_WORKER || kw == STATUS_UNEMPLOYED;
    }

}
