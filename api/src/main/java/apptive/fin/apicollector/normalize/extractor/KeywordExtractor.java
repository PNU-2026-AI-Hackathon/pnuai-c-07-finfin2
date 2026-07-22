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

    /** draft의 모든 property에 키워드를 추출·부착한 새 draft를 반환한다. */
    public ProductDraft attachTo(ProductDraft draft) {
        List<ProductPropertyDraft> properties = new ArrayList<>();
        for (ProductPropertyDraft property : draft.properties()) {
            properties.add(property.toBuilder()
                    .keywords(extract(draft, property))
                    .build());
        }
        return draft.toBuilder()
                .properties(properties)
                .build();
    }

}
