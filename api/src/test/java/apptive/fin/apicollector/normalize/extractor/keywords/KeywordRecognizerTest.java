package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordRecognizerTest {

    @Test
    void invalidRegexTokenDoesNotMatch() {
        KeywordRecognizer recognizer = new KeywordRecognizer() {
            @Override
            public List<KeywordValueEnum> recognize(ProductDraft productDraft, ProductPropertyDraft propertyDraft) {
                return List.of();
            }
        };

        assertThat(recognizer.matchesToken("savings account", "(")).isFalse();
    }

}
