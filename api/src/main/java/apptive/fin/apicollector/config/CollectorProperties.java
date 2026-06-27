package apptive.fin.apicollector.config;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@ConfigurationProperties(prefix = "collector")
public record CollectorProperties(
        boolean enabled,
        Source source,
        Mode mode,
        int normalizerVersion,
        int readerPageSize,
        int unseenDisablePeriod,
        OntongYouth ontongYouth,
        Fss fss,
        Llm llm
) {

    public record OntongYouth(
            String baseUrl,
            String apiKey,
            int pageSize
    ) {}

    public record Fss(
            String baseUrl,
            String apiKey,
            int pageSize
    ) {}

    public record Llm(
            boolean enabled,
            String provider,
            String model,
            int promptVersion,
            int schemaVersion,
            int chunkSize,
            int maxConcurrency,
            Double temperature,
            String baseUrl,
            String apiKey
    ) {}
}
