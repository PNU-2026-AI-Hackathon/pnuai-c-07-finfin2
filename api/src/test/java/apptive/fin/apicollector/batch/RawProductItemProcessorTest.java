package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.enrich.ProductDraftEnricher;
import apptive.fin.apicollector.normalize.normalizer.ProductNormalizer;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawProductItemProcessorTest {

    @Test
    void dispatchesBySource() {
        ProductDraft draft = ProductDraft.builder()
                .sourceCode("FSS")
                .normalizerVersion(1)
                .build();
        RawProductItemProcessor processor = new RawProductItemProcessor(List.of(new StubNormalizer(Source.FSS, draft)), List.of());

        ProductDraft result = processor.process(new ProductRaw(Source.FSS, "external", "hash", "{}", ProductType.SAVING));

        assertThat(result).isSameAs(draft);
    }

    @Test
    void appliesSupportedEnricher() {
        ProductDraft draft = ProductDraft.builder()
                .sourceCode("FSS")
                .normalizerVersion(1)
                .productName("before")
                .build();
        ProductDraft enriched = draft.toBuilder()
                .productName("after")
                .build();
        RawProductItemProcessor processor = new RawProductItemProcessor(
                List.of(new StubNormalizer(Source.FSS, draft)),
                List.of(new StubEnricher(Source.FSS, enriched))
        );

        ProductDraft result = processor.process(new ProductRaw(Source.FSS, "external", "hash", "{}", ProductType.SAVING));

        assertThat(result).isSameAs(enriched);
    }

    @Test
    void throwsWhenNormalizerDoesNotExist() {
        RawProductItemProcessor processor = new RawProductItemProcessor(List.of(), List.of());

        assertThatThrownBy(() -> processor.process(new ProductRaw(Source.FSS, "external", "hash", "{}", ProductType.SAVING)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported raw source");
    }

    private record StubNormalizer(Source source, ProductDraft draft) implements ProductNormalizer {

        @Override
        public ProductDraft normalize(ProductRaw rawProduct) {
            return draft;
        }
    }

    private record StubEnricher(Source source, ProductDraft draft) implements ProductDraftEnricher {

        @Override
        public boolean supports(Source source) {
            return this.source == source;
        }

        @Override
        public ProductDraft enrich(ProductRaw rawProduct, ProductDraft draft) {
            return this.draft;
        }
    }
}
