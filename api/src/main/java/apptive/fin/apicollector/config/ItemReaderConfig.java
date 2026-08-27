package apptive.fin.apicollector.config;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.batch.RawProductItemReader;
import apptive.fin.apicollector.raw.ProductRawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ItemReaderConfig {
    @Bean
    @StepScope
    public RawProductItemReader fssRawProductItemReader(
            ProductRawRepository repository,
            CollectorProperties properties
    ) {
        return new RawProductItemReader(
                repository,
                properties,
                Source.FSS
        );
    }

    @Bean
    @StepScope
    public RawProductItemReader ontongRawProductItemReader(
            ProductRawRepository repository,
            CollectorProperties properties
    ) {
        return new RawProductItemReader(
                repository,
                properties,
                Source.ONTONG
        );
    }
}
