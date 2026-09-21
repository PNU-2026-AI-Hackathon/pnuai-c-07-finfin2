package apptive.fin.search;

import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductRequiredKeyword;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.search.enums.ExtractionConfidence;
import apptive.fin.search.enums.RequiredKeywordEffect;
import apptive.fin.provider.entity.Provider;
import apptive.fin.search.service.MatchScoreService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * V2 가중치 테스트 (PRD 개정: 4축 - 고특성30 + 균등20).
 *
 * <p>V2 SAW 가중치 체계:</p>
 * <h3>정부상품:</h3>
 * <ul>
 *   <li><b>핵심 혜택 (benefits)</b>: 30점 - #정부기여금, #비과세 매칭</li>
 *   <li><b>현재 신분 (identity)</b>: 30점 - 신분특화 만점 / 신분포함 절반</li>
 *   <li><b>납입 한도 (deposit)</b>: 20점 - 납입한도 충족 비율</li>
 *   <li><b>저축 기간 (period)</b>: 20점 - 저축기간 정확 매칭</li>
 * </ul>
 *
 * <h3>은행상품:</h3>
 * <ul>
 *   <li><b>은행 거래 (bankCond)</b>: 30점 - 우대조건 매칭</li>
 *   <li><b>핵심 혜택 (benefits)</b>: 30점 - #최고이율_중심, #우대조건_간편 매칭</li>
 *   <li><b>납입 한도 (deposit)</b>: 20점 - 납입한도 충족 비율</li>
 *   <li><b>저축 기간 (period)</b>: 20점 - 저축기간 정확 매칭</li>
 * </ul>
 *
 * @see MatchScoreServiceTest V1 레거시 테스트 (비활성화됨)
 * @see apptive.fin.search.enums.ScoreWeightEnum 가중치 정의
 */
class MatchScoreServiceV2Test {

    @Test
    void V2_은행상품_모든_축이_만점이면_총점_100이다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.BANK_SALARY_TRANSFER
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)
                ),
                false
        );

        // V2 은행: 은행거래30 + 핵심혜택30 + 납입20 + 기간20 = 100
        assertThat(result.bankCondScore()).isCloseTo(30.0, offset(0.001));
        assertThat(result.benefitScore()).isCloseTo(30.0, offset(0.001));
        assertThat(result.depositScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.periodScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.identityScore()).isZero();  // 은행은 신분특화 미적용
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void V2_정부상품_모든_축이_만점이면_총점_100이다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("ONTONG", createPropertyWithIdentity(
                10L,
                "policy-provider",
                500_000L,
                12,
                KeywordValueEnum.STATUS_MILITARY,  // 신분특화 키워드
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),  // 신분 선택
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of()
                ),
                false
        );

        // V2 정부: 핵심혜택30 + 현재신분30 + 납입20 + 기간20 = 100
        assertThat(result.benefitScore()).isCloseTo(30.0, offset(0.001));
        assertThat(result.identityScore()).isCloseTo(30.0, offset(0.001));  // 신분특화 만점
        assertThat(result.depositScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.periodScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.bankCondScore()).isZero();  // 정부는 은행거래 미적용
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void V2_정부상품_신분포함이면_절반점수() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("ONTONG", createPropertyWithIdentity(
                10L,
                "policy-provider",
                500_000L,
                12,
                KeywordValueEnum.STATUS_SME_WORKER,  // 신분포함 키워드 (특화 아님)
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_SME_WORKER),  // 신분 선택
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of()
                ),
                false
        );

        // 신분포함은 절반 점수 (30 * 0.5 = 15)
        assertThat(result.identityScore()).isCloseTo(15.0, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(85.0, offset(0.001));  // 30 + 15 + 20 + 20
    }

    @Test
    void V2_은행상품_혜택이_없으면_재배분된다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BANK_SALARY_TRANSFER
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(),  // 혜택 없음
                        List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)
                ),
                false
        );

        // 혜택 30 제거 → 은행거래30 + 납입20 + 기간20 = 70 → 재배분 후 100
        // 각 축의 비율: 30/70, 20/70, 20/70 → 42.86, 28.57, 28.57
        assertThat(result.benefitScore()).isZero();
        assertThat(result.bankCondScore()).isCloseTo(42.857, offset(0.01));
        assertThat(result.depositScore()).isCloseTo(28.571, offset(0.01));
        assertThat(result.periodScore()).isCloseTo(28.571, offset(0.01));
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void V2_기간이_불일치하면_기간점수가_0이다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                24,  // 24개월 상품
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.BANK_SALARY_TRANSFER
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_12_MONTH,  // 12개월 선택 → 불일치
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)
                ),
                false
        );

        // 기간 불일치 → 기간점수 0 → 은행거래 + 혜택 + 납입만 배점
        assertThat(result.periodScore()).isZero();
        assertThat(result.totalScore()).isCloseTo(80.0, offset(0.001));  // 30 + 30 + 20
    }

    @Test
    void V2_납입한도_초과시_비율만큼_감점된다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                150_000L,  // 한도 15만
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.BANK_SALARY_TRANSFER
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),  // 희망 30만 → 한도의 50%
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)
                ),
                false
        );

        // 납입: 150000/300000 = 0.5 → 20 * 0.5 = 10
        assertThat(result.depositScore()).isCloseTo(10.0, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(90.0, offset(0.001));  // 30 + 30 + 20 + 10
    }

    @Test
    void V2_은행상품_은행조건_미선택시_재배분() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.BANK_SALARY_TRANSFER
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of()  // 은행조건 미선택
                ),
                false
        );

        // 은행조건 30 제거 → 혜택30 + 납입20 + 기간20 = 70 → 재배분 후 100
        assertThat(result.bankCondScore()).isZero();
        assertThat(result.benefitScore()).isCloseTo(42.857, offset(0.01));
        assertThat(result.depositScore()).isCloseTo(28.571, offset(0.01));
        assertThat(result.periodScore()).isCloseTo(28.571, offset(0.01));
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void V2_정부상품_신분_미선택시_재배분() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("ONTONG", createPropertyWithIdentity(
                10L,
                "policy-provider",
                500_000L,
                12,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),  // 신분 미선택
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of()
                ),
                false
        );

        // 신분 30 제거 → 혜택30 + 납입20 + 기간20 = 70 → 재배분 후 100
        assertThat(result.identityScore()).isZero();
        assertThat(result.benefitScore()).isCloseTo(42.857, offset(0.01));
        assertThat(result.depositScore()).isCloseTo(28.571, offset(0.01));
        assertThat(result.periodScore()).isCloseTo(28.571, offset(0.01));
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void V2_단기예치_1개월_정확매칭() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                1,  // 1개월 상품
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.BANK_SALARY_TRANSFER
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_1_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)
                ),
                false
        );

        assertThat(result.periodScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    // === 헬퍼 메서드 ===

    private Product createProduct(String sourceCode, ProductProperty property) {
        ProductSource source = new ProductSource();
        Product product = new Product();

        ReflectionTestUtils.setField(source, "code", sourceCode);
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "productName", "test-product");
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));
        return product;
    }

    private ProductProperty createProperty(Long id, String providerName, Long maxMonthlyLimit) {
        Provider provider = new Provider();
        ProductProperty property = new ProductProperty();

        ReflectionTestUtils.setField(provider, "name", providerName);
        ReflectionTestUtils.setField(property, "id", id);
        ReflectionTestUtils.setField(property, "provider", provider);
        ReflectionTestUtils.setField(property, "maxMonthlyLimit", maxMonthlyLimit);
        ReflectionTestUtils.setField(property, "keywords", new ArrayList<>());
        ReflectionTestUtils.setField(property, "preferentialRates", new ArrayList<>());
        ReflectionTestUtils.setField(property, "requiredKeywords", new ArrayList<>());
        return property;
    }

    private ProductProperty createProperty(
            Long id,
            String providerName,
            Long maxMonthlyLimit,
            Integer saveTrm,
            KeywordValueEnum... keywords
    ) {
        ProductProperty property = createProperty(id, providerName, maxMonthlyLimit);
        ReflectionTestUtils.setField(property, "saveTrm", saveTrm);
        for (KeywordValueEnum keyword : keywords) {
            addKeyword(property, keyword);
        }
        return property;
    }

    private ProductProperty createPropertyWithIdentity(
            Long id,
            String providerName,
            Long maxMonthlyLimit,
            Integer saveTrm,
            KeywordValueEnum identityKeyword,
            KeywordValueEnum... otherKeywords
    ) {
        ProductProperty property = createProperty(id, providerName, maxMonthlyLimit);
        ReflectionTestUtils.setField(property, "saveTrm", saveTrm);
        // 신분 키워드 추가 (특화 키워드로 처리)
        addKeyword(property, identityKeyword);
        for (KeywordValueEnum keyword : otherKeywords) {
            addKeyword(property, keyword);
        }
        return property;
    }

    private void addKeyword(ProductProperty property, KeywordValueEnum keywordValue) {
        if (keywordValue.isPreferentialRate()) {
            // 우대금리 키워드는 preferentialRates에 추가
            addPreferentialRate(property, keywordValue);
            return;
        }

        if (keywordValue.isRequired()) {
            // 신분 키워드는 requiredKeywords에 추가
            addRequiredKeyword(property, keywordValue);
            return;
        }

        apptive.fin.search.entity.ProductKeyword keyword = new apptive.fin.search.entity.ProductKeyword();
        ReflectionTestUtils.setField(keyword, "keywordCode", keywordValue);
        List<apptive.fin.search.entity.ProductKeyword> keywords = new ArrayList<>(
                (List<apptive.fin.search.entity.ProductKeyword>) ReflectionTestUtils.getField(property, "keywords")
        );
        keywords.add(keyword);
        ReflectionTestUtils.setField(property, "keywords", keywords);
    }

    private void addRequiredKeyword(ProductProperty property, KeywordValueEnum keywordValue) {
        ProductRequiredKeyword required = new ProductRequiredKeyword();
        ReflectionTestUtils.setField(required, "keywordCode", keywordValue);
        ReflectionTestUtils.setField(required, "effect", RequiredKeywordEffect.REQUIRE);
        ReflectionTestUtils.setField(required, "confidence", ExtractionConfidence.HIGH);
        List<ProductRequiredKeyword> requiredKeywords = new ArrayList<>(
                (List<ProductRequiredKeyword>) ReflectionTestUtils.getField(property, "requiredKeywords")
        );
        requiredKeywords.add(required);
        ReflectionTestUtils.setField(property, "requiredKeywords", requiredKeywords);
    }

    private void addPreferentialRate(ProductProperty property, KeywordValueEnum keywordValue) {
        apptive.fin.search.entity.ProductPreferentialRate rate = new apptive.fin.search.entity.ProductPreferentialRate();
        ReflectionTestUtils.setField(rate, "keywordCode", keywordValue);
        ReflectionTestUtils.setField(rate, "rate", java.math.BigDecimal.valueOf(0.5));
        List<apptive.fin.search.entity.ProductPreferentialRate> rates = new ArrayList<>(
                (List<apptive.fin.search.entity.ProductPreferentialRate>) ReflectionTestUtils.getField(property, "preferentialRates")
        );
        rates.add(rate);
        ReflectionTestUtils.setField(property, "preferentialRates", rates);
    }

    private SearchRequestDto createRequest(Long monthlySavingsGoal) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, monthlySavingsGoal,
                        null, null,
                        null, null, List.of()
                )
        );
    }
}
