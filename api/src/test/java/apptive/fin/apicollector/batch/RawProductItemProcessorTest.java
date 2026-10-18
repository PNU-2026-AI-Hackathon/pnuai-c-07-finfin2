package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.ProductDraftFinalizer;
import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.enrich.ProductDraftEnricher;
import apptive.fin.apicollector.normalize.normalizer.ProductNormalizer;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawProductItemProcessorTest {

    private final ProductDraftFinalizer finalizer = new ProductDraftFinalizer();

    @Test
    void dispatchesBySource() {
        ProductDraft draft = ProductDraft.builder()
                .sourceCode("FSS")
                .normalizerVersion(1)
                .build();
        RawProductItemProcessor processor = new RawProductItemProcessor(
                List.of(new StubNormalizer(Source.FSS, draft)),
                List.of(),
                finalizer
        );

        ProductDraft result = processor.process(new ProductRaw(Source.FSS, "external", "hash", "{}", ProductType.SAVING));

        assertThat(result).isEqualTo(draft);
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
                .properties(List.of(ProductPropertyDraft.builder()
                        .keywords(List.of(
                                KeywordValueEnum.REGION_BUSAN,
                                KeywordValueEnum.BANK_CARD_USAGE
                        ))
                        .preferentialRates(List.of(PreferentialRateDraft.builder()
                                .keywordCode(KeywordValueEnum.BANK_CARD_USAGE)
                                .rate(new BigDecimal("0.10"))
                                .description("카드 우대")
                                .build()))
                        .build()))
                .build();
        RawProductItemProcessor processor = new RawProductItemProcessor(
                List.of(new StubNormalizer(Source.FSS, draft)),
                List.of(new StubEnricher(Source.FSS, enriched)),
                finalizer
        );

        ProductDraft result = processor.process(new ProductRaw(Source.FSS, "external", "hash", "{}", ProductType.SAVING));

        assertThat(result.productName()).isEqualTo("after");
        assertThat(result.properties().getFirst().keywords())
                .containsExactly(
                        KeywordValueEnum.REGION_BUSAN,
                        KeywordValueEnum.BENEFIT_EASY_CONDITION
                );
    }

    @Test
    void throwsWhenNormalizerDoesNotExist() {
        RawProductItemProcessor processor = new RawProductItemProcessor(List.of(), List.of(), finalizer);

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
