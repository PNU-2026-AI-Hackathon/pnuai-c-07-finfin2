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
        OntongYouth ontongYouth,
        Fss fss
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
}
