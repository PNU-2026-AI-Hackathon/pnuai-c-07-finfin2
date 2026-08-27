package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BenefitKeywordRecognizerTest {

    private final BenefitKeywordRecognizer recognizer = new BenefitKeywordRecognizer();

    @Test
    void addsTaxFreeKeywordFromContentText() {
        ProductDraft draft = draft("이 상품은 비과세 혜택을 제공합니다.");

        assertThat(recognizer.recognize(draft, ProductPropertyDraft.builder().build()))
                .contains(KeywordValueEnum.BENEFIT_TAX_FREE);
    }

    @Test
    void doesNotAddGovSubsidyFromContentTextAnymore() {
        // BENEFIT_GOV_SUBSIDY는 텍스트 매칭으로 부여하지 않는다. 수동입력 상품에서만 명시적으로 부여한다.
        ProductDraft draft = draft("매월 저축 시 정부 지원금과 장려금, 기여금을 지급합니다.");

        assertThat(recognizer.recognize(draft, ProductPropertyDraft.builder().build()))
                .doesNotContain(KeywordValueEnum.BENEFIT_GOV_SUBSIDY);
    }

    private ProductDraft draft(String content) {
        return ProductDraft.builder()
                .content(content)
                .build();
    }
}
