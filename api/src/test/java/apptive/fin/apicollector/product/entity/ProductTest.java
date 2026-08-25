package apptive.fin.apicollector.product.entity;

import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    private static Product newProduct() {
        ProductSource source = ProductSource.create("FSS", "FSS");
        Product product = Product.create(source, ProductType.SAVING, "P001", "테스트 적금");
        return product;
    }

    private static Provider newProvider(ProductSource source) {
        return Provider.create(source, "ORG001", "테스트은행", null);
    }

    private static ProductPropertyDraft draft(Integer saveTerm, String reserveType, BigDecimal baseRate) {
        return draft(saveTerm, reserveType, baseRate, null);
    }

    private static ProductPropertyDraft draft(
            Integer saveTerm,
            String reserveType,
            BigDecimal baseRate,
            String variantCode
    ) {
        return ProductPropertyDraft.builder()
                .variantCode(variantCode)
                .providerCode("ORG001")
                .providerName("테스트은행")
                .intrRateType("S")
                .reserveType(reserveType)
                .saveTerm(saveTerm)
                .baseRate(baseRate)
                .build();
    }

    @Test
    void replacePropertiesKeepsVariantsWithOtherwiseIdenticalCoordinatesDistinctAndStable() {
        Product product = newProduct();
        Provider provider = newProvider(product.getSource());

        product.replaceProperties(List.of(
                draft(36, null, new BigDecimal("2.00"), "GENERAL"),
                draft(36, null, new BigDecimal("4.00"), "PREFERENTIAL")
        ), ignored -> provider);
        ProductProperty general = product.getProperties().stream()
                .filter(property -> "GENERAL".equals(property.getVariantCode()))
                .findFirst()
                .orElseThrow();
        ProductProperty preferential = product.getProperties().stream()
                .filter(property -> "PREFERENTIAL".equals(property.getVariantCode()))
                .findFirst()
                .orElseThrow();

        product.replaceProperties(List.of(
                draft(36, null, new BigDecimal("2.50"), "GENERAL"),
                draft(36, null, new BigDecimal("4.50"), "PREFERENTIAL")
        ), ignored -> provider);

        assertThat(product.getProperties()).hasSize(2);
        assertThat(product.getProperties()).contains(general, preferential);
        assertThat(general.getBaseRate()).isEqualByComparingTo("2.50");
        assertThat(preferential.getBaseRate()).isEqualByComparingTo("4.50");
        assertThat(product.getProperties()).allMatch(ProductProperty::getIsJoinable);
    }

    @Test
    void replacePropertiesMarksFreshlyInsertedPropertiesJoinable() {
        Product product = newProduct();
        Provider provider = newProvider(product.getSource());

        product.replaceProperties(
                List.of(draft(12, "F", new BigDecimal("3.00")), draft(24, "F", new BigDecimal("3.20"))),
                ignored -> provider
        );

        assertThat(product.getProperties()).hasSize(2);
        assertThat(product.getProperties())
                .allMatch(ProductProperty::getIsJoinable);
    }

    @Test
    void replacePropertiesUpdatesMatchingPropertyInPlaceKeepingInstance() {
        Product product = newProduct();
        Provider provider = newProvider(product.getSource());

        product.replaceProperties(List.of(draft(12, "F", new BigDecimal("3.00"))), ignored -> provider);
        ProductProperty original = product.getProperties().getFirst();

        product.replaceProperties(List.of(draft(12, "F", new BigDecimal("3.50"))), ignored -> provider);

        assertThat(product.getProperties()).hasSize(1);
        assertThat(product.getProperties().getFirst()).isSameAs(original);
        assertThat(original.getBaseRate()).isEqualByComparingTo("3.50");
        assertThat(original.getIsJoinable()).isTrue();
    }

    @Test
    void replacePropertiesAddsNewKeyWhileKeepingExisting() {
        Product product = newProduct();
        Provider provider = newProvider(product.getSource());

        product.replaceProperties(List.of(draft(12, "F", new BigDecimal("3.00"))), ignored -> provider);
        ProductProperty original = product.getProperties().getFirst();

        product.replaceProperties(
                List.of(draft(12, "F", new BigDecimal("3.00")), draft(24, "F", new BigDecimal("3.20"))),
                ignored -> provider
        );

        assertThat(product.getProperties()).hasSize(2);
        assertThat(product.getProperties()).contains(original);
        assertThat(product.getProperties())
                .extracting(ProductProperty::getSaveTrm)
                .containsExactlyInAnyOrder(12, 24);
    }

    @Test
    void replacePropertiesSoftDisablesVanishedPropertyInsteadOfDeleting() {
        Product product = newProduct();
        Provider provider = newProvider(product.getSource());
        ProductPropertyDraft vanishedDraft = draft(24, "F", new BigDecimal("3.20"))
                .toBuilder()
                .keywords(List.of(
                        KeywordValueEnum.REGION_BUSAN,
                        KeywordValueEnum.BANK_ONLINE_JOIN,
                        KeywordValueEnum.STATUS_MILITARY
                ))
                .build();

        product.replaceProperties(
                List.of(draft(12, "F", new BigDecimal("3.00")), vanishedDraft),
                ignored -> provider
        );
        ProductProperty term24 = product.getProperties().stream()
                .filter(p -> p.getSaveTrm() == 24)
                .findFirst()
                .orElseThrow();

        product.replaceProperties(List.of(draft(12, "F", new BigDecimal("3.00"))), ignored -> provider);

        assertThat(product.getProperties()).hasSize(2);
        assertThat(product.getProperties()).contains(term24);
        assertThat(term24.getIsJoinable()).isFalse();
        assertThat(term24.getKeywords())
                .extracting(ProductKeyword::getKeywordCode)
                .containsExactly(KeywordValueEnum.REGION_BUSAN);
        assertThat(product.getProperties().stream()
                .filter(p -> p.getSaveTrm() == 12)
                .findFirst()
                .orElseThrow()
                .getIsJoinable()).isTrue();
    }

    @Test
    void replacePropertiesReactivatesSoftDisabledPropertyWhenKeyReappears() {
        Product product = newProduct();
        Provider provider = newProvider(product.getSource());

        product.replaceProperties(List.of(draft(24, "F", new BigDecimal("3.20"))), ignored -> provider);
        ProductProperty term24 = product.getProperties().getFirst();

        product.replaceProperties(List.of(draft(12, "F", new BigDecimal("3.00"))), ignored -> provider);
        assertThat(term24.getIsJoinable()).isFalse();

        product.replaceProperties(List.of(draft(24, "F", new BigDecimal("3.40"))), ignored -> provider);

        assertThat(product.getProperties()).contains(term24);
        assertThat(term24.getIsJoinable()).isTrue();
        assertThat(term24.getBaseRate()).isEqualByComparingTo("3.40");
    }

    @Test
    void replacePropertiesCanPreserveExistingApplyUrlWhenDraftOmitsIt() {
        Product product = newProduct();
        Provider provider = newProvider(product.getSource());
        ProductPropertyDraft initial = draft(12, "F", new BigDecimal("3.00"))
                .toBuilder()
                .applyUrl("https://bank.example/product")
                .build();
        product.replaceProperties(List.of(initial), ignored -> provider);

        product.replaceProperties(
                List.of(draft(12, "F", new BigDecimal("3.50"))),
                ignored -> provider,
                true
        );

        assertThat(product.getProperties().getFirst().getApplyUrl())
                .isEqualTo("https://bank.example/product");
    }

    @Test
    void replaceKeywordsReusesExistingKeywordsAndAddsOnlyMissingOnes() {
        ProductSource source = ProductSource.create("ONTONG_YOUTH", "ONTONG_YOUTH");
        Provider provider = Provider.create(source, "ORG001", "테스트기관", null);
        Product product = Product.create(source, ProductType.POLICY, "P001", "청년 저축 지원");
        product.replaceProperties(List.of(ProductPropertyDraft.builder()
                .providerCode("ORG001")
                .providerName("테스트기관")
                .build()), ignored -> provider);
        ProductProperty property = product.getProperties().getFirst();

        property.replaceKeywords(List.of(
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.INTEREST_SAVINGS
        ));
        ProductKeyword existing = property.getKeywords().stream()
                .filter(keyword -> keyword.getKeywordCode() == KeywordValueEnum.BENEFIT_GOV_SUBSIDY)
                .findFirst()
                .orElseThrow();

        property.replaceKeywords(List.of(
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.REGION_SEOUL
        ));

        assertThat(property.getKeywords())
                .extracting(ProductKeyword::getKeywordCode)
                .containsExactlyInAnyOrder(
                        KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                        KeywordValueEnum.REGION_SEOUL
                );
        assertThat(property.getKeywords()).contains(existing);
    }
}
