package apptive.fin.search;

import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
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

/**
 * V2 가중치 테스트 (PRD 개정: 3축 - 혜택50, 기간30, 납입20).
 * 신분특화, 은행조건 점수는 제거됨.
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
                KeywordValueEnum.BENEFIT_EASY_CONDITION
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
                        List.of()
                ),
                false
        );

        // V2: 혜택50 + 기간30 + 납입20 = 100
        assertThat(result.benefitScore()).isCloseTo(50.0, offset(0.001));
        assertThat(result.periodScore()).isCloseTo(30.0, offset(0.001));
        assertThat(result.depositScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.identityScore()).isZero();  // V2에서 제거됨
        assertThat(result.bankCondScore()).isZero();  // V2에서 제거됨
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void V2_정부상품_모든_축이_만점이면_총점_100이다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("ONTONG", createProperty(
                10L,
                "policy-provider",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_GOV_SUBSIDY
        ));

        ProductMatchDto result = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_GOV_SUBSIDY),
                        List.of()
                ),
                false
        );

        // V2: 혜택50 + 기간30 + 납입20 = 100
        assertThat(result.benefitScore()).isCloseTo(50.0, offset(0.001));
        assertThat(result.periodScore()).isCloseTo(30.0, offset(0.001));
        assertThat(result.depositScore()).isCloseTo(20.0, offset(0.001));
        assertThat(result.identityScore()).isZero();
        assertThat(result.bankCondScore()).isZero();
        assertThat(result.totalScore()).isCloseTo(100.0, offset(0.001));
    }

    @Test
    void V2_혜택이_없으면_기간과_납입에_재배분된다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12
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
                        List.of()
                ),
                false
        );

        // 혜택 50 제거 → 기간 30 + 납입 20 = 50 → 재배분 후 기간 60 + 납입 40 = 100
        assertThat(result.benefitScore()).isZero();
        assertThat(result.periodScore()).isCloseTo(60.0, offset(0.001));
        assertThat(result.depositScore()).isCloseTo(40.0, offset(0.001));
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
                KeywordValueEnum.BENEFIT_EASY_CONDITION
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
                        List.of()
                ),
                false
        );

        // 기간 불일치 → 기간점수 0 → 혜택 + 납입만 배점
        assertThat(result.periodScore()).isZero();
        assertThat(result.totalScore()).isCloseTo(70.0, offset(0.001));  // 혜택50 + 납입20
    }

    @Test
    void V2_납입한도_초과시_비율만큼_감점된다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                150_000L,  // 한도 15만
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION
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
                        List.of()
                ),
                false
        );

        // 납입: 150000/300000 = 0.5 → 20 * 0.5 = 10
        assertThat(result.depositScore()).isCloseTo(10.0, offset(0.001));
        assertThat(result.totalScore()).isCloseTo(90.0, offset(0.001));  // 50 + 30 + 10
    }

    @Test
    void V2_신분선택은_점수에_영향없다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.STATUS_MILITARY
        ));

        ProductMatchDto withIdentity = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_MILITARY),  // 신분 선택
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of()
                ),
                false
        );

        ProductMatchDto withoutIdentity = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),  // 신분 미선택
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of()
                ),
                false
        );

        // V2에서는 신분 선택 여부가 점수에 영향 없음
        assertThat(withIdentity.identityScore()).isZero();
        assertThat(withoutIdentity.identityScore()).isZero();
        assertThat(withIdentity.totalScore()).isEqualTo(withoutIdentity.totalScore());
    }

    @Test
    void V2_은행조건선택은_점수에_영향없다() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                12,
                KeywordValueEnum.BENEFIT_EASY_CONDITION,
                KeywordValueEnum.BANK_SALARY_TRANSFER
        ));

        ProductMatchDto withBankCond = matchScoreService.score(
                product,
                product.getProperties().get(0),
                createRequest(300_000L),
                new ResolvedKeywords(
                        List.of(),
                        List.of(),
                        KeywordValueEnum.TERM_12_MONTH,
                        List.of(KeywordValueEnum.BENEFIT_EASY_CONDITION),
                        List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)  // 은행조건 선택
                ),
                false
        );

        ProductMatchDto withoutBankCond = matchScoreService.score(
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

        // V2에서는 은행조건 선택 여부가 점수에 영향 없음
        assertThat(withBankCond.bankCondScore()).isZero();
        assertThat(withoutBankCond.bankCondScore()).isZero();
        assertThat(withBankCond.totalScore()).isEqualTo(withoutBankCond.totalScore());
    }

    @Test
    void V2_단기예치_1개월_정확매칭() {
        MatchScoreService matchScoreService = new MatchScoreService();
        Product product = createProduct("FSS", createProperty(
                10L,
                "test-bank",
                500_000L,
                1,  // 1개월 상품
                KeywordValueEnum.BENEFIT_EASY_CONDITION
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
                        List.of()
                ),
                false
        );

        assertThat(result.periodScore()).isCloseTo(30.0, offset(0.001));
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
            addOwnedKeyword(property, keyword);
        }
        return property;
    }

    private void addOwnedKeyword(ProductProperty property, KeywordValueEnum keywordValue) {
        if (keywordValue.isPreferentialRate()) {
            return;  // 우대금리는 별도 처리 필요
        }
        if (keywordValue.isRequired()) {
            return;  // 가입조건은 별도 처리 필요
        }

        apptive.fin.search.entity.ProductKeyword keyword = new apptive.fin.search.entity.ProductKeyword();
        ReflectionTestUtils.setField(keyword, "keywordCode", keywordValue);
        List<apptive.fin.search.entity.ProductKeyword> keywords = new ArrayList<>(
                (List<apptive.fin.search.entity.ProductKeyword>) ReflectionTestUtils.getField(property, "keywords")
        );
        keywords.add(keyword);
        ReflectionTestUtils.setField(property, "keywords", keywords);
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
