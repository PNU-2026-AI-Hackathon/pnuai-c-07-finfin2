package apptive.fin.apicollector.normalize.extractor;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.keywords.KeywordRecognizer;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordExtractorTest {

    @Test
    void extractsOnlyTagOwnedKeywordFamilies() {
        KeywordRecognizer recognizer = (product, property) -> List.of(
                KeywordValueEnum.BANK_CARD_USAGE,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.REGION_BUSAN,
                KeywordValueEnum.REGION_BUSAN
        );
        KeywordExtractor extractor = new KeywordExtractor(List.of(recognizer));

        List<KeywordValueEnum> result = extractor.extract(
                ProductDraft.builder().build(),
                ProductPropertyDraft.builder().build()
        );

        assertThat(result).containsExactly(KeywordValueEnum.REGION_BUSAN);
    }
}
