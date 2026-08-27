package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
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
        assertThat(draft.properties()).isEmpty();
    }
}
