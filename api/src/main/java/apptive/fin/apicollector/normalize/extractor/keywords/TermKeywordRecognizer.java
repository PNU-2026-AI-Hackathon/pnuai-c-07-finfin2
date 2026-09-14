package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TermKeywordRecognizer implements KeywordRecognizer {
    @Override
    public List<KeywordValueEnum> recognize(ProductDraft productDraft, ProductPropertyDraft propertyDraft) {
        Integer term = propertyDraft.saveTerm();
        if (term == null) {
            return List.of();
        }
        return List.of(TermKeywords.bucket(term));
    }
}
