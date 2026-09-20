package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductCategoryEnum;

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
    private final ParkingProductService parkingProductService;

    public ProductSearchResultDto search(SearchRequestDto request) {
        return search(request, null);
    }

    /**
     * 통합 검색 (대분류 라우팅).
     * saveTrmExact 또는 savingPeriod 키워드에 따라 대분류를 결정하고 해당 파이프라인 실행.
     */
    public UnifiedSearchResultDto searchUnified(SearchRequestDto request, AuthUserDetails userDetails) {
        ResolvedKeywords resolvedKeywords = resolveKeywordService.resolveKeywords(request.options());
        ProductCategoryEnum category = determineCategory(request, resolvedKeywords);

        if (category == ProductCategoryEnum.SHORT_TERM) {
            ShortTermResultDto shortTermResult = searchShortTerm(request, userDetails, resolvedKeywords);
            return UnifiedSearchResultDto.fromShortTerm(shortTermResult);
        } else {
            ProductSearchResultDto longTermResult = searchLongTerm(request, userDetails, resolvedKeywords);
            return UnifiedSearchResultDto.fromLongTerm(longTermResult);
        }
    }

    public UnifiedSearchResultDto searchUnified(SearchRequestDto request) {
        return searchUnified(request, null);
    }

    /**
     * 대분류 결정 로직.
     * 1순위: saveTrmExact (정확한 개월 수)
     * 2순위: savingPeriod 키워드 (레거시 호환)
     * 기본값: LONG_TERM (목돈만들기)
     */
    private ProductCategoryEnum determineCategory(SearchRequestDto request, ResolvedKeywords resolvedKeywords) {
        // 1순위: saveTrmExact
        Integer saveTrmExact = request.saveTrmExact();
        if (saveTrmExact != null) {
            return ProductCategoryEnum.fromSaveTrm(saveTrmExact);
        }

        // 2순위: savingPeriod 키워드
        KeywordValueEnum savingPeriod = resolvedKeywords.savingPeriod();
        if (savingPeriod != null) {
            return ProductCategoryEnum.fromKeyword(savingPeriod);
        }

        // 기본값
        return ProductCategoryEnum.LONG_TERM;
    }

    /**
     * 단기예치 검색 파이프라인.
     * - 파킹통장 탭: 최고금리순, 비로그인 허용
     * - 예적금 탭: 세후 실수령액순, 로그인 필요
     */
    public ShortTermResultDto searchShortTerm(SearchRequestDto request, AuthUserDetails userDetails, ResolvedKeywords resolvedKeywords) {
        // 단기예치 검증 (파킹통장 탭은 예치액만 필수, 은행조건 불필요)
        searchRequestPolicy.validateForShortTerm(request);

        // 가입 가능 상품 필터링
        List<EligibleProductOption> eligible = eligibilityFilterService.filterEligibleOptions(request, resolvedKeywords);

        // 은행 상품만 (단기예치는 은행 예적금 + 파킹통장)
        List<EligibleProductOption> bankList = eligible.stream()
                .filter(option -> option.product().isBank())
                .toList();

        // 저축기간 필터 (선택된 기간과 일치하는 상품만)
        Integer targetSaveTrm = request.saveTrmExact();
        List<EligibleProductOption> filteredByTerm = targetSaveTrm != null
                ? bankList.stream()
                        .filter(option -> targetSaveTrm.equals(option.property().getSaveTrm()))
                        .toList()
                : bankList;

        // 파킹통장 목록 (최고금리순, 비로그인 허용)
        List<ParkingProductDto> parkingProducts = parkingProductService.findParkingProducts(request);

        // tabB (예적금 탭) 활성화 여부 - 단기예치 정책 사용
        boolean tabBEnabled = searchRequestPolicy.canUseShortTermPersonalization(request, userDetails);

        // 예적금 탭: 세후 실수령액순 정렬
        List<ProductRateDto> depositSavingsProducts = tabBEnabled
                ? filteredByTerm.stream()
                        .map(option -> rateCalculatorService.calculate(
                                option.product(),
                                option.property(),
                                request,
                                resolvedKeywords
                        ))
                        .filter(dto -> dto.netReturn() != null)  // 세후 실수령액 계산 가능한 상품만
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        ProductRateDto::productId,
                                        Function.identity(),
                                        (left, right) -> compareNetReturn(left, right) >= 0 ? left : right
                                ),
                                map -> sortedByNetReturn(map.values())
                        ))
                : List.of();

        // 탭 활성화 상태
        TabAvailabilityDto tabs = TabAvailabilityDto.builder()
                .tabAEnabled(true)  // 파킹통장 탭 (비로그인 허용)
                .tabBEnabled(tabBEnabled)  // 예적금 탭 (로그인 필요)
                .tabBDisabledReason(tabBEnabled ? null : "로그인 후 상세 정보를 입력하면 예적금 탭을 확인할 수 있어요.")
                .build();

        // 카드 요약 (단기예치용)
        List<ProductCardSummaryDto> productCardSummaries = List.of(); // TODO: 단기예치용 카드 요약 구현

        return ShortTermResultDto.builder()
                .tabs(tabs)
                .parkingProducts(parkingProducts)
                .depositSavingsProducts(depositSavingsProducts)
                .productCardSummaries(productCardSummaries)
                .eligibleProductCount(
                        filteredByTerm.stream()
                                .map(o -> o.product().getId())
                                .distinct()
                                .count()
                )
                .build();
    }

    /**
     * 목돈만들기 검색 파이프라인 (기존 search 로직).
     */
    public ProductSearchResultDto searchLongTerm(SearchRequestDto request, AuthUserDetails userDetails, ResolvedKeywords resolvedKeywords) {
        searchRequestPolicy.validateForRecommendation(request, resolvedKeywords);
        return searchInternal(request, userDetails, resolvedKeywords);
    }

    // 상품 검색 결과를 반환하는 메서드 (레거시 호환)
    public ProductSearchResultDto search(SearchRequestDto request, AuthUserDetails userDetails) {
        ResolvedKeywords resolvedKeywords = resolveKeywordService.resolveKeywords(request.options());
        searchRequestPolicy.validateForRecommendation(request, resolvedKeywords);
        return searchInternal(request, userDetails, resolvedKeywords);
    }

    // 내부 검색 로직 (키워드 해석 완료 후 호출)
    private ProductSearchResultDto searchInternal(SearchRequestDto request, AuthUserDetails userDetails, ResolvedKeywords resolvedKeywords) {

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

        // 정부상품 점수 계산 + tieBreaker(기여금총액) 설정 후 정렬
        // PRD 동점 규칙: 적합도 → 기여금총액 → 상품명
        List<ProductMatchDto> govRanked = collapseToBestPerProduct(
                govList.stream()
                        .map(option -> {
                            ProductMatchDto dto = matchScoreService.score(
                                    option.product(),
                                    option.property(),
                                    request,
                                    resolvedKeywords,
                                    false
                            );
                            // tieBreaker = 기여금총액 (정부상품)
                            ProductRateDto rateDto = rateCalculatorService.calculate(
                                    option.product(),
                                    option.property(),
                                    request,
                                    resolvedKeywords
                            );
                            Long tieBreaker = rateDto.netReturn();  // 정부: 원금+기여금 = netReturn
                            return withTieBreaker(dto, tieBreaker);
                        })
        );

        // 은행상품 점수 계산 + tieBreaker(세후실수령액) 설정 후 정렬
        // PRD 동점 규칙: 적합도 → 실수령액 → 상품명
        List<ProductMatchDto> bankRanked = collapseToBestPerProduct(
                bankList.stream()
                        .map(option -> {
                            ProductMatchDto dto = matchScoreService.score(
                                    option.product(),
                                    option.property(),
                                    request,
                                    resolvedKeywords,
                                    tabBEnabled,
                                    bankMaxInterestThreshold
                            );
                            // tieBreaker = 세후실수령액 (은행상품)
                            ProductRateDto rateDto = rateCalculatorService.calculate(
                                    option.product(),
                                    option.property(),
                                    request,
                                    resolvedKeywords
                            );
                            Long tieBreaker = rateDto.netReturn();
                            return withTieBreaker(dto, tieBreaker);
                        })
        );

        // 탭별 활성화여부 계산
        TabAvailabilityDto tabs = TabAvailabilityDto.builder()
                .tabAEnabled(true)
                .tabBEnabled(tabBEnabled)
                .tabBDisabledReason(tabBEnabled ? null : "로그인 후 상세 정보를 입력하면 금리순 정렬을 확인할 수 있어요.")
                .build();
				
				
        // tabB가 활성화되어 있으면 정부상품 기여금총액(수익률) 높은순으로 정렬, 비활성화면 빈 리스트
        // PRD 동점 규칙: 기여금총액 → 환산수익률 → 상품명
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
                                map -> sortedByGovernmentRate(map.values())
                        ))
                : List.of();

        // tabB가 활성화되어 있으면 은행상품 세후실수령액순 정렬, 비활성화면 빈 리스트
        // PRD 동점 규칙: 실수령액 → 최고금리 → 상품명
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
                                        // 세후실수령액 기준으로 베스트 선택
                                        (left, right) -> {
                                            long leftReturn = left.netReturn() != null ? left.netReturn() : 0L;
                                            long rightReturn = right.netReturn() != null ? right.netReturn() : 0L;
                                            return leftReturn >= rightReturn ? left : right;
                                        }
                                ),
                                map -> sortedByBankRate(map.values())
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

        // TOP3 균등 배점 (PRD: 3축 33/33/34)
        List<ProductMatchDto> governmentTop3 = generateTop3(
                govList, request, resolvedKeywords, null
        );
        List<ProductMatchDto> bankTop3 = generateTop3(
                bankList, request, resolvedKeywords, bankMaxInterestThreshold
        );

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
                .governmentTop3(governmentTop3)
                .bankTop3(bankTop3)
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
                            .productName(p.getProductName())
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
                        .sorted(tabAComparator())
                        .toList()
        ));
    }

    /**
     * 탭A 동점 규칙: 적합도 → tieBreaker(정부:기여금/은행:실수령액) → 상품명
     */
    private static Comparator<ProductMatchDto> tabAComparator() {
        return Comparator
                .comparingDouble(ProductMatchDto::totalScore).reversed()
                .thenComparing((dto) -> dto.tieBreaker() != null ? dto.tieBreaker() : 0L, Comparator.reverseOrder())
                .thenComparing(ProductMatchDto::productName, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    /**
     * ProductMatchDto에 tieBreaker 값을 설정한 새 인스턴스 반환
     */
    private static ProductMatchDto withTieBreaker(ProductMatchDto dto, Long tieBreaker) {
        return ProductMatchDto.builder()
                .productId(dto.productId())
                .productPropertyId(dto.productPropertyId())
                .productName(dto.productName())
                .providerName(dto.providerName())
                .source(dto.source())
                .totalScore(dto.totalScore())
                .benefitScore(dto.benefitScore())
                .periodScore(dto.periodScore())
                .identityScore(dto.identityScore())
                .depositScore(dto.depositScore())
                .bankCondScore(dto.bankCondScore())
                .tieBreaker(tieBreaker)
                .build();
    }

    /**
     * TOP3 균등 배점 리스트 생성 (PRD: 3축 33/33/34).
     * 상품별 베스트 property 선택 후 적합도순 상위 3개 반환.
     */
    private List<ProductMatchDto> generateTop3(
            List<EligibleProductOption> options,
            SearchRequestDto request,
            ResolvedKeywords resolvedKeywords,
            Double bankMaxInterestThreshold
    ) {
        return options.stream()
                .map(option -> matchScoreService.scoreForTop3(
                        option.product(),
                        option.property(),
                        request,
                        resolvedKeywords,
                        bankMaxInterestThreshold
                ))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                ProductMatchDto::productId,
                                Function.identity(),
                                (left, right) -> left.totalScore() >= right.totalScore() ? left : right
                        ),
                        map -> map.values().stream()
                                .sorted(Comparator.comparingDouble(ProductMatchDto::totalScore).reversed()
                                        .thenComparing(ProductMatchDto::productName, Comparator.nullsLast(Comparator.naturalOrder())))
                                .limit(3)
                                .toList()
                ));
    }

    /**
     * 탭B 정부 정렬: 기여금총액(achievableRate) → 환산수익률 → 상품명
     * (정부상품에서 achievableRate는 환산수익률로 사용됨)
     */
    private List<ProductRateDto> sortedByGovernmentRate(Collection<ProductRateDto> products) {
        return products.stream()
                .sorted(Comparator
                        .comparingDouble(ProductRateDto::achievableRate).reversed()
                        .thenComparing(ProductRateDto::productName, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    /**
     * 탭B 은행 정렬: 실수령액 → 최고금리(achievableRate) → 상품명
     */
    private List<ProductRateDto> sortedByBankRate(Collection<ProductRateDto> products) {
        return products.stream()
                .sorted(Comparator
                        .comparingLong((ProductRateDto dto) -> dto.netReturn() != null ? dto.netReturn() : 0L).reversed()
                        .thenComparingDouble(ProductRateDto::achievableRate).reversed()
                        .thenComparing(ProductRateDto::productName, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    // 세후 실수령액 기준 내림차순 정렬 (PRD 동점 규칙: 세후실수령액 → 달성가능금리 → 상품명)
    private List<ProductRateDto> sortedByNetReturn(Collection<ProductRateDto> products) {
        return products.stream()
                .sorted(Comparator
                        .comparingLong((ProductRateDto dto) -> dto.netReturn() != null ? dto.netReturn() : 0L).reversed()
                        .thenComparingDouble(ProductRateDto::achievableRate).reversed()
                        .thenComparing(ProductRateDto::productName, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    // 세후 실수령액 비교 (null-safe)
    private int compareNetReturn(ProductRateDto left, ProductRateDto right) {
        Long leftReturn = left.netReturn() != null ? left.netReturn() : 0L;
        Long rightReturn = right.netReturn() != null ? right.netReturn() : 0L;
        return Long.compare(leftReturn, rightReturn);
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
