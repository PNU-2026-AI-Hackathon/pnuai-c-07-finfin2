package apptive.fin.apicollector.config;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private static final HttpClientSettings FETCH_SETTINGS =
            HttpClientSettings.defaults()
                    .withConnectTimeout(Duration.ofSeconds(5))
                    .withReadTimeout(Duration.ofSeconds(30));

    // LLM 응답은 대형 프롬프트 + structured output 생성 시간이 길어 read 여유를 둔다.
    private static final HttpClientSettings LLM_SETTINGS =
            HttpClientSettings.defaults()
                    .withConnectTimeout(Duration.ofSeconds(5))
                    .withReadTimeout(Duration.ofSeconds(60));

    private final CollectorProperties collectorProperties;

    @Bean
    public RestClient ontongYouthRestClient() {
        return RestClient.builder()
                .baseUrl(collectorProperties.ontongYouth().baseUrl())
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(FETCH_SETTINGS))
                .build();
    }

    @Bean
    public RestClient fssRestClient() {
        return RestClient.builder()
                .baseUrl(collectorProperties.fss().baseUrl())
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(FETCH_SETTINGS))
                .build();
    }

    @Bean
    public RestClient geminiRestClient() {
        return RestClient.builder()
                .baseUrl(collectorProperties.llm().baseUrl())
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(LLM_SETTINGS))
                .build();
    }
}
