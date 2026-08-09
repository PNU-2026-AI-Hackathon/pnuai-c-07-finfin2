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
import apptive.fin.search.enums.ProductType;
import apptive.fin.search.repository.ProductRepository;
import apptive.fin.search.util.ProductAvailability;
import apptive.fin.search.dto.OptionRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final ProductDisplayKeywordService productDisplayKeywordService;

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

        List<OptionRequestDto> options =
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
        Double bankMaxInterestThreshold = product.isBank()
                ? bankMaxInterestThreshold()
                : null;

        return buildProductDetail(
                product,
                calcRequest,
                keywords,
                userDetails,
                selected,
                detailProperties,
                government,
                subscription,
                bankMaxInterestThreshold
        );
    }

    private ProductDetailResponseDto buildProductDetail(
            Product product,
            SearchRequestDto calcRequest,
            ResolvedKeywords keywords,
            AuthUserDetails userDetails,
            ProductProperty selected,
            List<ProductProperty> detailProperties,
            boolean government,
            boolean subscription,
            Double bankMaxInterestThreshold
    ) {
        boolean metricsLocked = userDetails == null;
        boolean showMetrics = !metricsLocked && !subscription && selected != null;

        // 적합도(리스트 탭A totalScore)는 잠금과 무관 — property/옵션이 있으면 계산.
        // includeTx는 리스트(SearchService.isTabBEnabled)와 동일하게 맞춰 값 일관성 보장.
        Double matchScore = null;
        if (selected != null && !calcRequest.options().isEmpty()) {
            boolean includeTx = userDetails != null && calcRequest.hasTransactionHistory();
            // 임계 금리도 리스트와 같은 값을 넘긴다 — 빠뜨리면 #최고이율 판정이 정적 태그 폴백으로
            // 떨어져서 같은 응답 안에서 카드 totalScore와 상세 matchScore가 갈린다.
            matchScore = matchScoreService
                    .score(product, selected, calcRequest, keywords, includeTx, bankMaxInterestThreshold)
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
                .productPropertyId(selected != null ? selected.getId() : null)
                .productType(product.getType())
                .sourceCode(product.getSource().getCode())
                .productName(product.getProductName())
                .providerName(providerName(selected))
                .keywords(productDisplayKeywordService.resolve(
                        product,
                        detailProperties,
                        bankMaxInterestThreshold
                ))
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

    private Double bankMaxInterestThreshold() {
        return SearchService.computeTopRateThreshold(
                productRepository.findJoinableMaxRatesBySourceCode(ProductSource.BANK_CODE).stream()
                        .map(BigDecimal::doubleValue)
                        .toList());
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
