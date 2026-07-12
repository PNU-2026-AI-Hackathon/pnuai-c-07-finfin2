package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductKeyword;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.provider.entity.Provider;
import apptive.fin.search.service.MatchScoreService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class MatchScoreServiceTest {

    private static final String KB_PROVIDER_CODE = "0010927";

    @Test
    void 월저축목표가_null이어도_예외없이_저축액점수를_제외한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = new Product();
        ProductSource source = new ProductSource();
        Provider provider = new Provider();
        ProductProperty property = new ProductProperty();

        ReflectionTestUtils.setField(source, "code", "FSS");
        ReflectionTestUtils.setField(provider, "name", "테스트은행");
        ReflectionTestUtils.setField(property, "id", 10L);
        ReflectionTestUtils.setField(property, "provider", provider);
        ReflectionTestUtils.setField(property, "maxMonthlyLimit", 500_000L);
        ReflectionTestUtils.setField(property, "keywords", new ArrayList<>());
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "productName", "청년우대적금");
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, null, null, List.of()
                )
        );

        // List → 단일 반환으로 변경
        ProductMatchDto result = matchScoreService.score(product, product.getProperties().get(0), request, new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()), false);

        assertThat(result.depositScore()).isZero();
        assertThat(result.productPropertyId()).isEqualTo(10L);
        assertThat(result.providerName()).isEqualTo("테스트은행");
    }

    private ProductProperty createProperty(Long id, String providerName, Long maxMonthlyLimit) {
        Provider provider = new Provider();
        ProductProperty property = new ProductProperty();

        ReflectionTestUtils.setField(provider, "name", providerName);
        ReflectionTestUtils.setField(property, "id", id);
        ReflectionTestUtils.setField(property, "provider", provider);
        ReflectionTestUtils.setField(property, "maxMonthlyLimit", maxMonthlyLimit);
        ReflectionTestUtils.setField(property, "keywords", new ArrayList<>());
        return property;
    }

    @Test
    void 신분을_선택하지_않으면_신분_배점을_은행상품_유효항목에_재배분한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.BANK_CARD_USAGE
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.identityScore()).isZero();
        assertThat(result.totalScore()).isCloseTo(100.0, org.assertj.core.data.Offset.offset(0.001));
    }

    @Test
    void 은행상품에_해당하지_않는_혜택은_제외하고_배점을_재배분한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BANK_CARD_USAGE
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.benefitScore()).isZero();
        assertThat(result.bankCondScore()).isCloseTo(50.0, offset(0.001));
        assertThat(result.periodScore()).isCloseTo(25.0, offset(0.001));
        assertThat(result.depositScore()).isCloseTo(18.75, offset(0.001));
        assertThat(result.identityScore()).isCloseTo(6.25, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(100.0, org.assertj.core.data.Offset.offset(0.001));
    }

    @Test
    void 신분_기간_혜택을_선택하지_않으면_은행조건과_납입에_비례_재배분한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BANK_CARD_USAGE
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        null,
                        List.of(),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.benefitScore()).isZero();
        assertThat(result.periodScore()).isZero();
        assertThat(result.identityScore()).isZero();
        assertThat(result.bankCondScore()).isCloseTo(72.7273, offset(0.001));
        assertThat(result.depositScore()).isCloseTo(27.2727, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void 은행상품은_기간이_인접구간이면_기간점수를_절반만_부여한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                24,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BANK_CARD_USAGE
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.periodScore()).isCloseTo(10.0, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(90.0, offset(0.001));
    }

    @Test
    void 은행상품은_희망납입액이_한도를_초과하면_비율만큼_납입점수를_감점한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                150_000L,
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BANK_CARD_USAGE
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.depositScore()).isCloseTo(7.5, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(92.5, offset(0.001));
    }

    @Test
    void 은행상품은_은행조건_여러개중_일치한_비율만큼_점수를_부여한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BANK_SALARY_TRANSFER
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_SALARY_TRANSFER, KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.bankCondScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(80.0, offset(0.001));
    }

    @Test
    void 정부상품은_은행조건을_제외하고_배점을_재배분한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "policy-provider",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BANK_CARD_USAGE,
                KeywordValueEnum.INTEREST_SAVINGS
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.bankCondScore()).isZero();
        assertThat(result.totalScore()).isCloseTo(100.0, org.assertj.core.data.Offset.offset(0.001));
    }

    @Test
    void 정부상품은_MVP_배점을_사용하고_은행조건을_무시한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "policy-provider",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BANK_CARD_USAGE
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.benefitScore()).isCloseTo(40.0, offset(0.001));
        assertThat(result.periodScore()).isCloseTo(22.0, offset(0.001));
        assertThat(result.identityScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.depositScore()).isCloseTo(18.0, offset(0.001));
        assertThat(result.bankCondScore()).isZero();
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void 정부상품은_일반_신분_키워드가_일치하면_신분점수를_절반만_부여한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "policy-provider",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.STATUS_PART_TIME
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_PART_TIME),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of()
                ),
                false
        );

        assertThat(result.identityScore()).isCloseTo(10.0, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(90.0, offset(0.001));
    }

    @Test
    void 은행상품은_모든_선택항목이_일치하면_MVP_배점을_그대로_사용한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
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
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.bankCondScore()).isCloseTo(40.0, offset(0.001));
        assertThat(result.benefitScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.periodScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.depositScore()).isCloseTo(15.0, offset(0.001));
        assertThat(result.identityScore()).isCloseTo(5.0, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void 정부상품은_은행_제공기관이어도_은행조건_점수를_반영하지_않는다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(
                10L,
                "KB",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.STATUS_MILITARY,
                KeywordValueEnum.BANK_CARD_USAGE,
                KeywordValueEnum.INTEREST_SAVINGS
        );
        setProviderCode(property, "KB");
        Product product = createProduct("ONTONG", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of(KeywordValueEnum.BANK_CARD_USAGE)
                ),
                false
        );

        assertThat(result.bankCondScore()).isZero();
        assertThat(result.totalScore()).isCloseTo(100.0, org.assertj.core.data.Offset.offset(0.001));
    }

    @Test
    void 거래이력_반영이_켜져_있으면_탭A에_첫거래_조건을_반영한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(
                10L,
                "KB",
                500_000L,
                12,
                KeywordValueEnum.BANK_FIRST_TRANSACTION
        );
        setProviderCode(property, "KB");
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L, List.of("KB"), List.of()),
                new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()),
                true
        );

        assertThat(result.bankCondScore()).isGreaterThan(0.0);
    }

    @Test
    void 거래이력_반영이_켜져_있으면_탭A에_재예치_조건을_반영한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(
                10L,
                "KB",
                500_000L,
                12,
                KeywordValueEnum.BANK_REDEPOSIT
        );
        setProviderCode(property, "KB");
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L, List.of(), List.of("KB")),
                new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()),
                true
        );

        assertThat(result.bankCondScore()).isGreaterThan(0.0);
    }

    @Test
    void 거래이력_반영이_꺼져_있으면_탭A에_첫거래_조건을_반영하지_않는다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(
                10L,
                "KB",
                500_000L,
                12,
                KeywordValueEnum.BANK_FIRST_TRANSACTION
        );
        setProviderCode(property, "KB");
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L, List.of("KB"), List.of()),
                new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()),
                false
        );

        assertThat(result.bankCondScore()).isZero();
    }

    @Test
    void 첫거래_거래이력은_선택한_은행에만_매칭된다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(
                10L,
                "KB",
                500_000L,
                12,
                KeywordValueEnum.BANK_FIRST_TRANSACTION
        );
        setProviderCode(property, "KB");
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L, List.of("SHINHAN"), List.of()),
                new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()),
                true
        );

        assertThat(result.bankCondScore()).isZero();
    }

    @Test
    void 거래이력은_provider_code로만_매칭된다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(
                10L,
                "국민은행",
                500_000L,
                12,
                KeywordValueEnum.BANK_FIRST_TRANSACTION
        );
        setProviderCode(property, KB_PROVIDER_CODE);
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L, List.of(KB_PROVIDER_CODE), List.of()),
                new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()),
                true
        );

        assertThat(result.bankCondScore()).isGreaterThan(0.0);
    }

    @Test
    void 거래이력은_provider_별칭으로_매칭되지_않는다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(
                10L,
                "국민은행",
                500_000L,
                12,
                KeywordValueEnum.BANK_FIRST_TRANSACTION
        );
        setProviderCode(property, KB_PROVIDER_CODE);
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L, List.of("KB"), List.of()),
                new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()),
                true
        );

        assertThat(result.bankCondScore()).isZero();
    }

    // ===== [D] 월 납입액 null 시 납입한도 배점 재배분 =====

    @Test
    void 월저축목표가_null이면_납입한도_배점을_활성차원에_재배분해_만점이_100이_된다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        // 정부 상품: 혜택+기간+신분 만점 매칭, 월납입 null → 납입한도(18) 재배분되어야 총점 100
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "정책기관",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY,
                KeywordValueEnum.STATUS_MILITARY
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(null),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),
                        KeywordValueEnum.TERM_AROUND_1_YEAR,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of()
                ),
                false
        );

        assertThat(result.depositScore()).isZero();
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    // ===== [E] 첫거래/재예치는 거래이력 은행이 있을 때만 매칭 =====

    @Test
    void 첫거래는_거래이력_은행이_비어있으면_탭A에서_매칭되지_않는다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(
                10L,
                "KB",
                500_000L,
                12,
                KeywordValueEnum.BANK_FIRST_TRANSACTION
        );
        setProviderCode(property, "KB");
        Product product = createProduct("FSS", property);

        // 첫거래가 은행거래 조건으로 들어왔지만 거래이력(neverUsedBanks)이 비어 있음 → 매칭 불인정
        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L, List.of(), List.of()),
                new ResolvedKeywords(
                        List.of(), List.of(), null,
                        List.of(),
                        List.of(KeywordValueEnum.BANK_FIRST_TRANSACTION)
                ),
                true
        );

        assertThat(result.bankCondScore()).isZero();
    }

    @Test
    void 재예치는_거래이력_은행이_비어있으면_탭A에서_매칭되지_않는다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(
                10L,
                "KB",
                500_000L,
                12,
                KeywordValueEnum.BANK_REDEPOSIT
        );
        setProviderCode(property, "KB");
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L, List.of(), List.of()),
                new ResolvedKeywords(
                        List.of(), List.of(), null,
                        List.of(),
                        List.of(KeywordValueEnum.BANK_REDEPOSIT)
                ),
                true
        );

        assertThat(result.bankCondScore()).isZero();
    }

    // ===== [A] 은행 #최고이율_중심 상위 30% 동적 판정 =====

    @Test
    void 은행_최고이율은_임계금리_이상이면_동적으로_매칭된다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(10L, "KB", 500_000L, 12);
        ReflectionTestUtils.setField(property, "maxRate", new java.math.BigDecimal("5.00"));
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                property,
                createRequest(300_000L),
                new ResolvedKeywords(List.of(), List.of(), null,
                        List.of(KeywordValueEnum.BENEFIT_MAX_INTEREST), List.of()),
                false,
                4.0
        );

        assertThat(result.benefitScore()).isGreaterThan(0.0);
    }

    @Test
    void 은행_최고이율은_임계금리_미만이면_매칭되지_않는다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        ProductProperty property = createProperty(10L, "KB", 500_000L, 12);
        ReflectionTestUtils.setField(property, "maxRate", new java.math.BigDecimal("3.00"));
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                property,
                createRequest(300_000L),
                new ResolvedKeywords(List.of(), List.of(), null,
                        List.of(KeywordValueEnum.BENEFIT_MAX_INTEREST), List.of()),
                false,
                4.0
        );

        assertThat(result.benefitScore()).isZero();
    }

    @Test
    void 임계값이_null이면_최고이율은_정적태그_방식으로_판정한다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        // 태그는 있으나 금리는 낮음 → 임계값 미제공 시 태그로 매칭
        ProductProperty property = createProperty(10L, "KB", 500_000L, 12,
                KeywordValueEnum.BENEFIT_MAX_INTEREST);
        ReflectionTestUtils.setField(property, "maxRate", new java.math.BigDecimal("1.00"));
        Product product = createProduct("FSS", property);

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(List.of(), List.of(), null,
                        List.of(KeywordValueEnum.BENEFIT_MAX_INTEREST), List.of()),
                false
        );

        assertThat(result.benefitScore()).isGreaterThan(0.0);
    }

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

    private void addKeyword(ProductProperty property, KeywordValueEnum keywordValue) {
        ProductKeyword keyword = new ProductKeyword();
        ReflectionTestUtils.setField(keyword, "keywordCode", keywordValue);
        List<ProductKeyword> keywords = new ArrayList<>(
                (List<ProductKeyword>) ReflectionTestUtils.getField(property, "keywords")
        );
        keywords.add(keyword);
        ReflectionTestUtils.setField(property, "keywords", keywords);
    }

    private void setProviderCode(ProductProperty property, String code) {
        Provider provider = property.getProvider();
        ReflectionTestUtils.setField(provider, "code", code);
    }

    private SearchRequestDto createRequest(Long monthlySavingsGoal) {
        return createRequest(monthlySavingsGoal, null, null);
    }

    private SearchRequestDto createRequest(
            Long monthlySavingsGoal,
            List<String> neverUsedBanks,
            List<String> maturedSavingBanks
    ) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, monthlySavingsGoal, null,
                        neverUsedBanks, maturedSavingBanks, List.of()
                )
        );
    }
}

