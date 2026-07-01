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
        addIfContains(keywords, content, KeywordValueEnum.BANK_AUTO_TRANSFER,
                "자동이체", "자동.*이체"
        );
        addIfContains(keywords, content, KeywordValueEnum.BANK_MARKETING,
                "마케팅", "상품서비스.*안내", "개인\\(?신용\\)?정보.*동의", "정보.*수집.*동의"
        );
        addIfContains(keywords, content, KeywordValueEnum.BANK_REDEPOSIT,
                "재예치", "재가입"
        );
        addIfContains(keywords, content, KeywordValueEnum.BANK_ONLINE_JOIN,
                "인터넷", "스마트폰", "비대면", "모바일"
        );

        return keywords.stream().toList();
    }
}
