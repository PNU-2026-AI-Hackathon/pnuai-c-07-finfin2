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

class FssProductNormalizerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FssProductNormalizer normalizer = new FssProductNormalizer(objectMapper, properties());

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
                """);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.sourceCode()).isEqualTo("FSS");
        assertThat(draft.type()).isEqualTo(ProductType.BANK);
        assertThat(draft.productCode()).isEqualTo("FSS:SAVING:001:ABC");
        assertThat(draft.productName()).isEqualTo("청년 적금");
        assertThat(draft.baseRate()).isEqualByComparingTo("3.5");
        assertThat(draft.maxRate()).isEqualByComparingTo("4.5");
        assertThat(draft.maxMonthlyLimit()).isEqualTo(300_000L);
        assertThat(draft.minTenureMonths()).isEqualTo(24);
        assertThat(draft.options()).hasSize(2);
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
                    "spcl_cnd": "급여 이체와 카드 사용 시 우대금리 제공",
                    "join_member": "첫거래 고객 우대",
                    "max_limit": 500000
                  },
                  "options": [
                    {"intr_rate_type": "S", "intr_rate_type_nm": "단리", "save_trm": "12", "intr_rate": 3.0, "intr_rate2": 4.0}
                  ]
                }
                """);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.keywords())
                .contains(
                        KeywordValueEnum.INTEREST_SAVINGS,
                        KeywordValueEnum.BENEFIT_MAX_INTEREST,
                        KeywordValueEnum.BANK_SALARY_TRANSFER,
                        KeywordValueEnum.BANK_CARD_USAGE,
                        KeywordValueEnum.BANK_FIRST_TRANSACTION
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
