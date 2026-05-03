package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.ProductDraft;
import apptive.fin.apicollector.normalize.ProductNormalizer;
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
        RawProductItemProcessor processor = new RawProductItemProcessor(List.of(new StubNormalizer(Source.FSS, draft)));

        ProductDraft result = processor.process(new ProductRaw(Source.FSS, "external", "hash", "{}"));

        assertThat(result).isSameAs(draft);
    }

    @Test
    void throwsWhenNormalizerDoesNotExist() {
        RawProductItemProcessor processor = new RawProductItemProcessor(List.of());

        assertThatThrownBy(() -> processor.process(new ProductRaw(Source.FSS, "external", "hash", "{}")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported raw source");
    }

    private record StubNormalizer(Source source, ProductDraft draft) implements ProductNormalizer {

        @Override
        public ProductDraft normalize(ProductRaw rawProduct) {
            return draft;
        }
    }
}
