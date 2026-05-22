package apptive.fin.apicollector.normalize.dto;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.ProductClassification;
import apptive.fin.apicollector.product.ProductType;
import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ProductDraft(
        Long rawId,
        Source rawSource,
        int normalizerVersion,
        ProductClassification classification,
        Boolean saveProduct,
        String sourceCode,
        ProductType type,
        String productCode,
        String productName,
        String content,
        List<ProductPropertyDraft> properties
) {
    public ProductDraft {
        classification = classification == null ? ProductClassification.FINANCIAL_PRODUCT : classification;
        saveProduct = saveProduct == null ? Boolean.TRUE : saveProduct;
        properties = properties == null ? List.of() : List.copyOf(properties);
    }

    public boolean shouldSaveProduct() {
        return saveProduct;
    }
}
