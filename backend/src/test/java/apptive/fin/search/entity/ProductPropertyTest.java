package apptive.fin.search.entity;

import apptive.fin.provider.entity.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ProductPropertyTest {

    @Test
    void propertyApplyUrlTakesPriorityOverProviderApplyUrl() {
        ProductProperty property = property("https://product.example/apply", "https://provider.example/apply");

        assertThat(property.resolvedApplyUrl()).isEqualTo("https://product.example/apply");
    }

    @Test
    void missingPropertyApplyUrlFallsBackToProviderApplyUrl() {
        ProductProperty property = property(null, "https://provider.example/apply");
        assertThat(property.resolvedApplyUrl()).isEqualTo("https://provider.example/apply");

        ReflectionTestUtils.setField(property, "applyUrl", "");
        assertThat(property.resolvedApplyUrl()).isEqualTo("https://provider.example/apply");

        ReflectionTestUtils.setField(property, "applyUrl", "   ");
        assertThat(property.resolvedApplyUrl()).isEqualTo("https://provider.example/apply");
    }

    @Test
    void missingPropertyAndProviderApplyUrlsResolveToNull() {
        ProductProperty property = property(null, "   ");
        assertThat(property.resolvedApplyUrl()).isNull();

        ReflectionTestUtils.setField(property, "provider", null);
        assertThat(property.resolvedApplyUrl()).isNull();
    }

    private ProductProperty property(String propertyApplyUrl, String providerApplyUrl) {
        Provider provider = new Provider();
        ReflectionTestUtils.setField(provider, "applyUrl", providerApplyUrl);

        ProductProperty property = new ProductProperty();
        ReflectionTestUtils.setField(property, "applyUrl", propertyApplyUrl);
        ReflectionTestUtils.setField(property, "provider", provider);
        return property;
    }
}
