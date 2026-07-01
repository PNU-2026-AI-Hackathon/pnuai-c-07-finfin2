package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.FssPreferentialRateExtractor;
import apptive.fin.apicollector.normalize.extractor.FssRequiredKeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.keywords.BankKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.BenefitKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.InterestKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.RegionKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.StatusKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.TermKeywordRecognizer;
import apptive.fin.apicollector.normalize.normalizer.FssProductNormalizer;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FssProductNormalizerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FssProductNormalizer normalizer = new FssProductNormalizer(
            objectMapper,
            properties(),
            keywordExtractor(),
            new FssPreferentialRateExtractor(),
            new FssRequiredKeywordExtractor()
    );

    @Test
    void normalizesFssRawProduct() {
        ProductRaw raw = new ProductRaw(Source.FSS, "FSS:SAVING:001:ABC", "hash", """
                {
                  "source": "FSS",
                  "productType": "SAVING",
                  "financialGroupName": "은행",
                  "base": {
                    "fin_co_no": "001",
                    "kor_co_nm": "테스트은행",
                    "fin_prdt_nm": "청년 적금",
                    "join_way": "모바일",
                    "spcl_cnd": "급여 이체 우대",
                    "max_limit": 300000
                  },
                  "options": [
                    {"intr_rate_type": "S", "intr_rate_type_nm": "단리", "save_trm": "12", "intr_rate": 3.1, "intr_rate2": 4.1},
                    {"intr_rate_type": "S", "intr_rate_type_nm": "단리", "save_trm": "24", "intr_rate": 3.5, "intr_rate2": 4.5}
                  ]
                }
                """, ProductType.SAVING);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.sourceCode()).isEqualTo("FSS");
        assertThat(draft.type()).isEqualTo(ProductType.SAVING);
        assertThat(draft.productCode()).isEqualTo("FSS:SAVING:001:ABC");
        assertThat(draft.productName()).isEqualTo("청년 적금");
        assertThat(draft.properties()).hasSize(2);
        assertThat(draft.properties().get(1).providerCode()).isEqualTo("001");
        assertThat(draft.properties().get(1).providerName()).isEqualTo("테스트은행");
        assertThat(draft.properties().get(1).baseRate()).isEqualByComparingTo("3.5");
        assertThat(draft.properties().get(1).maxRate()).isEqualByComparingTo("4.5");
        assertThat(draft.properties().get(1).maxMonthlyLimit()).isEqualTo(300_000L);
        assertThat(draft.properties().get(1).minTenureMonths()).isNull();
        assertThat(draft.shouldSaveProduct()).isTrue();
    }

    @Test
    void extractsKeywordsFromFssProductJson() {
        ProductRaw raw = new ProductRaw(Source.FSS, "FSS:SAVING:002:DEF", "hash", """
                {
                  "source": "FSS",
                  "productType": "SAVING",
                  "financialGroupName": "은행",
                  "base": {
                    "fin_co_no": "002",
                    "kor_co_nm": "테스트은행",
                    "fin_prdt_nm": "급여 카드 우대 청년 적금",
                    "join_way": "모바일",
                    "spcl_cnd": "급여 이체와 신용/체크카드 사용 시 우대금리 제공",
                    "join_member": "첫거래 고객 우대",
                    "max_limit": 500000
                  },
                  "options": [
                    {"intr_rate_type": "S", "intr_rate_type_nm": "단리", "save_trm": "12", "intr_rate": 3.0, "intr_rate2": 4.0}
                  ]
                }
                """, ProductType.SAVING);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.properties().getFirst().keywords())
                .contains(
                        KeywordValueEnum.INTEREST_SAVINGS,
                        KeywordValueEnum.BANK_SALARY_TRANSFER,
                        KeywordValueEnum.BANK_CARD_USAGE,
                        KeywordValueEnum.BANK_FIRST_TRANSACTION
                );
    }

    @Test
    void extractsPreferentialRatesAndRequiredKeywordsFromFssText() {
        ProductRaw raw = new ProductRaw(Source.FSS, "FSS:SAVING:003:GHI", "hash", """
                {
                  "source": "FSS",
                  "productType": "SAVING",
                  "financialGroupName": "은행",
                  "base": {
                    "fin_co_no": "003",
                    "kor_co_nm": "테스트은행",
                    "fin_prdt_nm": "중소기업 우대 적금",
                    "join_way": "스마트폰",
                    "spcl_cnd": "최고우대금리 1.0%p\\n급여이체 실적 : 0.5%p\\n카드 사용 : 0.3%p\\n마케팅 동의 : 0.1%p",
                    "join_member": "중소기업 재직 근로자",
                    "max_limit": 500000
                  },
                  "options": [
                    {"intr_rate_type": "S", "intr_rate_type_nm": "단리", "save_trm": "12", "intr_rate": 3.0, "intr_rate2": 4.0}
                  ]
                }
                """, ProductType.SAVING);

        ProductDraft draft = normalizer.normalize(raw);
        ProductPropertyDraft property = draft.properties().getFirst();

        assertThat(property.preferentialRates())
                .extracting(rate -> rate.keywordCode())
                .containsExactlyInAnyOrder(
                        KeywordValueEnum.BANK_SALARY_TRANSFER,
                        KeywordValueEnum.BANK_CARD_USAGE,
                        KeywordValueEnum.BANK_MARKETING
                );
        assertThat(property.preferentialRates())
                .allSatisfy(rate -> assertThat(rate.rate()).isPositive());
        assertThat(property.requiredKeywords())
                .extracting(required -> required.keywordCode())
                .containsExactly(KeywordValueEnum.STATUS_SME_WORKER);
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
                new BankKeywordRecognizer(),
                new InterestKeywordRecognizer(),
                new RegionKeywordRecognizer(),
                new StatusKeywordRecognizer(),
                new TermKeywordRecognizer()
        ));
    }
}
