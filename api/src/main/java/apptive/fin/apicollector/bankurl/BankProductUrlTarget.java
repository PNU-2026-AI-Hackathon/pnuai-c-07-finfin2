package apptive.fin.apicollector.bankurl;

public record BankProductUrlTarget(
        Long productId,
        String productCode,
        String productName,
        String productType,
        String providerCode,
        String providerName
) {
}
