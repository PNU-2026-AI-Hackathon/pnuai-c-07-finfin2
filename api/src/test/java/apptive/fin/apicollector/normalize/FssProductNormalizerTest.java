package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.FssPreferentialRateExtractor;
import apptive.fin.apicollector.normalize.extractor.FssRequiredKeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.keywords.BenefitKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.InterestKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.RegionKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.TermKeywordRecognizer;
import apptive.fin.apicollector.normalize.normalizer.FssBankNameNormalizer;
import apptive.fin.apicollector.normalize.normalizer.FssBankUrlNormalizer;
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
            new FssRequiredKeywordExtractor(),
            new FssBankNameNormalizer(),
            new FssBankUrlNormalizer()
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
                .containsExactly(
                        KeywordValueEnum.INTEREST_SAVINGS,
                        KeywordValueEnum.TERM_AROUND_1_YEAR
                );
        assertThat(draft.properties().getFirst().preferentialRates()).isEmpty();
    }

    @Test
    void collapsesWhitespaceInProductName() {
        ProductRaw raw = new ProductRaw(Source.FSS, "FSS:SAVING:005:MNO", "hash", """
                {
                  "source": "FSS",
                  "productType": "SAVING",
                  "financialGroupName": "은행",
                  "base": {
                    "fin_co_no": "005",
                    "kor_co_nm": "테스트은행",
                    "fin_prdt_nm": "스마일드림 \\n정기예금\\n(개인)"
                  },
                  "options": []
                }
                """, ProductType.SAVING);

        ProductDraft draft = normalizer.normalize(raw);

        // 내부 개행은 공백으로 축약되지만(스마일드림 \n정기예금 -> 스마일드림 정기예금),
        // 이름 끝 괄호 "(개인)"은 원천 정보라 그대로 둔다.
        assertThat(draft.productName()).isEqualTo("스마일드림 정기예금 (개인)");
    }

    @Test
    void keepsTrailingParenSoScrapersCanTellProductsApart() {
        // 이름 끝 괄호는 적립·지급 방식을 담고 있고, 은행 URL 스크래퍼가 상품을 구분하는 유일한 근거다.
        // 표시용으로 떼는 것은 backend 의 Product.getDisplayProductName() 책임이다.
        assertThat(normalizedName("JB 다이렉트적금(자유적립식)")).isEqualTo("JB 다이렉트적금(자유적립식)");
        assertThat(normalizedName("제주Dream\\n정기예금\\n(개인/만기\\n지급식)"))
                .isEqualTo("제주Dream 정기예금 (개인/만기 지급식)");
        assertThat(normalizedName("Sh해양플라스틱Zero!적금\\n(정액적립식)"))
                .isEqualTo("Sh해양플라스틱Zero!적금 (정액적립식)");
        assertThat(normalizedName("The든든예금(시즌2)")).isEqualTo("The든든예금(시즌2)");
        // 중간 브랜드 괄호도 당연히 그대로
        assertThat(normalizedName("더(The) 특판 정기예금")).isEqualTo("더(The) 특판 정기예금");
    }

    // fin_prdt_nm(JSON 리터럴; 개행은 \\n으로 전달)만 다른 최소 raw를 만들어 정규화된 상품명을 돌려준다.
    private String normalizedName(String finPrdtNmJsonLiteral) {
        ProductRaw raw = new ProductRaw(Source.FSS, "FSS:SAVING:001:ABC", "hash", """
                {
                  "source": "FSS",
                  "productType": "SAVING",
                  "financialGroupName": "은행",
                  "base": {
                    "fin_co_no": "001",
                    "kor_co_nm": "테스트은행",
                    "fin_prdt_nm": "%s"
                  },
                  "options": []
                }
                """.formatted(finPrdtNmJsonLiteral), ProductType.SAVING);
        return normalizer.normalize(raw).productName();
    }

    @Test
    void collapsesWhitespaceInProviderName() {
        // fin_co_no가 FssBankNameNormalizer의 코드-이름 매핑에 없는 값이라 kor_co_nm 원문이 그대로 흘러가며,
        // 개행/공백이 포함된 경우 \s+ -> 단일 공백으로 축약되어야 한다.
        ProductRaw raw = new ProductRaw(Source.FSS, "FSS:SAVING:006:PQR", "hash", """
                {
                  "source": "FSS",
                  "productType": "SAVING",
                  "financialGroupName": "은행",
                  "base": {
                    "fin_co_no": "006",
                    "kor_co_nm": "광주\\n은행",
                    "fin_prdt_nm": "청년 적금"
                  },
                  "options": []
                }
                """, ProductType.SAVING);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.properties().getFirst().providerName()).isEqualTo("광주 은행");
    }

    @Test
    void normalizesFssProviderNameByCompanyCode() {
        ProductRaw raw = new ProductRaw(Source.FSS, "FSS:SAVING:0013909:XYZ", "hash", """
                {
                  "source": "FSS",
                  "productType": "SAVING",
                  "financialGroupName": "은행",
                  "base": {
                    "fin_co_no": "0013909",
                    "kor_co_nm": "주식회사 하나은행",
                    "fin_prdt_nm": "하나 청년 적금"
                  },
                  "options": []
                }
                """, ProductType.SAVING);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.properties().getFirst().providerName()).isEqualTo("하나은행");
        assertThat(draft.properties().getFirst().providerApplyUrl())
                .isEqualTo("https://www.kebhana.com/cont/mall/index.jsp");
    }

    @Test
    void leavesProviderApplyUrlNullForUnknownCompanyCode() {
        ProductRaw raw = new ProductRaw(Source.FSS, "FSS:SAVING:001:ABC", "hash", """
                {
                  "source": "FSS",
                  "productType": "SAVING",
                  "financialGroupName": "은행",
                  "base": {
                    "fin_co_no": "001",
                    "kor_co_nm": "테스트은행",
                    "fin_prdt_nm": "청년 적금"
                  },
                  "options": []
                }
                """, ProductType.SAVING);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.properties().getFirst().providerApplyUrl()).isNull();
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

    @Test
    void mapsJoinMethodEligibilityCautionAndInstallmentType() {
        ProductRaw raw = new ProductRaw(Source.FSS, "FSS:SAVING:004:JKL", "hash", """
                {
                  "source": "FSS",
                  "productType": "SAVING",
                  "financialGroupName": "은행",
                  "base": {
                    "fin_co_no": "004",
                    "kor_co_nm": "테스트은행",
                    "fin_prdt_nm": "자유적금",
                    "join_way": "영업점,인터넷,스마트폰",
                    "join_member": "실명의 개인",
                    "etc_note": "만기 후 이자율은 기본이율의 50%로 낮아집니다.",
                    "max_limit": 300000
                  },
                  "options": [
                    {"intr_rate_type": "S", "intr_rate_type_nm": "단리", "rsrv_type": "F", "rsrv_type_nm": "자유적립식", "save_trm": "12", "intr_rate": 3.1, "intr_rate2": 4.1}
                  ]
                }
                """, ProductType.SAVING);

        ProductDraft draft = normalizer.normalize(raw);

        assertThat(draft.joinMethod()).isEqualTo("영업점,인터넷,스마트폰");
        assertThat(draft.eligibilityText()).isEqualTo("실명의 개인");
        assertThat(draft.cautionText()).isEqualTo("만기 후 이자율은 기본이율의 50%로 낮아집니다.");
        assertThat(draft.properties().getFirst().reserveType()).isEqualTo("F");
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
