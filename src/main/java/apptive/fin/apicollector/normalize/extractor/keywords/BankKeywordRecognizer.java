package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BankKeywordRecognizer implements KeywordRecognizer {


    @Override
    public List<KeywordValueEnum> recognize(ProductDraft productDraft, ProductPropertyDraft propertyDraft) {
        String content = productDraft.productName() + " " + productDraft.content();
        Set<KeywordValueEnum> keywords = new HashSet<>();
        addIfContains(keywords, content, KeywordValueEnum.BANK_CARD_USAGE,
                "(신용|체크).*카드", "카드결제", "카드사용", "카드.*결제"
        );
        addIfContains(keywords, content, KeywordValueEnum.BANK_SALARY_TRANSFER,
                "급여.*(입금|이체)"
        );
        addIfContains(keywords, content, KeywordValueEnum.BANK_FIRST_TRANSACTION,
                "첫거래", "최초거래", "신규고객", "첫고객"
        );

        return keywords.stream().toList();
    }
}
