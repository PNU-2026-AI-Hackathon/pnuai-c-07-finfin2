package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDraftFinalizerTest {

    private final ProductDraftFinalizer finalizer = new ProductDraftFinalizer();

    @Test
    void enforcesTagOwnershipAndDerivesEasyConditionFromFinalRateCount() {
        ProductPropertyDraft property = ProductPropertyDraft.builder()
                .keywords(List.of(
                        KeywordValueEnum.REGION_BUSAN,
                        KeywordValueEnum.BANK_CARD_USAGE,
                        KeywordValueEnum.STATUS_MILITARY
                ))
                .preferentialRates(rates(2))
                .build();

        ProductDraft result = finalizer.apply(ProductDraft.builder()
                .properties(List.of(property))
                .build());

        assertThat(result.properties().getFirst().keywords())
                .containsExactly(
                        KeywordValueEnum.REGION_BUSAN,
                        KeywordValueEnum.BENEFIT_EASY_CONDITION
                );
    }

    @Test
    void addsEasyConditionOnlyForOneToThreeRates() {
        ProductDraft result = finalizer.apply(ProductDraft.builder()
                .properties(List.of(
                        propertyWithRates(0),
                        propertyWithRates(1),
                        propertyWithRates(3),
                        propertyWithRates(4)
                ))
                .build());

        assertThat(result.properties().get(0).keywords())
                .doesNotContain(KeywordValueEnum.BENEFIT_EASY_CONDITION);
        assertThat(result.properties().get(1).keywords())
                .contains(KeywordValueEnum.BENEFIT_EASY_CONDITION);
        assertThat(result.properties().get(2).keywords())
                .contains(KeywordValueEnum.BENEFIT_EASY_CONDITION);
        assertThat(result.properties().get(3).keywords())
                .doesNotContain(KeywordValueEnum.BENEFIT_EASY_CONDITION);
    }

    private ProductPropertyDraft propertyWithRates(int count) {
        return ProductPropertyDraft.builder()
                .keywords(List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION))
                .preferentialRates(rates(count))
                .build();
    }

    private List<PreferentialRateDraft> rates(int count) {
        return IntStream.range(0, count)
                .mapToObj(index -> PreferentialRateDraft.builder()
                        .keywordCode(KeywordValueEnum.BANK_ETC)
                        .rate(new BigDecimal("0.10"))
                        .description("condition-" + index)
                        .build())
                .toList();
    }
}
