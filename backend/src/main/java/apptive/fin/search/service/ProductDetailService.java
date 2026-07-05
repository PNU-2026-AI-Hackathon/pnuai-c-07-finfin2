package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.ProductType;
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
import apptive.fin.search.entity.ProductKeyword;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        ProductProperty selected = selectProperty(product, req.productPropertyId(), calcRequest, keywords);

        boolean subscription = product.getType() == ProductType.SUBSCRIPTION;
        boolean government = ONTONG.equals(product.getSource().getCode()) && !subscription;
        boolean metricsLocked = userDetails == null; // 비로그인 시 수익 지표 잠금(로그인 게이트)
        boolean showMetrics = !metricsLocked && !subscription && selected != null;

        GovernmentDetailDto governmentDetail = null;
        BankDetailDto bankDetail = null;
        List<RateTableRowDto> rateTable = null;
        if (showMetrics) {
            if (government) {
                governmentDetail = rateCalculatorService.governmentDetail(selected, calcRequest);
            } else {
                bankDetail = rateCalculatorService.bankDetail(selected, calcRequest, keywords);
            }
            rateTable = buildRateTable(product, government);
        }

        return ProductDetailResponseDto.builder()
                .productId(product.getId())
                .productType(product.getType())
                .sourceCode(product.getSource().getCode())
                .productName(product.getProductName())
                .providerName(providerName(selected))
                .keywords(distinctKeywords(product))
                .content(product.getContent())
                .contentSummary(product.getContentSummary())
                .saveTrms(saveTrms(product))
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
                .installmentType(selected != null ? selected.getInstallmentType() : null)
                .applyUrl(resolveApplyUrl(product, selected))
                .metricsLocked(metricsLocked)
                .lockMessage(metricsLocked ? METRICS_LOCK_MESSAGE : null)
                .government(governmentDetail)
                .bank(bankDetail)
                .rateTable(rateTable)
                .build();
    }

    private ProductProperty selectProperty(
            Product product,
            Long productPropertyId,
            SearchRequestDto request,
            ResolvedKeywords keywords
    ) {
        if (productPropertyId != null) {
            return product.getProperties().stream()
                    .filter(property -> productPropertyId.equals(property.getId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(SearchErrorCode.PRODUCT_NOT_FOUND));
        }
        // 직접 진입 등으로 productPropertyId가 없으면 대표 property 폴백 (리스트 eligibility를 안 거쳐 값이 다를 수 있음)
        return rateCalculatorService.selectRepresentativeProperty(product, request, keywords);
    }

    // 정부형은 기본/최고금리 공시 상품만 금리표 노출, 은행형은 전체 옵션.
    private List<RateTableRowDto> buildRateTable(Product product, boolean government) {
        return product.getProperties().stream()
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

    // ONTONG → property.applyUrl, FSS → provider 대표 URL, 없으면 null(비활성).
    private String resolveApplyUrl(Product product, ProductProperty selected) {
        if (ONTONG.equals(product.getSource().getCode())) {
            if (selected != null && selected.getApplyUrl() != null) {
                return selected.getApplyUrl();
            }
            return product.getProperties().stream()
                    .map(ProductProperty::getApplyUrl)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return selected != null && selected.getProvider() != null
                ? selected.getProvider().getApplyUrl()
                : null;
    }

    private List<KeywordValueEnum> distinctKeywords(Product product) {
        return product.getProperties().stream()
                .flatMap(property -> property.getKeywords().stream())
                .map(ProductKeyword::getKeywordCode)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private List<Integer> saveTrms(Product product) {
        return product.getProperties().stream()
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
