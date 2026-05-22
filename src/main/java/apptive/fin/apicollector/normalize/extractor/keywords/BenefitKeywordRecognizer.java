package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BenefitKeywordRecognizer implements KeywordRecognizer {
    @Override
    public List<KeywordValueEnum> recognize(ProductDraft productDraft, ProductPropertyDraft propertyDraft) {
        Set<KeywordValueEnum> keywords = new HashSet<>();
        String content =  productDraft.content();
        addIfContains(keywords, content, KeywordValueEnum.BENEFIT_TAX_FREE,
                "비과세"
        );
        addIfContains(keywords, content, KeywordValueEnum.BENEFIT_HOUSE_PREPARE,
                "내집마련", "주택"
        );
        addIfContains(keywords, content, KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                "기여금", "지원금", "장려금"
        );


        return keywords.stream().toList();
    }
}
