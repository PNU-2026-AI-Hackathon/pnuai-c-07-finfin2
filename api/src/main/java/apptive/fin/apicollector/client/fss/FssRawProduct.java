package apptive.fin.apicollector.client.fss;

import tools.jackson.databind.JsonNode;

public record FssRawProduct(
        FssProductType productType,
        FssFinancialGroup financialGroup,
        String externalId,
        JsonNode raw
) {
}
