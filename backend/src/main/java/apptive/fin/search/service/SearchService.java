package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.search.enums.KeywordValueEnum;

import apptive.fin.search.dto.*;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.repository.ProductRepository;

import apptive.fin.search.entity.ProductKeyword;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final EligibilityFilterService eligibilityFilterService;
    private final MatchScoreService matchScoreService;
    private final RateCalculatorService rateCalculatorService;
    private final ResolveKeywordService resolveKeywordService;
    private final ProductRepository productRepository;
    private final ProductCardSummaryService productCardSummaryService;
    private final SearchRequestPolicy searchRequestPolicy;

    public ProductSearchResultDto search(SearchRequestDto request) {
        return search(request, null);
    }

    // 상품 검색 결과를 반환하는 메서드
    public ProductSearchResultDto search(SearchRequestDto request, AuthUserDetails userDetails) {
        // 프론트에서 전송된 List<OptionRequestDto>를 ResolvedKeywords로 변환
        ResolvedKeywords resolvedKeywords = resolveKeywordService.resolveKeywords(request.options());
        searchRequestPolicy.validateForRecommendation(request, resolvedKeywords);

        // 사용자가 가입 가능한 상품 필터링
        List<EligibleProductOption> eligible = eligibilityFilterService.filterEligibleOptions(
                request,
                resolvedKeywords
        );
        
        // 선택된 지역 키워드가 빈칸이 아니라면
        if (!resolvedKeywords.regions().isEmpty()) {
            // 은행 상품이거나 해당 지역 키워드를 가진 상품만 필터링하여 eligible한 상품 업데이트
            eligible = eligible.stream()
                    .filter(option -> option.product().isBank() || hasMatchingRegion(option, resolvedKeywords.regions()))
                    .toList();
        }

	// 정부상품 목록
        List<EligibleProductOption> govList = eligible.stream()
                .filter(option -> option.product().isGovernment())
                .toList();

        // 은행상품 목록
        List<EligibleProductOption> bankList = eligible.stream()
                .filter(option -> option.product().isBank())
                .toList();

        // tabB 활성화 여부 판별
        boolean tabBEnabled = searchRequestPolicy.canUsePersonalization(
                request,
                resolvedKeywords,
                userDetails
        );

        // 은행 #최고이율_중심 상위 30% 판정용 임계 금리(결과셋 maxRate 기준). null이면 정적 태그 방식으로 폴백.
        List<Double> bankMaxInterestRates = bankList.stream()
                .map(option -> option.property().getMaxRate())
                .filter(Objects::nonNull)
                .map(java.math.BigDecimal::doubleValue)
                .toList();
        Double bankMaxInterestThreshold = BankMaxInterestPolicy.calculateThreshold(bankMaxInterestRates);

        // 정부상품 점수 계산 후 각 상품별로 총점이 가장 높은 (Product, ProductProperty) 쌍만 남긴 뒤 점수순 내림차순 정렬
        List<ProductMatchDto> govRanked = collapseToBestPerProduct(
                govList.stream()
                        .map(option -> matchScoreService.score(
                                option.product(),
                                option.property(),
                                request,
                                resolvedKeywords,
                                false
                        ))
        );

        // 은행상품 점수 계산 후 각 상품별로 총점이 가장 높은 (Product, ProductProperty) 쌍만 남긴 뒤 점수순 내림차순 정렬
        List<ProductMatchDto> bankRanked = collapseToBestPerProduct(
                bankList.stream()
                        .map(option -> matchScoreService.score(
                                option.product(),
                                option.property(),
                                request,
                                resolvedKeywords,
                                tabBEnabled,
                                bankMaxInterestThreshold
                        ))
        );

        // 탭별 활성화여부 계산
        TabAvailabilityDto tabs = TabAvailabilityDto.builder()
                .tabAEnabled(true)
                .tabBEnabled(tabBEnabled)
                .tabBDisabledReason(tabBEnabled ? null : "로그인 후 상세 정보를 입력하면 금리순 정렬을 확인할 수 있어요.")
                .build();
				
				
        // tabB가 활성화되어 있으면 정부상품 수익률 높은순으로 내림차순 정렬한 리스트, 비활성화면 빈 리스트				
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

				
	// tabB가 활성화되어 있으면 은행상품 이자율 높은순으로 내림차순 정렬한 리스트, 비활성화면 빈 리스트
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

        // 청약상품
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
                                        (left, right) -> left // 겹치면 무조건 먼저 들어온 것 선택
                                ),
                                map -> map.values().stream().toList() // 정렬없이 바로 리스트화 
                        ))
                : List.of();

        List<ProductCardSummaryDto> productCardSummaries = productCardSummaryService.build(
                eligible,
                govRanked,
                bankRanked,
                governmentRateRanked,
                bankRateRanked,
                request,
                resolvedKeywords,
                tabBEnabled,
                bankMaxInterestThreshold
        );

        return ProductSearchResultDto.builder()
                .tabs(tabs)
                .governmentRanked(govRanked)
                .bankRanked(bankRanked)
                .governmentRateRanked(governmentRateRanked)
                .bankRateRanked(bankRateRanked)
                .subscriptionProducts(subscriptions)
                .productCardSummaries(productCardSummaries)
                .eligibleProductCount(
                        eligible.stream()
                                .map(o->o.product().getId())
                                .distinct()
                                .count()
                )
                .build();
    }

    // 이름으로 찾기
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
                            .productName(p.getDisplayProductName())
                            .source(p.getSource().getCode())
                            .providerName(bestProperty != null ? bestProperty.providerName() : null)
                            .baseRate(bestProperty != null && bestProperty.getBaseRate() != null
                                    ? bestProperty.getBaseRate().doubleValue() : null)
                            .maxRate(bestProperty != null && bestProperty.getMaxRate() != null
                                    ? bestProperty.getMaxRate().doubleValue() : null)
                            .build();
                })
                .toList();
    }

    // 상품별로 총점이 가장 높은 (Product, ProductProperty) 쌍만 남기고 총점 내림차순 정렬(순수 함수, 테스트 용이하도록 분리).
    // 동점이면 먼저 계산된 항목을 유지(>=).
    static List<ProductMatchDto> collapseToBestPerProduct(Stream<ProductMatchDto> scored) {
        return scored.collect(Collectors.collectingAndThen(
                Collectors.toMap(
                        ProductMatchDto::productId,
                        Function.identity(),
                        (left, right) -> left.totalScore() >= right.totalScore() ? left : right
                ),
                map -> map.values().stream()
                        .sorted(Comparator.comparingDouble(ProductMatchDto::totalScore).reversed())
                        .toList()
        ));
    }
		
    // 달성 가능 금리 기준 내림차순 정렬
    private List<ProductRateDto> sortedByAchievableRate(Collection<ProductRateDto> products) {
        return products.stream()
                .sorted(Comparator.comparingDouble(ProductRateDto::achievableRate).reversed())
                .toList();
    }
		
    // 상품에서 매칭되는 지역 있는지 확인하는 함수
    private boolean hasMatchingRegion(EligibleProductOption option, List<KeywordValueEnum> selectedRegions) {
        List<KeywordValueEnum> productRegions = option.property().keywordCodes().stream()
                .filter(keyword -> keyword.name().startsWith("REGION_"))
                .toList();
                
        // 상품에 지역제한이 없거나 맞는 지역이 있으면 true
        return productRegions.isEmpty()
                || selectedRegions.stream().anyMatch(productRegions::contains);
    }

		
}
