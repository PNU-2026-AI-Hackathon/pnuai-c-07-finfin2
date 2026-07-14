package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
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
        ProductType type = productDraft.type();

        // ProductType이 DEPOSIT/SAVING이면 텍스트 매칭 없이 결정적으로 INTEREST_SAVINGS를 부여한다.
        // ProductType에는 LOAN 값이 없으므로 INTEREST_LOAN은 상품명 매칭을 그대로 유지한다.
        if (type == ProductType.DEPOSIT || type == ProductType.SAVING) {
            keywords.add(KeywordValueEnum.INTEREST_SAVINGS);
        }
        else {
            // type이 없거나(POLICY/SUBSCRIPTION 등) 예적금 여부를 알 수 없을 때만 상품명 텍스트로 판별한다.
            addIfContains(keywords, title, KeywordValueEnum.INTEREST_SAVINGS, "적금", "예금", "저축");
        }
        addIfContains(keywords, title, KeywordValueEnum.INTEREST_LOAN, "대출");

        return keywords.stream().toList();
    }
}
