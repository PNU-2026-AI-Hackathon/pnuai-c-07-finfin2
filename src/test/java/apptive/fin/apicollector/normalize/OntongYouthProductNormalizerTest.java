package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class OntongYouthProductNormalizerTest {

    private final OntongYouthProductNormalizer normalizer = new OntongYouthProductNormalizer(
            new ObjectMapper(),
            properties(),
            new OntongYouthPolicyClassifier(),
            new MonthlyLimitExtractor()
    );

    @Test
    void normalizesOnlyFinancialPolicy() {
        ProductRaw raw = new ProductRaw(Source.ONTONG_YOUTH, "P001", "hash", """
                {
                  "plcyNo": "P001",
                  "plcyNm": "청년 저축 지원",
                  "plcyKywdNm": "보조금",
                  "mclsfNm": "취약계층 및 금융지원",
                  "plcyPvsnMthdCd": "0042006",
                  "plcyExplnCn": "청년 자산형성 지원",
                  "plcySprtCn": "매월 10만원 저축 시 장려금 지원",
                  "sprvsnInstCd": "ORG001",
                  "sprvsnInstCdNm": "테스트기관",
                  "sprtTrgtMinAge": "19",
                  "sprtTrgtMaxAge": "34",
                  "earnMaxAmt": "0",
                  "aplyUrlAddr": "https://example.com"
                }
                """);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.classification()).isEqualTo(ProductClassification.FINANCIAL_PRODUCT);
        assertThat(draft.shouldSaveProduct()).isTrue();
        assertThat(draft.sourceCode()).isEqualTo("ONTONG_YOUTH");
        assertThat(draft.type()).isEqualTo(ProductType.GOVERNMENT);
        assertThat(draft.productCode()).isEqualTo("P001");
        assertThat(draft.providerCode()).isEqualTo("ORG001");
        assertThat(draft.maxMonthlyLimit()).isEqualTo(100_000L);
        assertThat(draft.minAge()).isEqualTo(19);
        assertThat(draft.maxAge()).isEqualTo(34);
        assertThat(draft.earnMaxAmt()).isNull();
        assertThat(draft.options()).isEmpty();
    }

    @Test
    void returnsSkippedDraftForLoanPolicy() {
        ProductRaw raw = new ProductRaw(Source.ONTONG_YOUTH, "P002", "hash", """
                {
                  "plcyNo": "P002",
                  "plcyNm": "청년 대출 지원",
                  "plcyKywdNm": "대출",
                  "mclsfNm": "취약계층 및 금융지원",
                  "plcyPvsnMthdCd": "0042006",
                  "plcySprtCn": "금리 지원"
                }
                """);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.classification()).isEqualTo(ProductClassification.LOAN_EXCLUDED);
        assertThat(draft.shouldSaveProduct()).isFalse();
        assertThat(draft.sourceCode()).isEqualTo("ONTONG_YOUTH");
    }

    @Test
    void extractsKeywordsFromOntongPolicyJson() {
        ProductRaw raw = new ProductRaw(Source.ONTONG_YOUTH, "P003", "hash", """
                {
                  "plcyNo": "P003",
                  "plcyNm": "서울 청년 저축 장려금",
                  "plcyKywdNm": "보조금",
                  "lclsfNm": "복지문화",
                  "mclsfNm": "취약계층 및 금융지원",
                  "plcyPvsnMthdCd": "0042006",
                  "plcyExplnCn": "서울 거주 청년의 자산형성을 지원합니다.",
                  "plcySprtCn": "매월 15만원 저축 시 장려금과 비과세 혜택을 제공합니다.",
                  "zipCd": "서울",
                  "sprvsnInstCd": "SEOUL",
                  "sprvsnInstCdNm": "서울시"
                }
                """);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.keywords())
                .contains(
                        KeywordValueEnum.REGION_SEOUL,
                        KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                        KeywordValueEnum.BENEFIT_TAX_FREE,
                        KeywordValueEnum.INTEREST_SAVINGS
                );
    }

    private CollectorProperties properties() {
        return new CollectorProperties(
                true,
                Source.ALL,
                Mode.NORMALIZE_ONLY,
                3,
                500,
                new CollectorProperties.OntongYouth("http://localhost", "key", 100),
                new CollectorProperties.Fss("http://localhost", "key", 100)
        );
    }
}
