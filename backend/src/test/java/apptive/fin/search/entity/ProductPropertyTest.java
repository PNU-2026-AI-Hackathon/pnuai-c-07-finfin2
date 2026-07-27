package apptive.fin.search.entity;

import apptive.fin.provider.entity.Provider;
import apptive.fin.search.ExtractionConfidence;
import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.RequiredKeywordEffect;
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
    void keywordCodesUnionsOwnedKeywordTablesAndDeduplicates() {
        ProductProperty property = new ProductProperty();
        addKeyword(property, KeywordValueEnum.REGION_BUSAN);
        addKeyword(property, KeywordValueEnum.BANK_ONLINE_JOIN);
        addKeyword(property, KeywordValueEnum.STATUS_UNEMPLOYED);
        addPreferentialRate(property, KeywordValueEnum.BANK_ONLINE_JOIN);
        addPreferentialRate(property, KeywordValueEnum.BANK_CARD_USAGE);
        addRequiredKeyword(
                property,
                KeywordValueEnum.STATUS_MILITARY,
                RequiredKeywordEffect.REQUIRE,
                ExtractionConfidence.HIGH
        );

        assertThat(property.keywordCodes()).containsExactly(
                KeywordValueEnum.REGION_BUSAN,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BANK_CARD_USAGE,
                KeywordValueEnum.BANK_ONLINE_JOIN
        );
    }

    @Test
    void keywordCodesOnlyIncludesOwnedRateAndHighConfidenceRequirements() {
        ProductProperty property = new ProductProperty();
        addKeyword(property, KeywordValueEnum.BANK_AUTO_TRANSFER);
        addKeyword(property, KeywordValueEnum.STATUS_MILITARY);
        addPreferentialRate(property, KeywordValueEnum.BENEFIT_EASY_CONDITION);
        addRequiredKeyword(
                property,
                KeywordValueEnum.STATUS_UNEMPLOYED,
                RequiredKeywordEffect.EXCLUDE,
                ExtractionConfidence.HIGH
        );
        addRequiredKeyword(
                property,
                KeywordValueEnum.STATUS_PART_TIME,
                RequiredKeywordEffect.REQUIRE,
                ExtractionConfidence.MEDIUM
        );
        addRequiredKeyword(
                property,
                KeywordValueEnum.BANK_CARD_USAGE,
                RequiredKeywordEffect.REQUIRE,
                ExtractionConfidence.HIGH
        );

        assertThat(property.keywordCodes()).isEmpty();
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

    private void addRequiredKeyword(
            ProductProperty property,
            KeywordValueEnum keywordCode,
            RequiredKeywordEffect effect,
            ExtractionConfidence confidence
    ) {
        ProductRequiredKeyword required = new ProductRequiredKeyword();
        ReflectionTestUtils.setField(required, "keywordCode", keywordCode);
        ReflectionTestUtils.setField(required, "effect", effect);
        ReflectionTestUtils.setField(required, "confidence", confidence);

        List<ProductRequiredKeyword> requiredKeywords = new ArrayList<>(property.getRequiredKeywords());
        requiredKeywords.add(required);
        ReflectionTestUtils.setField(property, "requiredKeywords", requiredKeywords);
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
