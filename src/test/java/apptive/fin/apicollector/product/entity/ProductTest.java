package apptive.fin.apicollector.product.entity;

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
        Product product = Product.create(source, provider, ProductType.GOVERNMENT, "P001", "청년 저축 지원");

        product.replaceKeywords(List.of(
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.INTEREST_SAVINGS
        ));
        ProductKeyword existing = product.getKeywords().stream()
                .filter(keyword -> keyword.getKeywordCode() == KeywordValueEnum.BENEFIT_GOV_SUBSIDY)
                .findFirst()
                .orElseThrow();

        product.replaceKeywords(List.of(
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.REGION_SEOUL
        ));

        assertThat(product.getKeywords())
                .extracting(ProductKeyword::getKeywordCode)
                .containsExactlyInAnyOrder(
                        KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                        KeywordValueEnum.REGION_SEOUL
                );
        assertThat(product.getKeywords()).contains(existing);
    }
}
