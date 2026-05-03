package apptive.fin.apicollector.normalize;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Component
public class OntongYouthPolicyClassifier extends AbstractProductNormalizer {

    private static final String FINANCE_CATEGORY = "취약계층 및 금융지원";
    private static final String SUBSIDY_KEYWORD = "보조금";
    private static final String LOAN_KEYWORD = "대출";
    private static final List<String> LOAN_METHOD_CODES = List.of("42003", "42007");
    private static final List<String> FINANCIAL_KEYWORDS = List.of(
            "저축",
            "적금",
            "예금",
            "자산형성",
            "매칭",
            "장려금",
            "기여금",
            "적립",
            "내일채움",
            "내일저축",
            "미래적금",
            "납입",
            "목돈",
            "비과세",
            "청약",
            "주택드림",
            "분양"
    );

    public ProductClassification classify(JsonNode policy) {
        if (!isFinanceCandidate(policy)) {
            return ProductClassification.EXCLUDED;
        }

        if (isLoan(policy)) {
            return ProductClassification.LOAN_EXCLUDED;
        }

        if (hasFinancialKeyword(policy)) {
            return ProductClassification.FINANCIAL_PRODUCT;
        }

        return ProductClassification.UNCLASSIFIED;
    }

    private boolean isFinanceCandidate(JsonNode policy) {
        String category = text(policy, "mclsfNm");
        String keywords = text(policy, "plcyKywdNm");

        return FINANCE_CATEGORY.equals(category)
                || contains(keywords, SUBSIDY_KEYWORD);
    }

    private boolean isLoan(JsonNode policy) {
        String keywords = text(policy, "plcyKywdNm");
        String methodCode = text(policy, "plcyPvsnMthdCd");

        return contains(keywords, LOAN_KEYWORD)
                || LOAN_METHOD_CODES.stream().anyMatch(code -> hasCode(methodCode, code));
    }

    private boolean hasFinancialKeyword(JsonNode policy) {
        String supportContent = text(policy, "plcySprtCn");
        return FINANCIAL_KEYWORDS.stream().anyMatch(keyword -> contains(supportContent, keyword));
    }

    private boolean hasCode(String rawCode, String targetCode) {
        return rawCode != null && rawCode.replaceFirst("^0+", "").equals(targetCode);
    }

    private boolean contains(String value, String token) {
        return value != null && value.contains(token);
    }
}
