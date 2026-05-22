package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TermKeywordRecognizer implements KeywordRecognizer {
    @Override
    public List<KeywordValueEnum> recognize(ProductDraft productDraft, ProductPropertyDraft propertyDraft) {
        Integer term = propertyDraft.saveTerm();
        if (term == null)
            return List.of();

        Set<KeywordValueEnum> keywords = new HashSet<>();
        if (term < 24) {
            keywords.add(KeywordValueEnum.TERM_AROUND_1_YEAR);
        }
        else if (term < 37) {
            keywords.add(KeywordValueEnum.TERM_2_TO_3_YEARS);
        }
        else {
            keywords.add(KeywordValueEnum.TERM_OVER_5_YEARS);
        }

        return keywords.stream().toList();
    }
}
