package apptive.fin.apicollector.client;

import apptive.fin.apicollector.config.CollectorProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OntongYouthClient {

    private final CollectorProperties properties;
    private final RestClient ontongYouthRestClient;

    public List<JsonNode> fetchAll() {
        RestClient restClient = RestClient.builder()
                .baseUrl(properties.ontongYouth().baseUrl())
                .build();

        List<JsonNode> result = new ArrayList<>();

        int page = 1;
        int pageSize = properties.ontongYouth().pageSize();

        while (true) {
            JsonNode response = fetchPage(restClient, page, pageSize);
            List<JsonNode> items = extractItems(response);

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

    private JsonNode fetchPage(RestClient restClient, int page, int pageSize) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/go/ythip/getPlcy")
                        .queryParam("apiKeyNm", properties.ontongYouth().apiKey())
                        .queryParam("pageNum", page)
                        .queryParam("pageSize", pageSize)
                        .queryParam("rtnType", "json")
                        .build()
                )
                .retrieve()
                .body(JsonNode.class);
    }

    private List<JsonNode> extractItems(JsonNode response) {
        JsonNode items = response
                .path("result")
                .path("youthPolicyList");

        if (!items.isArray()) {
            return List.of();
        }

        List<JsonNode> result = new ArrayList<>();
        items.forEach(result::add);
        return result;
    }
}