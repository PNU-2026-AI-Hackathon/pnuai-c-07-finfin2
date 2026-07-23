package apptive.fin.apicollector.client.fss;


import apptive.fin.apicollector.config.CollectorProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FssClient {

    private static final String DEPOSIT_PATH = "/finlifeapi/depositProductsSearch.json";
    private static final String SAVING_PATH = "/finlifeapi/savingProductsSearch.json";


    private final CollectorProperties properties;
    private final RestClient fssRestClient;
    private final ObjectMapper objectMapper;
    private final FssOptionOrdering fssOptionOrdering;

    public FssClient(
            CollectorProperties properties,
            @Qualifier("fssRestClient") RestClient fssRestClient,
            ObjectMapper objectMapper,
            FssOptionOrdering fssOptionOrdering
    ) {
        this.properties = properties;
        this.fssRestClient = fssRestClient;
        this.objectMapper = objectMapper;
        this.fssOptionOrdering = fssOptionOrdering;
    }

    public List<FssRawProduct> fetchAll() {
        List<FssRawProduct> result = new ArrayList<>();
        Map<String, FssProductType> paths = Map.of(
                DEPOSIT_PATH, FssProductType.DEPOSIT,
                SAVING_PATH, FssProductType.SAVING
        );

        for (String path : paths.keySet()) {
            for (FssFinancialGroup group : FssFinancialGroup.values()) {
                result.addAll(fetchProducts(
                        paths.get(path),
                        group,
                        path
                ));
            }
        }

        return result;
    }


    private List<FssRawProduct> fetchProducts(
            FssProductType productType,
            FssFinancialGroup group,
            String path
    ){
        List<FssRawProduct> result = new ArrayList<>();

        int page = 1;
        int pageSize = properties.fss().pageSize();

        while (true) {
            JsonNode response = request(path, group, page);
            List<FssRawProduct> items = extractProducts(productType, group, response);

            if (items.isEmpty()) {
                break;
            }

            result.addAll(items);

            if (items.size() < pageSize) {
                break;
            }

            page++;
        }

        return result;
    }

    private JsonNode request(
            String path,
            FssFinancialGroup group,
            int page
    ) {
        return fssRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("auth", properties.fss().apiKey())
                        .queryParam("topFinGrpNo", group.getCode())
                        .queryParam("pageNo", page)
                        .build()
                )
                .retrieve()
                .body(JsonNode.class);
    }

    private List<FssRawProduct> extractProducts(
            FssProductType productType,
            FssFinancialGroup group,
            JsonNode response
    ) {
        JsonNode resultNode = response.path("result");
        JsonNode baseList = resultNode.path("baseList");
        JsonNode optionList = resultNode.path("optionList");

        if (!baseList.isArray()) {
            return List.of();
        }

        Map<FssProductKey, List<JsonNode>> optionsByProduct =
                groupOptionsByProduct(optionList);

        List<FssRawProduct> result = new ArrayList<>();

        for (JsonNode base : baseList) {
            String externalId = buildExternalId(productType, group, base);

            if (externalId == null) {
                continue;
            }

            ObjectNode raw = objectMapper.createObjectNode();
            raw.put("source", "FSS");
            raw.put("productType", productType.name());
            raw.put("financialGroupCode", group.getCode());
            raw.put("financialGroupName", group.getDescription());
            raw.set("base", base);

            ArrayNode optionArray = objectMapper.createArrayNode();
            List<JsonNode> options = optionsByProduct.getOrDefault(
                    FssProductKey.from(base),
                    List.of()
            );
            fssOptionOrdering.sort(options).forEach(optionArray::add);

            raw.set("options", optionArray);

            result.add(new FssRawProduct(
                    productType,
                    group,
                    externalId,
                    raw
            ));
        }

        return result;
    }

    private String buildExternalId(
            FssProductType productType,
            FssFinancialGroup group,
            JsonNode base
    ) {
        String companyCode = base.path("fin_co_no").asString(null);
        String productCode = base.path("fin_prdt_cd").asString(null);

        if (productCode == null || productCode.isBlank()) {
            return null;
        }

        if (companyCode == null || companyCode.isBlank()) {
            return "FSS:%s:%s:%s".formatted(
                    productType.name(),
                    group.getCode(),
                    productCode
            );
        }

        return "FSS:%s:%s:%s:%s".formatted(
                productType.name(),
                group.getCode(),
                companyCode,
                productCode
        );
    }

    private Map<FssProductKey, List<JsonNode>> groupOptionsByProduct(JsonNode optionList) {
        Map<FssProductKey, List<JsonNode>> result = new HashMap<>();

        if (optionList == null || optionList.isMissingNode() || optionList.isNull() || !optionList.isArray()) {
            return result;
        }

        for (JsonNode option : optionList) {
            FssProductKey key = FssProductKey.from(option);

            result.computeIfAbsent(key, ignored -> new ArrayList<>())
                    .add(option);
        }

        return result;
    }
}
