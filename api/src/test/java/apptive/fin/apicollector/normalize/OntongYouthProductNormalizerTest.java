package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.classifier.OntongYouthPolicyClassifier;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.MonthlyLimitExtractor;
import apptive.fin.apicollector.normalize.extractor.keywords.BenefitKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.InterestKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.RegionKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.TermKeywordRecognizer;
import apptive.fin.apicollector.normalize.normalizer.OntongYouthProductNormalizer;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OntongYouthProductNormalizerTest {

    private final OntongYouthProductNormalizer normalizer = new OntongYouthProductNormalizer(
            new ObjectMapper(),
            properties(),
            new OntongYouthPolicyClassifier(),
            new MonthlyLimitExtractor(),
            keywordExtractor()
    );

    @Test
    void normalizesOnlyFinancialPolicy() {
        ProductRaw raw = new ProductRaw(Source.ONTONG, "P001", "hash", """
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
                """, ProductType.POLICY);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.classification()).isEqualTo(ProductClassification.FINANCIAL_PRODUCT);
        assertThat(draft.shouldSaveProduct()).isTrue();
        assertThat(draft.sourceCode()).isEqualTo("ONTONG");
        assertThat(draft.type()).isEqualTo(ProductType.POLICY);
        assertThat(draft.productCode()).isEqualTo("P001");
        assertThat(draft.properties()).hasSize(1);
        ProductPropertyDraft property = draft.properties().getFirst();
        assertThat(property.providerCode()).isEqualTo("ORG001");
        assertThat(property.maxMonthlyLimit()).isEqualTo(100_000L);
        assertThat(property.minAge()).isEqualTo(19);
        assertThat(property.maxAge()).isEqualTo(34);
        assertThat(property.earnMaxAmt()).isNull();
    }

    @Test
    void returnsSkippedDraftForLoanPolicy() {
        ProductRaw raw = new ProductRaw(Source.ONTONG, "P002", "hash", """
                {
                  "plcyNo": "P002",
                  "plcyNm": "청년 대출 지원",
                  "plcyKywdNm": "대출",
                  "mclsfNm": "취약계층 및 금융지원",
                  "plcyPvsnMthdCd": "0042006",
                  "plcySprtCn": "금리 지원"
                }
                """, ProductType.POLICY);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.classification()).isEqualTo(ProductClassification.LOAN_EXCLUDED);
        assertThat(draft.shouldSaveProduct()).isFalse();
        assertThat(draft.sourceCode()).isEqualTo("ONTONG");
    }

    @Test
    void extractsKeywordsFromOntongPolicyJson() {
        ProductRaw raw = new ProductRaw(Source.ONTONG, "P003", "hash", """
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
                """, ProductType.POLICY);

        ProductDraft draft = normalizer.normalize(raw);

        // BENEFIT_GOV_SUBSIDY는 텍스트 매칭(BenefitKeywordRecognizer)으로 더 이상 부여되지 않는다.
        // 수동입력(manual-products.json) 상품에서만 명시적으로 부여한다.
        assertThat(draft.properties().getFirst().keywords())
                .contains(
                        KeywordValueEnum.REGION_SEOUL,
                        KeywordValueEnum.BENEFIT_TAX_FREE,
                        KeywordValueEnum.INTEREST_SAVINGS
                )
                .doesNotContain(KeywordValueEnum.BENEFIT_GOV_SUBSIDY);
    }

    @Test
    void mapsJoinMethodEligibilityCautionAndRecruitmentPeriod() {
        ProductRaw raw = new ProductRaw(Source.ONTONG, "P004", "hash", """
                {
                  "plcyNo": "P004",
                  "plcyNm": "청년 내일 저축",
                  "plcyKywdNm": "보조금",
                  "mclsfNm": "취약계층 및 금융지원",
                  "plcyPvsnMthdCd": "0042006",
                  "plcyExplnCn": "청년 자산형성 지원",
                  "plcySprtCn": "매월 10만원 저축 시 장려금 지원",
                  "addAplyQlfcCndCn": "만 15세 이상 ~ 39세 이하",
                  "ptcpPrpTrgtCn": "기준 중위소득 50% 이하 청년",
                  "plcyAplyMthdCn": "읍면동 행정복지센터 방문 신청",
                  "etcMttrCn": "3년 통장 유지 필요",
                  "aplyYmd": "20250502 ~ 20250521",
                  "sprvsnInstCd": "ORG001",
                  "sprvsnInstCdNm": "테스트기관"
                }
                """, ProductType.POLICY);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.joinMethod()).isEqualTo("읍면동 행정복지센터 방문 신청");
        assertThat(draft.eligibilityText()).isEqualTo("만 15세 이상 ~ 39세 이하\n\n기준 중위소득 50% 이하 청년");
        assertThat(draft.cautionText()).isEqualTo("3년 통장 유지 필요");
        assertThat(draft.recruitmentPeriod()).isEqualTo("20250502 ~ 20250521");
    }

    private CollectorProperties properties() {
        return new CollectorProperties(
                true,
                Source.ALL,
                Mode.NORMALIZE_ONLY,
                3,
                500,
                7,
                new CollectorProperties.OntongYouth("http://localhost", "key", 100),
                new CollectorProperties.Fss("http://localhost", "key", 100),
                new CollectorProperties.Llm(false, "GEMINI", "gemini-test", 1, 1, 10, 3, 0.1, "http://localhost", "")
        );
    }

    private KeywordExtractor keywordExtractor() {
        return new KeywordExtractor(List.of(
                new BenefitKeywordRecognizer(),
                new InterestKeywordRecognizer(),
                new RegionKeywordRecognizer(),
                new TermKeywordRecognizer()
        ));
    }
}
