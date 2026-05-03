package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductDraft(
        Long rawId,
        Source rawSource,
        int normalizerVersion,
        ProductClassification classification,
        Boolean saveProduct,
        String sourceCode,
        String providerCode,
        String providerName,
        ProductType type,
        String productCode,
        String productName,
        String content,
        BigDecimal baseRate,
        BigDecimal maxRate,
        Long minMonthlyLimit,
        Long maxMonthlyLimit,
        Integer minAge,
        Integer maxAge,
        Long earnMaxAmt,
        Integer earnPercent,
        Integer minTenureMonths,
        Boolean requiresHomeless,
        Boolean requiresHouseholder,
        String applyUrl,
        List<ProductOptionDraft> options,
        List<KeywordValueEnum> keywords
) {
    public ProductDraft {
        classification = classification == null ? ProductClassification.FINANCIAL_PRODUCT : classification;
        saveProduct = saveProduct == null ? Boolean.TRUE : saveProduct;
        options = options == null ? List.of() : List.copyOf(options);
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
        requiresHomeless = requiresHomeless != null && requiresHomeless;
        requiresHouseholder = requiresHouseholder != null && requiresHouseholder;
    }

    public boolean shouldSaveProduct() {
        return saveProduct;
    }
}
