package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InterestKeywordRecognizerTest {

    private final InterestKeywordRecognizer recognizer = new InterestKeywordRecognizer();

    @Test
    void addsInterestSavingsForSavingTypeRegardlessOfProductName() {
        ProductDraft draft = draft(ProductType.SAVING, "무지개 통장");

        assertThat(recognizer.recognize(draft, ProductPropertyDraft.builder().build()))
                .contains(KeywordValueEnum.INTEREST_SAVINGS);
    }

    @Test
    void addsInterestSavingsForDepositTypeRegardlessOfProductName() {
        ProductDraft draft = draft(ProductType.DEPOSIT, "무지개 통장");

        assertThat(recognizer.recognize(draft, ProductPropertyDraft.builder().build()))
                .contains(KeywordValueEnum.INTEREST_SAVINGS);
    }

    @Test
    void fallsBackToProductNameWhenTypeIsNotDepositOrSaving() {
        ProductDraft draft = draft(ProductType.POLICY, "청년 저축 지원");

        assertThat(recognizer.recognize(draft, ProductPropertyDraft.builder().build()))
                .contains(KeywordValueEnum.INTEREST_SAVINGS);
    }

    @Test
    void doesNotAddInterestSavingsWhenTypeIsUnrelatedAndNameHasNoMatch() {
        ProductDraft draft = draft(ProductType.SUBSCRIPTION, "청년 주택드림 청약");

        assertThat(recognizer.recognize(draft, ProductPropertyDraft.builder().build()))
                .doesNotContain(KeywordValueEnum.INTEREST_SAVINGS);
    }

    @Test
    void doesNotAddInterestLoanFromProductNameRegardlessOfType() {
        ProductDraft draft = draft(ProductType.POLICY, "청년 전세자금 대출");

        assertThat(recognizer.recognize(draft, ProductPropertyDraft.builder().build()))
                .doesNotContain(KeywordValueEnum.INTEREST_LOAN);
    }

    private ProductDraft draft(ProductType type, String productName) {
        return ProductDraft.builder()
                .type(type)
                .productName(productName)
                .build();
    }
}
