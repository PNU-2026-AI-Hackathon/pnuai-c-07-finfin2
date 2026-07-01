package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.raw.ProductRaw;
import apptive.fin.apicollector.raw.ProductRawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RawProductItemReader implements ItemReader<ProductRaw> {

    private final ProductRawRepository repository;
    private final CollectorProperties properties;
    private final Source source;

    private Iterator<ProductRaw> iterator = List.<ProductRaw>of().iterator();
    private long lastSeenId = 0L;
    private boolean exhausted = false;

    public RawProductItemReader(
            ProductRawRepository repository,
            CollectorProperties properties,
            Source source
    ) {
        this.repository = repository;
        this.properties = properties;
        this.source = source;
    }

    @Override
    public ProductRaw read() {
        if (!iterator.hasNext() && !exhausted) {
            List<ProductRaw> page = repository.findNextNeedNormalize(
                    List.of(source),
                    lastSeenId,
                    properties.normalizerVersion(),
                    llmEnrichmentEnabled(),
                    llmProvider(),
                    llmModel(),
                    llmPromptVersion(),
                    llmSchemaVersion(),
                    PageRequest.of(0, properties.readerPageSize())
            );

            if (page.isEmpty()) {
                exhausted = true;
                return null;
            }

            iterator = page.iterator();
        }

        if (!iterator.hasNext()) {
            return null;
        }

        ProductRaw item = iterator.next();
        lastSeenId = item.getId();

        return item;
    }

    private boolean llmEnrichmentEnabled() {
        return properties.llm() != null
                && properties.llm().enabled()
                && properties.llm().apiKey() != null
                && !properties.llm().apiKey().isBlank();
    }

    private String llmProvider() {
        return properties.llm() == null ? "" : properties.llm().provider();
    }

    private String llmModel() {
        return properties.llm() == null ? "" : properties.llm().model();
    }

    private int llmPromptVersion() {
        return properties.llm() == null ? 0 : properties.llm().promptVersion();
    }

    private int llmSchemaVersion() {
        return properties.llm() == null ? 0 : properties.llm().schemaVersion();
    }
}
