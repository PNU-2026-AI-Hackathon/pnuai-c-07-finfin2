package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.KeywordValueEnum;

import apptive.fin.search.dto.*;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductKeyword;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.repository.ProductRepository;

import apptive.fin.search.SearchErrorCode;
import apptive.fin.search.dto.EligibleProductOption;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.ProductSearchResultDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.dto.TabAvailabilityDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductKeyword;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final EligibilityFilterService eligibilityFilterService;
    private final MatchScoreService matchScoreService;
    private final RateCalculatorService rateCalculatorService;
    private final ResolveKeywordService resolveKeywordService;
    private final ProductRepository productRepository;

    public ProductSearchResultDto search(SearchRequestDto request) {
        return search(request, null);
    }

    public ProductSearchResultDto search(SearchRequestDto request, AuthUserDetails userDetails) {
        ResolvedKeywords resolvedKeywords = resolveKeywordService.resolveKeywords(request.options());
        validateKeywordSelected(resolvedKeywords);

        List<EligibleProductOption> eligible = eligibilityFilterService.filterEligibleOptions(request);
        if (!resolvedKeywords.regions().isEmpty()) {
            eligible = eligible.stream()
                    .filter(option -> isBankProduct(option.product()) || hasMatchingRegion(option, resolvedKeywords.regions()))
                    .toList();
        }

        List<EligibleProductOption> govList = eligible.stream()
                .filter(option -> option.product().getSource().getCode().equals("ONTONG"))
                .toList();
        List<EligibleProductOption> bankList = eligible.stream()
                .filter(option -> option.product().getSource().getCode().equals("FSS"))
                .toList();

        boolean tabBEnabled = isTabBEnabled(request, userDetails);

        List<ProductMatchDto> govRanked = govList.stream()
                .map(option -> matchScoreService.score(
                        option.product(),
                        option.property(),
                        request,
                        resolvedKeywords,
                        false
                ))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                ProductMatchDto::productId,
                                Function.identity(),
                                (left, right) -> left.totalScore() >= right.totalScore() ? left : right
                        ),
                        map -> sortedByTotalScore(map.values())
                ));
        List<ProductMatchDto> bankRanked = bankList.stream()
                .map(option -> matchScoreService.score(
                        option.product(),
                        option.property(),
                        request,
                        resolvedKeywords,
                        tabBEnabled
                ))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                ProductMatchDto::productId,
                                Function.identity(),
                                (left, right) -> left.totalScore() >= right.totalScore() ? left : right
                        ),
                        map -> sortedByTotalScore(map.values())
                ));

        TabAvailabilityDto tabs = TabAvailabilityDto.builder()
                .tabAEnabled(true)
                .tabBEnabled(tabBEnabled)
                .tabBDisabledReason(tabBEnabled ? null : "로그인 후 상세 정보를 입력하면 금리순 정렬을 확인할 수 있어요.")
                .build();

        List<ProductRateDto> governmentRateRanked = tabBEnabled
                ? govList.stream()
                        .map(option -> rateCalculatorService.calculate(
                                option.product(),
                                option.property(),
                                request,
                                resolvedKeywords
                        ))
                        .filter(r -> !r.isSubscription())
                        .filter(ProductRateDto::rateComparable)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        ProductRateDto::productId,
                                        Function.identity(),
                                        (left, right) -> left.achievableRate() >= right.achievableRate() ? left : right
                                ),
                                map -> sortedByAchievableRate(map.values())
                        ))
                : List.of();

        List<ProductRateDto> bankRateRanked = tabBEnabled
                ? bankList.stream()
                        .map(option -> rateCalculatorService.calculate(
                                option.product(),
                                option.property(),
                                request,
                                resolvedKeywords
                        ))
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        ProductRateDto::productId,
                                        Function.identity(),
                                        (left, right) -> left.achievableRate() >= right.achievableRate() ? left : right
                                ),
                                map -> sortedByAchievableRate(map.values())
                        ))
                : List.of();

        List<ProductRateDto> subscriptions = tabBEnabled
                ? govList.stream()
                        .map(option -> rateCalculatorService.calculate(
                                option.product(),
                                option.property(),
                                request,
                                resolvedKeywords
                        ))
                        .filter(ProductRateDto::isSubscription)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        ProductRateDto::productId,
                                        Function.identity(),
                                        (left, right) -> left
                                ),
                                map -> map.values().stream().toList()
                        ))
                : List.of();

        return ProductSearchResultDto.builder()
                .tabs(tabs)
                .governmentRanked(govRanked)
                .bankRanked(bankRanked)
                .governmentRateRanked(governmentRateRanked)
                .bankRateRanked(bankRateRanked)
                .subscriptionProducts(subscriptions)
                .build();
    }

    public List<ProductNameSearchDto> searchByName(String searchInput){
        return productRepository.findByProductNameContaining(searchInput)
                .stream()
                .map(p -> {
                    ProductProperty bestProperty = p.getProperties().stream()
                            .max(Comparator.comparingDouble(pp ->
                                    pp.getMaxRate() != null ? pp.getMaxRate().doubleValue(): 0.0 ))
                            .orElse(null);

                    return ProductNameSearchDto.builder()
                            .productId(p.getId())
                            .productName(p.getProductName())
                            .source(p.getSource().getCode())
                            .providerName(bestProperty != null && bestProperty.getProvider() != null
                                    ? bestProperty.getProvider().getName() : null)
                            .baseRate(bestProperty != null && bestProperty.getBaseRate() != null
                                    ? bestProperty.getBaseRate().doubleValue() : null)
                            .maxRate(bestProperty != null && bestProperty.getMaxRate() != null
                                    ? bestProperty.getMaxRate().doubleValue() : null)
                            .build();
                })
                .toList();
    }


    private boolean isTabBEnabled(SearchRequestDto request, AuthUserDetails userDetails) {
        if (userDetails == null || request.detailedOptions() == null) {
            return false;
        }

        var detail = request.detailedOptions();
        return detail.neverUsedBanks() != null
                && detail.maturedSavingBanks() != null;
    }

    private void validateKeywordSelected(ResolvedKeywords keywords) {
        boolean hasSelectedKeyword = !keywords.regions().isEmpty()
                || !keywords.identities().isEmpty()
                || keywords.savingPeriod() != null
                || !keywords.coreBenefits().isEmpty()
                || !keywords.bankConditions().isEmpty();

        if (!hasSelectedKeyword) {
            throw new BusinessException(SearchErrorCode.KEYWORD_REQUIRED);
        }
    }

    private List<ProductMatchDto> sortedByTotalScore(Collection<ProductMatchDto> products) {
        return products.stream()
                .sorted(Comparator.comparingDouble(ProductMatchDto::totalScore).reversed())
                .toList();
    }

    private List<ProductRateDto> sortedByAchievableRate(Collection<ProductRateDto> products) {
        return products.stream()
                .sorted(Comparator.comparingDouble(ProductRateDto::achievableRate).reversed())
                .toList();
    }

    private boolean hasMatchingRegion(EligibleProductOption option, List<KeywordValueEnum> selectedRegions) {
        List<KeywordValueEnum> productRegions = option.property().getKeywords().stream()

                .map(ProductKeyword::getKeywordCode)
                .filter(keyword -> keyword.name().startsWith("REGION_"))
                .toList();

        return productRegions.isEmpty()
                || selectedRegions.stream().anyMatch(productRegions::contains);
    }

    private boolean isBankProduct(Product product) {
        return product.getSource().getCode().equals("FSS");
    }
}
