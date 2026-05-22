package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.normalize.classifier.OntongYouthPolicyClassifier;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;

class OntongYouthPolicyClassifierTest {

    private final OntongYouthPolicyClassifier classifier = new OntongYouthPolicyClassifier();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void classifiesFinancialProduct() {
        ObjectNode policy = policy("취약계층 및 금융지원", "보조금", "0042006", "청년적금");

        ProductClassification result = classifier.classify(policy);

        assertThat(result).isEqualTo(ProductClassification.FINANCIAL_PRODUCT);
    }

    @Test
    void excludesLoanByKeyword() {
        ObjectNode policy = policy("취약계층 및 금융지원", "대출", "0042006", "금리 혜택 지원");

        ProductClassification result = classifier.classify(policy);

        assertThat(result).isEqualTo(ProductClassification.LOAN_EXCLUDED);
    }

    @Test
    void excludesLoanByMethodCode() {
        ObjectNode policy = policy("취약계층 및 금융지원", "보조금", "0042003", "매월 적금 지원");

        ProductClassification result = classifier.classify(policy);

        assertThat(result).isEqualTo(ProductClassification.LOAN_EXCLUDED);
    }

    @Test
    void excludesNonFinanceCandidate() {
        ObjectNode policy = policy("문화예술", "교육", "0042006", "저축 장려금 지원");

        ProductClassification result = classifier.classify(policy);

        assertThat(result).isEqualTo(ProductClassification.EXCLUDED);
    }

    @Test
    void marksFinanceCandidateWithoutProductKeywordAsUnclassified() {
        ObjectNode policy = policy("취약계층 및 금융지원", "보조금", "0042006", "상담 프로그램 제공");

        ProductClassification result = classifier.classify(policy);

        assertThat(result).isEqualTo(ProductClassification.UNCLASSIFIED);
    }

    private ObjectNode policy(String category, String keyword, String methodCode, String supportContent) {
        ObjectNode policy = objectMapper.createObjectNode();
        policy.put("mclsfNm", category);
        policy.put("plcyKywdNm", keyword);
        policy.put("plcyPvsnMthdCd", methodCode);
        policy.put("plcySprtCn", supportContent);
        return policy;
    }
}
