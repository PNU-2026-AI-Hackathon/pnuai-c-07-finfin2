package apptive.fin.apicollector.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final CollectorProperties collectorProperties;

    @Bean
    public RestClient ontongYouthRestClient() {
        return RestClient.builder()
                .baseUrl(collectorProperties.ontongYouth().baseUrl())
                .build();
    }

    @Bean
    public RestClient fssRestClient() {
        return RestClient.builder()
                .baseUrl(collectorProperties.fss().baseUrl())
                .build();
    }
}
