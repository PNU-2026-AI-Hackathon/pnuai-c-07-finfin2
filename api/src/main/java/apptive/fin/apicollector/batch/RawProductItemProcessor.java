package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.ProductDraftFinalizer;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.enrich.ProductDraftEnricher;
import apptive.fin.apicollector.normalize.normalizer.ProductNormalizer;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RawProductItemProcessor implements ItemProcessor<ProductRaw, ProductDraft> {

    private final List<ProductNormalizer> normalizers;
    private final List<ProductDraftEnricher> enrichers;
    private final ProductDraftFinalizer finalizer;
    private volatile Map<Source, ProductNormalizer> normalizerBySource;

    @Override
    public ProductDraft process(ProductRaw item) {
        ProductNormalizer normalizer = normalizerBySource().get(item.getSource());
        if (normalizer == null) {
            throw new IllegalArgumentException("Unsupported raw source. rawId=%d, source=%s"
                    .formatted(item.getId(), item.getSource()));
        }

        ProductDraft draft = normalizer.normalize(item);
        for (ProductDraftEnricher enricher : enrichers) {
            if (enricher.supports(item.getSource())) {
                draft = enricher.enrich(item, draft);
            }
        }
        return finalizer.apply(draft);
    }

    private Map<Source, ProductNormalizer> normalizerBySource() {
        Map<Source, ProductNormalizer> result = normalizerBySource;
        if (result == null) {
            synchronized (this) {
                result = normalizerBySource;
                if (result == null) {
                    result = buildNormalizerBySource();
                    normalizerBySource = result;
                }
            }
        }

        return result;
    }

    private Map<Source, ProductNormalizer> buildNormalizerBySource() {
        Map<Source, ProductNormalizer> result = new EnumMap<>(Source.class);
        for (ProductNormalizer normalizer : normalizers) {
            result.put(normalizer.source(), normalizer);
        }
        return Map.copyOf(result);
    }

}
