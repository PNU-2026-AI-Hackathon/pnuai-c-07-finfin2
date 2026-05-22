package apptive.fin.apicollector.product.entity;

import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void replaceKeywordsReusesExistingKeywordsAndAddsOnlyMissingOnes() {
        ProductSource source = ProductSource.create("ONTONG_YOUTH", "ONTONG_YOUTH");
        Provider provider = Provider.create(source, "ORG001", "테스트기관");
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
