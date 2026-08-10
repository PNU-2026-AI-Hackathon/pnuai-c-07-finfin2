package apptive.fin.apicollector.bankurl;

import apptive.fin.apicollector.product.ProductType;

public record BankProductUrlTarget(
        Long productId,
        String productCode,
        String productName,
        ProductType productType,
        String providerCode,
        String providerName
) {
}
