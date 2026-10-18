package apptive.fin.apicollector.llm.gemini;

import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.llm.LlmProductEnrichment;
import apptive.fin.apicollector.llm.LlmProductEnrichmentRequest;
import apptive.fin.apicollector.llm.LlmProviderClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/** Gemini LLM 전송 담당. 응답 스키마 구성/파싱은 협력자에 위임한다. */
@Component
public class GeminiLlmProviderClient implements LlmProviderClient {

    private static final String PROVIDER = "GEMINI";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final CollectorProperties properties;
    private final GeminiEnrichmentSchema enrichmentSchema;
    private final GeminiResponseParser responseParser;
    // 특정 입력에서 모델이 출력을 최대치(65536)까지 폭주 생성해 타임아웃/비용 낭비가 난다.
    // 정상 enrichment 출력은 수백 토큰이라 상한을 둬 폭주를 잘라낸다.
    private final int maxOutputTokens;

    public GeminiLlmProviderClient(
            @Qualifier("geminiRestClient") RestClient restClient,
            ObjectMapper objectMapper,
            CollectorProperties properties,
            GeminiEnrichmentSchema enrichmentSchema,
            GeminiResponseParser responseParser,
            @Value("${collector.llm.max-output-tokens:2048}") int maxOutputTokens
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.enrichmentSchema = enrichmentSchema;
        this.responseParser = responseParser;
        this.maxOutputTokens = maxOutputTokens;
    }

    @Override
    public boolean supports(String provider) {
        return PROVIDER.equalsIgnoreCase(provider);
    }

    @Override
    public LlmProductEnrichment enrich(LlmProductEnrichmentRequest request) {
        byte[] requestBytes = objectMapper.writeValueAsBytes(requestBody(request));
        JsonNode response = restClient.post()
                .uri("/v1beta/interactions")
                .header("x-goog-api-key", properties.llm().apiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(requestBytes.length)
                .body(requestBytes)
                .retrieve()
                .body(JsonNode.class);

        return responseParser.parse(response);
    }

    private ObjectNode requestBody(LlmProductEnrichmentRequest request) {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", request.model());
        body.put("input", request.prompt());

        ObjectNode generationConfig = objectMapper.createObjectNode();
        Double temperature = properties.llm().temperature();
        if (temperature != null) {
            generationConfig.put("temperature", temperature);
        }
        if (maxOutputTokens > 0) {
            generationConfig.put("max_output_tokens", maxOutputTokens);
        }
        if (!generationConfig.isEmpty()) {
            body.set("generation_config", generationConfig);
        }

        ObjectNode responseFormat = objectMapper.createObjectNode();
        responseFormat.put("type", "text");
        responseFormat.put("mime_type", "application/json");
        responseFormat.set("schema", enrichmentSchema.build());
        body.set("response_format", responseFormat);

        return body;
    }
}
