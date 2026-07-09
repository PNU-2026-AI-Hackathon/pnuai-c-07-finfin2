package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductKeyword;
import apptive.fin.search.entity.ProductPreferentialRate;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.provider.entity.Provider;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.service.MatchScoreService;
import apptive.fin.search.service.RateCalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * PRD(Y3-2 필터링-정렬 로직 명세) 문서의 수치 예시를 그대로 재현하는 골든 테스트.
 */
class PrdGoldenScenarioTest {

    private final MatchScoreService matchScoreService = new MatchScoreService(null);
    private final RateCalculatorService rateCalculatorService = new RateCalculatorService();

    // ===== 1. 탭A 정부 상품 골든 시나리오 =====

    @Test
    void 정부상품_탭A_선택혜택_2개_신분_기간_납입_구성() {
        // User selects 3 core benefits: BENEFIT_GOV_SUBSIDY, BENEFIT_TAX_FREE, BENEFIT_MAX_INTEREST
        // Property keywords contain only BENEFIT_GOV_SUBSIDY, BENEFIT_TAX_FREE (2/3)
        // benefitScore = 40 × 2/3 = 26.6667
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "정책기관",
                300_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.BENEFIT_TAX_FREE,
                KeywordValueEnum.STATUS_PART_TIME
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(500_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_PART_TIME),
                        KeywordValueEnum.TERM_2_TO_3_YEARS,
                        List.of(
                                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                                KeywordValueEnum.BENEFIT_TAX_FREE,
                                KeywordValueEnum.BENEFIT_MAX_INTEREST
                        ),
                        List.of()
                )
        );

        // benefitScore = 40 × (2/3) = 26.6667
        assertThat(result.benefitScore()).isCloseTo(26.6667, offset(0.0001));

        // periodScore = 22 × 0.5 (adjacent: saveTrm=12, selected=TERM_2_TO_3_YEARS range 24-36)
        assertThat(result.periodScore()).isCloseTo(11.0, offset(0.0001));

        // identityScore = 20 × 0.5 (general identity, non-specialized)
        assertThat(result.identityScore()).isCloseTo(10.0, offset(0.0001));

        // depositScore = 18 × (300000/500000) = 18 × 0.6 = 10.8
        assertThat(result.depositScore()).isCloseTo(10.8, offset(0.0001));

        // bankCondScore = 0 for government products
        assertThat(result.bankCondScore()).isZero();

        // totalScore = 26.6667 + 11.0 + 10.0 + 10.8 + 0 = 58.4667
        assertThat(result.totalScore()).isCloseTo(58.4667, offset(0.0001));
    }

    // ===== 2. 탭A 은행 상품 골든 시나리오 =====

    @Test
    void 은행상품_탭A_모든선택_은행조건_혜택_기간_신분_납입() {
        // Selected bank conditions: BANK_AUTO_TRANSFER, BANK_SALARY_TRANSFER
        // Property tagged: BANK_SALARY_TRANSFER (1/2) → bankCondScore = 40 × 1/2 = 20.0
        // Selected benefits: BENEFIT_MAX_INTEREST, BENEFIT_GOV_SUBSIDY
        // Property tagged: BENEFIT_MAX_INTEREST only (1/1) → benefitScore = 20 × 1/1 = 20.0
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_MAX_INTEREST,
                KeywordValueEnum.BANK_SALARY_TRANSFER,
                KeywordValueEnum.STATUS_SME_WORKER
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_SME_WORKER),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_MAX_INTEREST, KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of(KeywordValueEnum.BANK_AUTO_TRANSFER, KeywordValueEnum.BANK_SALARY_TRANSFER)
                )
        );

        // bankCondScore = 40 × 1/2 = 20.0
        assertThat(result.bankCondScore()).isCloseTo(20.0, offset(0.0001));

        // benefitScore = 20 × 1/1 = 20.0 (GOV_SUBSIDY not applicable for bank)
        assertThat(result.benefitScore()).isCloseTo(20.0, offset(0.0001));

        // periodScore = 20 × 1.0 (exact match: saveTrm=12, selected=TERM_AROUND_1_YEAR range 6-12)
        assertThat(result.periodScore()).isCloseTo(20.0, offset(0.0001));

        // depositScore = 15 × 1.0 (300000 ≤ 500000)
        assertThat(result.depositScore()).isCloseTo(15.0, offset(0.0001));

        // identityScore = 5 × 1.0 (STATUS_SME_WORKER matches)
        assertThat(result.identityScore()).isCloseTo(5.0, offset(0.0001));

        // totalScore = 20.0 + 20.0 + 20.0 + 15.0 + 5.0 = 80.0
        assertThat(result.totalScore()).isCloseTo(80.0, offset(0.0001));
    }

    // ===== 3. 탭B 은행 달성가능금리 PRD B-2 예시 =====

    @Test
    void 은행상품_탭B_달성가능금리_급여이체_첫거래_온라인가입() {
        // baseRate "3.50", maxRate "6.00"
        // preferential rates:
        //   BANK_SALARY_TRANSFER 0.30 (selected)
        //   BANK_CARD_USAGE 0.20 (not selected)
        //   BANK_FIRST_TRANSACTION 0.50 (user never used this bank)
        //   BANK_ONLINE_JOIN 0.10 (automatic)
        // expected achievableRate = 3.5 + 0.3 + 0.5 + 0.1 = 4.4

        Product product = createProduct("BANK_PRD_B2", "달성가능금리", "FSS");
        ProductProperty property = createProperty(
                10L,
                "0010927",
                "KB국민은행",
                "3.50",
                "6.00"
        );

        addPreferentialRates(property,
                preferentialRate(KeywordValueEnum.BANK_SALARY_TRANSFER, "0.30"),
                preferentialRate(KeywordValueEnum.BANK_CARD_USAGE, "0.20"),
                preferentialRate(KeywordValueEnum.BANK_FIRST_TRANSACTION, "0.50"),
                preferentialRate(KeywordValueEnum.BANK_ONLINE_JOIN, "0.10")
        );

        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(null, List.of("0010927"), List.of()),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        null,
                        List.of(),
                        List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)
                )
        );

        assertThat(result.baseRate()).isEqualTo(3.5);
        // 3.5 + 0.3 (급여이체) + 0.5 (첫거래) + 0.1 (온라인가입) = 4.4
        assertThat(result.achievableRate()).isCloseTo(4.4, offset(0.0001));
    }

    // ===== 4. 탭B 정부 기여금 환산 수익률 골든 =====

    @Test
    void 정부상품_탭B_정률_1점0배_24개월_50점0수익률() {
        // 기쁨두배 2년
        Product product = createProduct("GOV_001", "기쁨두배", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(property, "govMatchingRatio", new BigDecimal("1.0000"));
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 24);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()));

        assertThat(result.achievableRate()).isCloseTo(50.0, offset(0.0001));
    }

    @Test
    void 정부상품_탭B_정률_1점0배_12개월_100점0수익률() {
        // 대구
        Product product = createProduct("GOV_002", "대구", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(property, "govMatchingRatio", new BigDecimal("1.0000"));
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 12);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()));

        assertThat(result.achievableRate()).isCloseTo(100.0, offset(0.0001));
    }

    @Test
    void 정부상품_탭B_정률_1점0배_10개월_120점0수익률() {
        // 광주13
        Product product = createProduct("GOV_003", "광주13", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(property, "govMatchingRatio", new BigDecimal("1.0000"));
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 10);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()));

        assertThat(result.achievableRate()).isCloseTo(120.0, offset(0.0001));
    }

    @Test
    void 정부상품_탭B_정률_1점5배_36개월_50점0수익률() {
        // 함안
        Product product = createProduct("GOV_004", "함안", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(property, "govMatchingRatio", new BigDecimal("1.5000"));
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 36);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()));

        assertThat(result.achievableRate()).isCloseTo(50.0, offset(0.0001));
    }

    @Test
    void 정부상품_탭B_정률_퍼센트_36개월_일반우대() {
        // 청년미래적금: 일반 0.06 (2.0), 우대 0.12 (4.0)
        Product product = createProduct("GOV_PERCENT", "청년미래적금", "ONTONG");
        ProductProperty general = createProperty(10L, "GOV", "정책기관", null, null);
        ProductProperty preferential = createProperty(11L, "GOV", "정책기관", null, null);

        ReflectionTestUtils.setField(general, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(general, "govMatchingRatio", new BigDecimal("0.0600"));
        ReflectionTestUtils.setField(general, "govContributionPeriodMonths", 36);

        ReflectionTestUtils.setField(preferential, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(preferential, "govMatchingRatio", new BigDecimal("0.1200"));
        ReflectionTestUtils.setField(preferential, "govContributionPeriodMonths", 36);

        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(general, preferential)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()));

        // 우대 버전(11L)이 선택됨 (4.0 > 2.0)
        assertThat(result.productPropertyId()).isEqualTo(11L);
        assertThat(result.achievableRate()).isCloseTo(4.0, offset(0.0001));
    }

    @Test
    void 정부상품_탭B_정액_월_300000_36개월_희망100000_100점0() {
        // 청년내일저축: goal 100000
        Product product = createProduct("GOV_005", "청년내일저축", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.FIXED_AMOUNT);
        ReflectionTestUtils.setField(property, "govMonthlyFixedContribution", 300_000L);
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 36);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(100_000L),
                new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of())
        );

        assertThat(result.achievableRate()).isCloseTo(100.0, offset(0.0001));
    }

    @Test
    void 정부상품_탭B_정액_월_300000_36개월_희망300000_33점3333() {
        // 청년내일저축: goal 300000
        Product product = createProduct("GOV_006", "청년내일저축-희망300000", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.FIXED_AMOUNT);
        ReflectionTestUtils.setField(property, "govMonthlyFixedContribution", 300_000L);
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 36);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of())
        );

        assertThat(result.achievableRate()).isCloseTo(33.3333, offset(0.0001));
    }

    // ===== 5. 재배분 불변식: 만점 조건 상품의 다양한 선택 조합 =====

    /**
     * 정부상품 만점 조건: 모든 선택 키워드가 태깅, saveTrm 일치, goal ≤ limit
     * 선택 조합별 totalScore = 100.0 검증
     */
    @Test
    void 정부상품_재배분_혜택만_선택_100점() {
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "정책기관",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.BENEFIT_TAX_FREE,
                KeywordValueEnum.BENEFIT_MAX_INTEREST
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        null,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY, KeywordValueEnum.BENEFIT_TAX_FREE, KeywordValueEnum.BENEFIT_MAX_INTEREST),
                        List.of()
                )
        );

        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.0001));
    }

    @Test
    void 정부상품_재배분_혜택_기간_선택_100점() {
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "정책기관",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.BENEFIT_TAX_FREE
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY, KeywordValueEnum.BENEFIT_TAX_FREE),
                        List.of()
                )
        );

        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.0001));
    }

    @Test
    void 정부상품_재배분_혜택_기간_신분_선택_100점() {
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "정책기관",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.BENEFIT_TAX_FREE,
                KeywordValueEnum.STATUS_MILITARY
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY, KeywordValueEnum.BENEFIT_TAX_FREE),
                        List.of()
                )
        );

        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.0001));
    }

    @Test
    void 정부상품_재배분_기간_신분_선택_100점() {
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "정책기관",
                500_000L,
                12,
                KeywordValueEnum.STATUS_MILITARY
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(),
                        List.of()
                )
        );

        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.0001));
    }

    @Test
    void 정부상품_재배분_신분만_선택_100점() {
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "정책기관",
                500_000L,
                12,
                KeywordValueEnum.STATUS_MILITARY
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        null,
                        List.of(),
                        List.of()
                )
        );

        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.0001));
    }

    /**
     * 은행상품 만점 조건: 모든 선택 키워드가 태깅, saveTrm 일치, goal ≤ limit
     * 선택 조합별 totalScore = 100.0 검증
     */
    @Test
    void 은행상품_재배분_은행거래만_선택_100점() {
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BANK_CARD_USAGE
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        null,
                        List.of(),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                )
        );

        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.0001));
    }

    @Test
    void 은행상품_재배분_은행거래_혜택_기간_신분_선택_100점() {
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BANK_CARD_USAGE
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                )
        );

        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.0001));
    }

    @Test
    void 은행상품_재배분_혜택만_MAX_INTEREST_선택_100점() {
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_MAX_INTEREST
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        null,
                        List.of(KeywordValueEnum.BENEFIT_MAX_INTEREST),
                        List.of()
                )
        );

        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.0001));
    }

    @Test
    void 은행상품_재배분_기간만_선택_100점() {
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(),
                        List.of()
                )
        );

        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.0001));
    }

    // ===== Helper methods (same pattern as MatchScoreServiceTest & RateCalculatorServiceTest) =====

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

    private Product createProduct(String code, String name, String sourceCode) {
        ProductSource source = new ProductSource();
        ReflectionTestUtils.setField(source, "code", sourceCode);

        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "productCode", code);
        ReflectionTestUtils.setField(product, "productName", name);
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "type", ProductType.SAVING);
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

    private ProductProperty createProperty(Long id, String providerCode, String providerName, String baseRate, String maxRate) {
        ProductProperty property = new ProductProperty();
        Provider provider = new Provider();
        ReflectionTestUtils.setField(provider, "code", providerCode);
        ReflectionTestUtils.setField(provider, "name", providerName);
        ReflectionTestUtils.setField(property, "id", id);
        ReflectionTestUtils.setField(property, "provider", provider);
        ReflectionTestUtils.setField(property, "keywords", new ArrayList<>());
        ReflectionTestUtils.setField(property, "preferentialRates", new ArrayList<>());
        if (baseRate != null) {
            ReflectionTestUtils.setField(property, "baseRate", new BigDecimal(baseRate));
        }
        if (maxRate != null) {
            ReflectionTestUtils.setField(property, "maxRate", new BigDecimal(maxRate));
        }
        return property;
    }

    private void addKeyword(ProductProperty property, KeywordValueEnum keywordValue) {
        ProductKeyword keyword = new ProductKeyword();
        ReflectionTestUtils.setField(keyword, "keywordCode", keywordValue);
        List<ProductKeyword> keywords = new ArrayList<>(
                (List<ProductKeyword>) ReflectionTestUtils.getField(property, "keywords")
        );
        keywords.add(keyword);
        ReflectionTestUtils.setField(property, "keywords", keywords);
    }

    private ProductPreferentialRate preferentialRate(KeywordValueEnum keyword, String rate) {
        ProductPreferentialRate preferentialRate = new ProductPreferentialRate();
        ReflectionTestUtils.setField(preferentialRate, "keywordCode", keyword);
        ReflectionTestUtils.setField(preferentialRate, "rate", new BigDecimal(rate));
        return preferentialRate;
    }

    private void addPreferentialRates(ProductProperty property, ProductPreferentialRate... preferentialRates) {
        List<ProductPreferentialRate> rates = new ArrayList<>(List.of(preferentialRates));
        rates.forEach(rate -> ReflectionTestUtils.setField(rate, "productProperty", property));
        ReflectionTestUtils.setField(property, "preferentialRates", rates);
    }

    private SearchRequestDto createRequest() {
        return createRequest(null);
    }

    private SearchRequestDto createRequest(Long monthlySavingsGoal) {
        return createRequest(monthlySavingsGoal, List.of(), List.of());
    }

    private SearchRequestDto createRequest(
            Long monthlySavingsGoal,
            List<String> neverUsedBanks,
            List<String> maturedSavingBanks
    ) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        monthlySavingsGoal,
                        null,
                        neverUsedBanks,
                        maturedSavingBanks,
                        List.of()
                )
        );
    }
}
