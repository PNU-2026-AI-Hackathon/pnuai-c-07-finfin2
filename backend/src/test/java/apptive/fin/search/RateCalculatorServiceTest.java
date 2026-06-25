package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductPreferentialRate;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.search.entity.Provider;
import apptive.fin.search.service.RateCalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RateCalculatorServiceTest {

    private final RateCalculatorService rateCalculatorService = new RateCalculatorService();

    @Test
    void 은행상품은_적용되는_우대금리가_없으면_기본금리를_사용한다() {
        Product product = createProduct("BANK001", "basic bank product", "FSS");
        ProductProperty property = createProperty(10L, "KB", "KB국민은행", "2.75", null);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), emptyKeywords());

        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.productPropertyId()).isEqualTo(10L);
        assertThat(result.baseRate()).isEqualTo(2.75);
        assertThat(result.achievableRate()).isEqualTo(2.75);
        assertThat(result.rateComparable()).isTrue();
        assertThat(result.isSubscription()).isFalse();
    }

    @Test
    void 은행상품은_선택한_1단계_우대금리만_더한다() {
        Product product = createProduct("BANK002", "preferential bank product", "FSS");
        ProductProperty property = createProperty(10L, "KB", "KB국민은행", "3.50", "5.00");
        addPreferentialRates(property,
                preferentialRate(KeywordValueEnum.BANK_SALARY_TRANSFER, "0.30"),
                preferentialRate(KeywordValueEnum.BANK_CARD_USAGE, "0.20"));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(),
                keywords(List.of(KeywordValueEnum.BANK_SALARY_TRANSFER))
        );

        assertThat(result.baseRate()).isEqualTo(3.5);
        assertThat(result.achievableRate()).isEqualTo(3.8);
    }

    @Test
    void 은행상품은_비대면가입_우대금리를_자동으로_더한다() {
        Product product = createProduct("BANK003", "online bank product", "FSS");
        ProductProperty property = createProperty(10L, "KB", "KB국민은행", "3.50", "5.00");
        addPreferentialRates(property, preferentialRate(KeywordValueEnum.BANK_ONLINE_JOIN, "0.10"));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), emptyKeywords());

        assertThat(result.achievableRate()).isEqualTo(3.6);
    }

    @Test
    void 은행상품은_미거래_은행일_때만_첫거래_우대금리를_더한다() {
        Product product = createProduct("BANK004", "first transaction product", "FSS");
        ProductProperty property = createProperty(10L, "KB", "KB국민은행", "3.50", "5.00");
        addPreferentialRates(property, preferentialRate(KeywordValueEnum.BANK_FIRST_TRANSACTION, "0.50"));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(null, null, List.of("KB"), List.of()),
                emptyKeywords()
        );

        assertThat(result.achievableRate()).isEqualTo(4.0);
    }

    @Test
    void 은행상품은_다른_은행의_첫거래_이력을_우대금리에_더하지_않는다() {
        Product product = createProduct("BANK005", "other first transaction product", "FSS");
        ProductProperty property = createProperty(10L, "KB", "KB국민은행", "3.50", "5.00");
        addPreferentialRates(property, preferentialRate(KeywordValueEnum.BANK_FIRST_TRANSACTION, "0.50"));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(null, null, List.of("SHINHAN"), List.of()),
                emptyKeywords()
        );

        assertThat(result.achievableRate()).isEqualTo(3.5);
    }

    @Test
    void 은행상품은_만기이력_은행일_때만_재예치_우대금리를_더한다() {
        Product product = createProduct("BANK006", "redeposit product", "FSS");
        ProductProperty property = createProperty(10L, "KB", "KB국민은행", "3.50", "5.00");
        addPreferentialRates(property, preferentialRate(KeywordValueEnum.BANK_REDEPOSIT, "0.40"));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(null, null, List.of(), List.of("KB")),
                emptyKeywords()
        );

        assertThat(result.achievableRate()).isEqualTo(3.9);
    }

    @Test
    void 은행상품은_나이조건이_맞으면_연령_우대금리를_더한다() {
        Product product = createProduct("BANK007", "age product", "FSS");
        ProductProperty property = createProperty(10L, "KB", "KB국민은행", "3.50", "5.00");
        addPreferentialRates(property, preferentialRate(KeywordValueEnum.BANK_AGE, "0.20", 19, 34));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(LocalDate.now().minusYears(25), null, List.of(), List.of()),
                emptyKeywords()
        );

        assertThat(result.achievableRate()).isEqualTo(3.7);
    }

    @Test
    void 은행상품은_달성가능금리를_최고금리로_상한처리한다() {
        Product product = createProduct("BANK008", "capped product", "FSS");
        ProductProperty property = createProperty(10L, "KB", "KB국민은행", "3.80", "4.00");
        addPreferentialRates(property,
                preferentialRate(KeywordValueEnum.BANK_SALARY_TRANSFER, "0.30"),
                preferentialRate(KeywordValueEnum.BANK_ONLINE_JOIN, "0.10"));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(),
                keywords(List.of(KeywordValueEnum.BANK_SALARY_TRANSFER))
        );

        assertThat(result.achievableRate()).isEqualTo(4.0);
    }

    @Test
    void 은행상품은_달성가능금리가_가장_높은_옵션을_선택한다() {
        Product product = createProduct("BANK009", "multi option product", "FSS");
        ProductProperty first = createProperty(10L, "KB", "KB국민은행", "3.80", "4.50");
        ProductProperty second = createProperty(11L, "KB", "KB국민은행", "3.50", "5.00");
        addPreferentialRates(second, preferentialRate(KeywordValueEnum.BANK_SALARY_TRANSFER, "0.60"));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(first, second)));

        ProductRateDto result = rateCalculatorService.calculate(
                product,
                createRequest(),
                keywords(List.of(KeywordValueEnum.BANK_SALARY_TRANSFER))
        );

        assertThat(result.productPropertyId()).isEqualTo(11L);
        assertThat(result.baseRate()).isEqualTo(3.5);
        assertThat(result.achievableRate()).isEqualTo(4.1);
    }

    @Test
    void 청약상품은_금리비교_불가로_반환한다() {
        Product product = createProduct("GOV001", "subscription product", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(product, "type", ProductType.SUBSCRIPTION);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), emptyKeywords());

        assertThat(result.isSubscription()).isTrue();
        assertThat(result.rateComparable()).isFalse();
    }

    @Test
    void 정부_정률상품은_기여금_환산수익률을_사용한다() {
        Product product = createProduct("GOV002", "government ratio product", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", "4.00", "4.50");
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(property, "govMatchingRatio", new BigDecimal("1.0000"));
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 24);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), emptyKeywords());

        assertThat(result.baseRate()).isZero();
        assertThat(result.achievableRate()).isEqualTo(50.0);
        assertThat(result.rateComparable()).isTrue();
        assertThat(result.isSubscription()).isFalse();
    }

    @Test
    void 정부_정률상품은_퍼센트형_매칭비율을_지원한다() {
        Product product = createProduct("GOV_PERCENT", "government percent matching product", "ONTONG");
        ProductProperty general = createProperty(10L, "GOV", "정책기관", null, null);
        ProductProperty preferential = createProperty(11L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(general, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(general, "govMatchingRatio", new BigDecimal("0.0600"));
        ReflectionTestUtils.setField(general, "govContributionPeriodMonths", 36);
        ReflectionTestUtils.setField(preferential, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(preferential, "govMatchingRatio", new BigDecimal("0.1200"));
        ReflectionTestUtils.setField(preferential, "govContributionPeriodMonths", 36);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(general, preferential)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), emptyKeywords());

        assertThat(result.productPropertyId()).isEqualTo(11L);
        assertThat(result.achievableRate()).isEqualTo(4.0);
        assertThat(result.rateComparable()).isTrue();
    }

    @Test
    void 정부_정률상품은_10개월_기여기간을_지원한다() {
        Product product = createProduct("GOV_TEN_MONTH", "government ten month product", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(property, "govMatchingRatio", new BigDecimal("1.0000"));
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 10);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), emptyKeywords());

        assertThat(result.achievableRate()).isEqualTo(120.0);
        assertThat(result.rateComparable()).isTrue();
    }

    @Test
    void 정부_정률상품은_1점5배_매칭비율을_지원한다() {
        Product product = createProduct("GOV_RATIO_1_5", "government 1.5 ratio product", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(property, "govMatchingRatio", new BigDecimal("1.5000"));
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 36);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), emptyKeywords());

        assertThat(result.achievableRate()).isEqualTo(50.0);
        assertThat(result.rateComparable()).isTrue();
    }

    @Test
    void 정부_정액상품은_희망월납입액으로_수익률을_계산한다() {
        Product product = createProduct("GOV003", "government fixed product", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.FIXED_AMOUNT);
        ReflectionTestUtils.setField(property, "govMonthlyFixedContribution", 300_000L);
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 36);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(100_000L), emptyKeywords());

        assertThat(result.achievableRate()).isEqualTo(100.0);
        assertThat(result.rateComparable()).isTrue();
        assertThat(result.isSubscription()).isFalse();
    }

    @Test
    void 정부_정액상품은_최대월납입한도를_실효납입액으로_사용한다() {
        Product product = createProduct("GOV_FIXED_LIMIT", "government fixed limited product", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.FIXED_AMOUNT);
        ReflectionTestUtils.setField(property, "govMonthlyFixedContribution", 150_000L);
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 36);
        ReflectionTestUtils.setField(property, "maxMonthlyLimit", 150_000L);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(500_000L), emptyKeywords());

        assertThat(result.achievableRate()).isEqualTo(33.33333333333333);
        assertThat(result.rateComparable()).isTrue();
    }

    @Test
    void 정부_정액상품은_희망월납입액이_없으면_금리비교_불가로_반환한다() {
        Product product = createProduct("GOV004", "government incomparable fixed product", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.FIXED_AMOUNT);
        ReflectionTestUtils.setField(property, "govMonthlyFixedContribution", 300_000L);
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 36);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), emptyKeywords());

        assertThat(result.rateComparable()).isFalse();
        assertThat(result.achievableRate()).isZero();
        assertThat(result.isSubscription()).isFalse();
    }

    @Test
    void 금리비교_제외_정부상품은_금리비교_불가로_반환한다() {
        Product product = createProduct("GOV005", "excluded government product", "ONTONG");
        ProductProperty property = createProperty(10L, "GOV", "정책기관", null, null);
        ReflectionTestUtils.setField(property, "excludeFromRateComparison", true);
        ReflectionTestUtils.setField(property, "govContributionType", ContributionType.RATIO);
        ReflectionTestUtils.setField(property, "govMatchingRatio", new BigDecimal("1.0000"));
        ReflectionTestUtils.setField(property, "govContributionPeriodMonths", 24);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        ProductRateDto result = rateCalculatorService.calculate(product, createRequest(), emptyKeywords());

        assertThat(result.rateComparable()).isFalse();
        assertThat(result.isSubscription()).isFalse();
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

    private ProductPreferentialRate preferentialRate(KeywordValueEnum keyword, String rate) {
        return preferentialRate(keyword, rate, null, null);
    }

    private ProductPreferentialRate preferentialRate(KeywordValueEnum keyword, String rate, Integer minAge, Integer maxAge) {
        ProductPreferentialRate preferentialRate = new ProductPreferentialRate();
        ReflectionTestUtils.setField(preferentialRate, "keywordCode", keyword);
        ReflectionTestUtils.setField(preferentialRate, "rate", new BigDecimal(rate));
        ReflectionTestUtils.setField(preferentialRate, "minAge", minAge);
        ReflectionTestUtils.setField(preferentialRate, "maxAge", maxAge);
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
        return createRequest(null, monthlySavingsGoal, List.of(), List.of());
    }

    private SearchRequestDto createRequest(
            LocalDate birthdate,
            Long monthlySavingsGoal,
            List<String> neverUsedBanks,
            List<String> maturedSavingBanks
    ) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        birthdate,
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

    private ResolvedKeywords emptyKeywords() {
        return keywords(List.of());
    }

    private ResolvedKeywords keywords(List<KeywordValueEnum> bankConditions) {
        return new ResolvedKeywords(List.of(), List.of(), null, List.of(), bankConditions);
    }
}
