package apptive.fin.apicollector.client.fss;

import tools.jackson.databind.JsonNode;

public record FssProductKey(
        String dclsMonth,
        String finCoNo,
        String finPrdtCd
) {
    static FssProductKey from(JsonNode node) {
        return new FssProductKey(
                node.path("dcls_month").asString(),
                node.path("fin_co_no").asString(),
                node.path("fin_prdt_cd").asString()
        );
    }

}
