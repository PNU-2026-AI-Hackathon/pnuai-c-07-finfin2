package apptive.fin.search.entity;

import apptive.fin.provider.entity.Provider;
import apptive.fin.search.KeywordValueEnum;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void hasKeywordReflectsTaggedKeywordsOnly() {
        ProductProperty property = new ProductProperty();
        assertThat(property.hasKeyword(KeywordValueEnum.BANK_ONLINE_JOIN)).isFalse();

        addKeyword(property, KeywordValueEnum.BANK_ONLINE_JOIN);

        assertThat(property.hasKeyword(KeywordValueEnum.BANK_ONLINE_JOIN)).isTrue();
        assertThat(property.hasKeyword(KeywordValueEnum.BANK_CARD_USAGE)).isFalse();
    }

    @Test
    void hasPreferentialRateReflectsPreferentialRatesOnly() {
        ProductProperty property = new ProductProperty();
        assertThat(property.hasPreferentialRate(KeywordValueEnum.BANK_ONLINE_JOIN)).isFalse();

        addPreferentialRate(property, KeywordValueEnum.BANK_ONLINE_JOIN);

        assertThat(property.hasPreferentialRate(KeywordValueEnum.BANK_ONLINE_JOIN)).isTrue();
        assertThat(property.hasPreferentialRate(KeywordValueEnum.BANK_CARD_USAGE)).isFalse();
    }

    @Test
    void keywordTagsAndPreferentialRatesAreTrackedSeparately() {
        ProductProperty property = new ProductProperty();
        addKeyword(property, KeywordValueEnum.BANK_ONLINE_JOIN);
        addPreferentialRate(property, KeywordValueEnum.BANK_AGE);

        assertThat(property.hasKeyword(KeywordValueEnum.BANK_AGE)).isFalse();
        assertThat(property.hasPreferentialRate(KeywordValueEnum.BANK_ONLINE_JOIN)).isFalse();
    }

    private void addKeyword(ProductProperty property, KeywordValueEnum keywordCode) {
        ProductKeyword keyword = new ProductKeyword();
        ReflectionTestUtils.setField(keyword, "keywordCode", keywordCode);

        List<ProductKeyword> keywords = new ArrayList<>(property.getKeywords());
        keywords.add(keyword);
        ReflectionTestUtils.setField(property, "keywords", keywords);
    }

    private void addPreferentialRate(ProductProperty property, KeywordValueEnum keywordCode) {
        ProductPreferentialRate rate = new ProductPreferentialRate();
        ReflectionTestUtils.setField(rate, "keywordCode", keywordCode);

        List<ProductPreferentialRate> rates = new ArrayList<>(property.getPreferentialRates());
        rates.add(rate);
        ReflectionTestUtils.setField(property, "preferentialRates", rates);
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
