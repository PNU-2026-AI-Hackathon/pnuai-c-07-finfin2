package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class InterestKeywordRecognizer implements KeywordRecognizer {
    @Override
    public List<KeywordValueEnum> recognize(ProductDraft productDraft, ProductPropertyDraft propertyDraft) {
        Set<KeywordValueEnum> keywords = new HashSet<KeywordValueEnum>();
        String title = productDraft.productName();
        addIfContains(keywords, title, KeywordValueEnum.INTEREST_SAVINGS, "적금", "예금", "저축");
        addIfContains(keywords, title, KeywordValueEnum.INTEREST_LOAN, "대출");

        return keywords.stream().toList();
    }
}
