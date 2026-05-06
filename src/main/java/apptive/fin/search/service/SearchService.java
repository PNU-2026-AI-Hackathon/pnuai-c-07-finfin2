package apptive.fin.search.service;

import apptive.fin.category.service.CategoryOptionService;
import apptive.fin.search.CategoryIdEnum;
import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.dto.*;
import apptive.fin.search.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final CategoryOptionService categoryOptionService;
    private final EligibilityFilterService eligibilityFilterService;
    private final MatchScoreService matchScoreService;
    private final RateCalculatorService rateCalculatorService;

    public ProductSearchResultDto search(SearchRequestDto request){
        // 자격 필터링
        List<Product> eligible = eligibilityFilterService.filterEligible(request);

        // source별 분리
        List<Product> govList = eligible.stream()
                .filter(p -> p.getSource().getCode().equals("ONTONG")).toList();
        List<Product> bankList = eligible.stream()
                .filter(p -> p.getSource().getCode().equals("FSS")).toList();

        // 탭 A
        List<ProductMatchDto> govRanked = govList.stream()
                .map(p -> matchScoreService.score(p, request))
                .sorted(Comparator.comparingDouble(ProductMatchDto::totalScore).reversed())
                .toList();
        List<ProductMatchDto> bankRanked = bankList.stream()
                .map(p -> matchScoreService.score(p, request))
                .sorted(Comparator.comparingDouble(ProductMatchDto::totalScore).reversed())
                .toList();

        // 탭 B
        List<ProductRateDto> allRated = Stream.concat(govList.stream(), bankList.stream())
                .map(p -> rateCalculatorService.calculate(p, request)).toList();

        List<ProductRateDto> rateRanked = allRated.stream()
                .filter(r -> !r.isSubscription())
                .sorted(Comparator.comparingDouble(ProductRateDto::achievableRate).reversed())
                .toList();

        List<ProductRateDto> subscriptions = allRated.stream()
                .filter(ProductRateDto::isSubscription)
                .toList();

        // TODO : 계산 로직 추가 후 최종 완성할 부분
        return ProductSearchResultDto.builder()
                .governmentRanked(govRanked)
                .bankRanked(bankRanked)
                .rateRanked(rateRanked)
                .subscriptionProducts(subscriptions)
                .build();
    }

    private ResolvedKeywords resolveKeywords(List<OptionRequestDto> options){
        Map<Long, KeywordValueEnum> mapping = categoryOptionService.getOptionMap();

        List<KeywordValueEnum> regions = new ArrayList<>();
        List<KeywordValueEnum> identities = new ArrayList<>();
        KeywordValueEnum savingPeriod = null;
        List<KeywordValueEnum> benefits = new ArrayList<>();
        List<KeywordValueEnum> bankConds = new ArrayList<>();

        for (OptionRequestDto option : options){
            KeywordValueEnum kw = mapping.get(option.optionId());
            if(kw == null) continue;

            Long categoryId = option.categoryId();
            if (categoryId.equals(CategoryIdEnum.REGION.getId())) regions.add(kw);
            else if(categoryId.equals(CategoryIdEnum.IDENTITY.getId())) identities.add(kw);
            else if(categoryId.equals(CategoryIdEnum.PERIOD.getId())) savingPeriod = kw;
            else if(categoryId.equals(CategoryIdEnum.BENEFIT.getId())) benefits.add(kw);
            else if(categoryId.equals(CategoryIdEnum.BANK_COND.getId())) bankConds.add(kw);
        }
        return new ResolvedKeywords(regions,identities,savingPeriod, bankConds,benefits);
    }
    public record ResolvedKeywords(
            List<KeywordValueEnum> regions,
            List<KeywordValueEnum> identities,
            KeywordValueEnum savingPeriod,
            List<KeywordValueEnum> coreBenefits,
            List<KeywordValueEnum> bankConditions
    ){}
}
