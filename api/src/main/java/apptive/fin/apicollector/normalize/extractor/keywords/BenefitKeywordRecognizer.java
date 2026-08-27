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
        // 정부지원 키워드(BENEFIT_GOV_SUBSIDY)는 수동입력(manual-products.json) 상품에서만 명시적으로 부여한다.
        // 은행 상품 설명 텍스트 매칭은 오탐(예: 일반 적금의 '지원금' 언급)이 많아 제거.

        return keywords.stream().toList();
    }
}
