package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.SearchErrorCode;
import apptive.fin.search.dto.BankDetailDto;
import apptive.fin.search.dto.GovernmentDetailDto;
import apptive.fin.search.dto.PreferentialConditionDto;
import apptive.fin.search.dto.ProductDetailRequestDto;
import apptive.fin.search.dto.ProductDetailResponseDto;
import apptive.fin.search.dto.RateTableRowDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductType;
import apptive.fin.search.repository.ProductRepository;
import apptive.fin.search.util.ProductAvailability;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDetailService {

    private static final String ONTONG = "ONTONG";
    private static final String METRICS_LOCK_MESSAGE =
            "내가 받을 수 있는 금리·기여금 환산 수익률·예상 만기 기여금 총액은 로그인 후 단계2 정보를 입력하면 확인할 수 있어요.";

    private final ProductRepository productRepository;
    private final ResolveKeywordService resolveKeywordService;
    private final RateCalculatorService rateCalculatorService;
    private final MatchScoreService matchScoreService;

    public ProductDetailResponseDto getProductDetail(
            Long productId,
            ProductDetailRequestDto request,
            AuthUserDetails userDetails
    ) {
        ProductDetailRequestDto req = request != null
                ? request
                : new ProductDetailRequestDto(null, null, null);

        // properties/keywords/preferentialRates 모두 @BatchSize라 lazy 접근으로 충분 (커스텀 fetch 쿼리 불필요)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(SearchErrorCode.PRODUCT_NOT_FOUND));

        List<apptive.fin.search.dto.OptionRequestDto> options =
                req.options() != null ? req.options() : List.of();
        ResolvedKeywords keywords = resolveKeywordService.resolveKeywords(options);
        SearchRequestDto calcRequest = new SearchRequestDto(options, req.detailedOptions());

        boolean subscription = product.getType() == ProductType.SUBSCRIPTION;
        boolean government = ONTONG.equals(product.getSource().getCode()) && !subscription;
        boolean metricsLocked = userDetails == null; // 비로그인 시 수익 지표 잠금(로그인 게이트)
        List<ProductProperty> joinableProperties = product.getProperties().stream()
                .filter(ProductProperty::isJoinable)
                .toList();
        boolean archivedProduct = joinableProperties.isEmpty();
        List<ProductProperty> detailCandidates = archivedProduct
                ? product.getProperties()
                : joinableProperties;
        ProductProperty selected = selectProperty(
                product,
                detailCandidates,
                archivedProduct,
                req.productPropertyId(),
                calcRequest,
                keywords,
                metricsLocked
        );
        List<ProductProperty> detailProperties = req.productPropertyId() != null
                && selected != null
                && !selected.isJoinable()
                ? List.of(selected)
                : detailCandidates;
        boolean showMetrics = !metricsLocked && !subscription && selected != null;

        // 적합도(리스트 탭A totalScore)는 잠금과 무관 — property/옵션이 있으면 계산.
        // includeTx는 리스트(SearchService.isTabBEnabled)와 동일하게 맞춰 값 일관성 보장.
        Double matchScore = null;
        if (selected != null && !options.isEmpty()) {
            boolean includeTx = userDetails != null && calcRequest.hasTransactionHistory();
            matchScore = matchScoreService
                    .score(product, selected, calcRequest, keywords, includeTx)
                    .totalScore();
        }

        GovernmentDetailDto governmentDetail = null;
        BankDetailDto bankDetail = null;
        List<RateTableRowDto> rateTable = null;
        if (showMetrics) {
            if (government) {
                governmentDetail = rateCalculatorService.governmentDetail(selected, calcRequest);
            } else {
                bankDetail = rateCalculatorService.bankDetail(selected, calcRequest, keywords);
            }
            rateTable = buildRateTable(detailProperties, government);
        }

        return ProductDetailResponseDto.builder()
                .productId(product.getId())
                .productType(product.getType())
                .sourceCode(product.getSource().getCode())
                .productName(product.getProductName())
                .providerName(providerName(selected))
                .keywords(distinctKeywords(product, detailProperties))
                .content(product.getContent())
                .contentSummary(product.getContentSummary())
                .saveTrms(saveTrms(detailProperties))
                .matchScore(matchScore)
                .minAge(selected != null ? selected.getMinAge() : null)
                .maxAge(selected != null ? selected.getMaxAge() : null)
                .minMonthlyLimit(selected != null ? selected.getMinMonthlyLimit() : null)
                .maxMonthlyLimit(selected != null ? selected.getMaxMonthlyLimit() : null)
                .requiresHomeless(selected != null ? selected.getRequiresHomeless() : null)
                .requiresHouseholder(selected != null ? selected.getRequiresHouseholder() : null)
                .joinMethod(product.getJoinMethod())
                .eligibilityText(product.getEligibilityText())
                .cautionText(product.getCautionText())
                .recruitmentPeriod(product.getRecruitmentPeriod())
                .reserveType(selected != null ? selected.getReserveType() : null)
                .reserveTypeName(selected != null && selected.getReserveType() != null
                        ? selected.getReserveType().getLabel() : null)
                .applyStatus(ProductAvailability.applyStatus(selected))
                .applyUrl(ProductAvailability.applyUrl(selected))
                .metricsLocked(metricsLocked)
                .lockMessage(metricsLocked ? METRICS_LOCK_MESSAGE : null)
                .government(governmentDetail)
                .bank(bankDetail)
                .rateTable(rateTable)
                .build();
    }

    private ProductProperty selectProperty(
            Product product,
            List<ProductProperty> detailCandidates,
            boolean archivedProduct,
            Long productPropertyId,
            SearchRequestDto request,
            ResolvedKeywords keywords,
            boolean metricsLocked
    ) {
        if (productPropertyId != null) {
            return product.getProperties().stream()
                    .filter(property -> productPropertyId.equals(property.getId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(SearchErrorCode.PRODUCT_NOT_FOUND));
        }
        if (metricsLocked || archivedProduct) {
            return detailCandidates.stream()
                    .sorted(publishedRateOrder())
                    .findFirst()
                    .orElse(null);
        }
        // 직접 진입 등으로 productPropertyId가 없으면 대표 property 폴백 (리스트 eligibility를 안 거쳐 값이 다를 수 있음)
        return rateCalculatorService.selectRepresentativeProperty(product, detailCandidates, request, keywords);
    }

    private Comparator<ProductProperty> publishedRateOrder() {
        Comparator<BigDecimal> rateOrder = Comparator.nullsLast(Comparator.reverseOrder());
        return Comparator
                .comparing(ProductProperty::getMaxRate, rateOrder)
                .thenComparing(ProductProperty::getBaseRate, rateOrder)
                .thenComparing(ProductProperty::getId, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    // 정부형은 기본/최고금리 공시 상품만 금리표 노출, 은행형은 전체 옵션.
    private List<RateTableRowDto> buildRateTable(List<ProductProperty> properties, boolean government) {
        return properties.stream()
                .filter(property -> !government || property.getBaseRate() != null || property.getMaxRate() != null)
                .map(property -> new RateTableRowDto(
                        property.getSaveTrm(),
                        property.getBaseRate() != null ? property.getBaseRate().doubleValue() : null,
                        property.getMaxRate() != null ? property.getMaxRate().doubleValue() : null,
                        property.getIntrRateType(),
                        property.getPreferentialRates().stream()
                                .map(rate -> new PreferentialConditionDto(
                                        rate.getKeywordCode(),
                                        rate.getRate() != null ? rate.getRate().doubleValue() : null,
                                        rate.getDescription()))
                                .toList()))
                .toList();
    }

    private List<KeywordValueEnum> distinctKeywords(Product product, List<ProductProperty> properties) {
        // 최고이율은 정적 태그가 아니라 검색과 동일 철학으로 동적 판정한다(PRD A-2).
        // 영속된 레거시 BENEFIT_MAX_INTEREST(재정규화 전 데이터·구 정부상품 태그 등)는 먼저 걸러내고,
        // 동적 기준(전체 은행상품 상위 30%)을 충족할 때만 다시 붙여 배지가 오로지 동적 결과만 반영하게 한다.
        List<KeywordValueEnum> keywords = properties.stream()
                .flatMap(property -> property.keywordCodes().stream())
                .filter(Objects::nonNull)
                .filter(code -> code != KeywordValueEnum.BENEFIT_MAX_INTEREST)
                .distinct()
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        if (properties.stream().anyMatch(ProductProperty::isJoinable) && isTopRateBank(product)) {
            keywords.add(KeywordValueEnum.BENEFIT_MAX_INTEREST);
        }
        return keywords;
    }

    // 은행 상품이면서 그 상품의 (가입가능 속성 중) 최고 maxRate가 전체 은행상품 상위 30% 컷 이상이면 true.
    // 컷 계산은 검색과 동일한 SearchService.computeTopRateThreshold를 재사용(읽기 전용, DB 미기록).
    // 컷 모집단·상품 자체 금리 모두 joinable 속성만 사용해 검색(eligible 기준)과 일관성을 맞춘다.
    private boolean isTopRateBank(Product product) {
        if (!product.isBank()) {
            return false;
        }
        Double productMaxRate = product.getProperties().stream()
                .filter(ProductProperty::isJoinable)
                .map(ProductProperty::getMaxRate)
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .max(Double::compareTo)
                .orElse(null);
        if (productMaxRate == null) {
            return false;
        }
        Double threshold = SearchService.computeTopRateThreshold(
                productRepository.findJoinableMaxRatesBySourceCode(ProductSource.BANK_CODE).stream()
                        .map(BigDecimal::doubleValue)
                        .toList());
        return threshold != null && productMaxRate >= threshold;
    }

    private List<Integer> saveTrms(List<ProductProperty> properties) {
        return properties.stream()
                .map(ProductProperty::getSaveTrm)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    private String providerName(ProductProperty property) {
        return property != null && property.getProvider() != null
                ? property.getProvider().getName()
                : null;
    }
}
