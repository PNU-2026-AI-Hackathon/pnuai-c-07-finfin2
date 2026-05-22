package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class StatusKeywordRecognizer implements KeywordRecognizer {

    @Override
    public List<KeywordValueEnum> recognize(ProductDraft productDraft, ProductPropertyDraft propertyDraft) {
        String content = productDraft.content();
        Set<KeywordValueEnum> keywords = new HashSet<>();
        addIfContains(keywords, content, KeywordValueEnum.STATUS_UNEMPLOYED,
                "미취업", "무직"
        );
        addIfContains(keywords, content, KeywordValueEnum.STATUS_PART_TIME,
                "단시간근로자", "단시간 근로", "시간제근로", "시간제", "일용직"
        );
        addIfContains(keywords, content, KeywordValueEnum.STATUS_SME_WORKER,
                "중소기업"
        );
        addIfContains(keywords, content, KeywordValueEnum.STATUS_MILITARY,
                "군인", "군대", "장병", "병사"
        );


        return keywords.stream().toList();
    }
}
