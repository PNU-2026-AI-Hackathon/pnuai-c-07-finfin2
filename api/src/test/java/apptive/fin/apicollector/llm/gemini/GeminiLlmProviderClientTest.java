package apptive.fin.apicollector.llm.gemini;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.llm.LlmProductEnrichment;
import apptive.fin.apicollector.llm.LlmProductEnrichmentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiLlmProviderClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void sendsUtf8RequestWithFixedContentLength() throws Exception {
        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl("http://localhost");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        GeminiResponseParser responseParser = mock(GeminiResponseParser.class);
        LlmProductEnrichment expected = emptyEnrichment();
        when(responseParser.parse(any(JsonNode.class))).thenReturn(expected);

        GeminiLlmProviderClient client = new GeminiLlmProviderClient(
                restClientBuilder.build(),
                objectMapper,
                properties(0.1),
                new GeminiEnrichmentSchema(objectMapper),
                responseParser
        );

        server.expect(requestTo("http://localhost/v1beta/interactions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("x-goog-api-key", "test-key"))
                .andExpect(request -> {
                    byte[] body = ((MockClientHttpRequest) request).getBodyAsBytes();
                    JsonNode json = objectMapper.readTree(body);

                    assertThat(request.getHeaders().getContentType())
                            .isEqualTo(MediaType.APPLICATION_JSON);
                    assertThat(request.getHeaders().getContentLength())
                            .isEqualTo(body.length);
                    assertThat(json.path("model").asText()).isEqualTo("gemini-test");
                    assertThat(json.path("input").asText()).isEqualTo("한국어 가입 조건을 요약해줘");
                    assertThat(json.path("generation_config").path("temperature").asDouble())
                            .isEqualTo(0.1);
                    assertThat(json.path("response_format").path("type").asText())
                            .isEqualTo("text");
                    assertThat(json.path("response_format").path("schema").isObject())
                            .isTrue();
                })
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        LlmProductEnrichment result = client.enrich(new LlmProductEnrichmentRequest(
                "gemini-test",
                "한국어 가입 조건을 요약해줘",
                1
        ));

        assertThat(result).isSameAs(expected);
        server.verify();
    }

    @Test
    void omitsGenerationConfigWhenTemperatureIsNotConfigured() throws Exception {
        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl("http://localhost");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        GeminiResponseParser responseParser = mock(GeminiResponseParser.class);
        LlmProductEnrichment expected = emptyEnrichment();
        when(responseParser.parse(any(JsonNode.class))).thenReturn(expected);

        GeminiLlmProviderClient client = new GeminiLlmProviderClient(
                restClientBuilder.build(),
                objectMapper,
                properties(null),
                new GeminiEnrichmentSchema(objectMapper),
                responseParser
        );

        server.expect(requestTo("http://localhost/v1beta/interactions"))
                .andExpect(request -> {
                    JsonNode json = objectMapper.readTree(((MockClientHttpRequest) request).getBodyAsBytes());
                    assertThat(json.has("generation_config")).isFalse();
                })
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        LlmProductEnrichment result = client.enrich(new LlmProductEnrichmentRequest(
                "gemini-test",
                "가입 조건을 요약해줘",
                1
        ));

        assertThat(result).isSameAs(expected);
        server.verify();
    }

    private CollectorProperties properties(Double temperature) {
        return new CollectorProperties(
                true,
                Source.FSS,
                Mode.NORMALIZE_ONLY,
                28,
                100,
                7,
                new CollectorProperties.OntongYouth("http://localhost", "key", 100),
                new CollectorProperties.Fss("http://localhost", "key", 100),
                new CollectorProperties.Llm(
                        true,
                        "GEMINI",
                        "gemini-test",
                        1,
                        1,
                        10,
                        3,
                        temperature,
                        "http://localhost",
                        "test-key"
                )
        );
    }

    private LlmProductEnrichment emptyEnrichment() {
        return new LlmProductEnrichment(
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null
        );
    }
}
