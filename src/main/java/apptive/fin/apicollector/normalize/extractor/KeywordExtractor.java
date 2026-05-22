package apptive.fin.apicollector.normalize.extractor;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.keywords.KeywordRecognizer;
import apptive.fin.apicollector.product.KeywordValueEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeywordExtractor {
    private final List<KeywordRecognizer> keywordRecognizers;

    public List<KeywordValueEnum> extract(ProductDraft productDraft, ProductPropertyDraft propertyDraft) {
        List<KeywordValueEnum> keywords = new ArrayList<>();
        for (KeywordRecognizer keywordRecognizer : keywordRecognizers) {
            keywords.addAll(keywordRecognizer.recognize(productDraft, propertyDraft));
        }
        return keywords;
    }

}
