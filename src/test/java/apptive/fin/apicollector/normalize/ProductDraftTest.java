package apptive.fin.apicollector.normalize;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDraftTest {

    @Test
    void appliesDefaults() {
        ProductDraft draft = ProductDraft.builder()
                .rawId(1L)
                .normalizerVersion(1)
                .build();

        assertThat(draft.classification()).isEqualTo(ProductClassification.FINANCIAL_PRODUCT);
        assertThat(draft.shouldSaveProduct()).isTrue();
        assertThat(draft.options()).isEmpty();
        assertThat(draft.keywords()).isEmpty();
        assertThat(draft.requiresHomeless()).isFalse();
        assertThat(draft.requiresHouseholder()).isFalse();
    }
}
