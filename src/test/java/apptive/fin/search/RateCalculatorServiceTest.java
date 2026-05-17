package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductKeyword;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.search.entity.Provider;
import apptive.fin.search.service.RateCalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RateCalculatorServiceTest {

    private final RateCalculatorService rateCalculatorService = new RateCalculatorService();

    @Test
    void 상품에_옵션이_여러개이면_옵션별_금리결과를_모두_반환한다() {
        Product product = createProduct("BANK001", "청년우대적금", "FSS");
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(
                createProperty(10L, "테스트은행", "3.80", "4.50"),
                createProperty(11L, "테스트은행", "3.50", "4.20")
        )));

        List<ProductRateDto> results = rateCalculatorService.calculate(product, createRequest());

        assertThat(results).hasSize(2);
        ProductRateDto result = results.get(0);
        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.productPropertyId()).isEqualTo(10L);
        assertThat(result.productName()).isEqualTo("청년우대적금");
        assertThat(result.providerName()).isEqualTo("테스트은행");
        assertThat(result.baseRate()).isEqualTo(3.8);
        assertThat(result.achievableRate()).isEqualTo(4.5);
        assertThat(result.isSubscription()).isFalse();
        assertThat(results.get(1).productPropertyId()).isEqualTo(11L);
        assertThat(results.get(1).achievableRate()).isEqualTo(4.2);
    }

    @Test
    void 최고금리가_없으면_기본금리를_달성가능금리로_사용한다() {
        Product product = createProduct("BANK002", "기본금리상품", "FSS");
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(
                createProperty(10L, "테스트은행", "2.75", null)
        )));

        List<ProductRateDto> results = rateCalculatorService.calculate(product, createRequest());

        assertThat(results).hasSize(1);
        ProductRateDto result = results.get(0);
        assertThat(result.baseRate()).isEqualTo(2.75);
        assertThat(result.achievableRate()).isEqualTo(2.75);
    }

    @Test
    void 금리가_없는_청약상품은_금리비교_대상에서_제외한다() {
        Product product = createProduct("GOV001", "청약상품", "ONTONG");
        ProductKeyword keyword = new ProductKeyword();
        ProductProperty property = createProperty(10L, "테스트은행", null, null);
        ReflectionTestUtils.setField(keyword, "keywordCode", KeywordValueEnum.INTEREST_SAVINGS);
        ReflectionTestUtils.setField(property, "keywords", new ArrayList<>(List.of(keyword)));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));

        List<ProductRateDto> results = rateCalculatorService.calculate(product, createRequest());

        assertThat(results).hasSize(1);
        ProductRateDto result = results.get(0);
        assertThat(result.isSubscription()).isTrue();
        assertThat(result.productPropertyId()).isEqualTo(10L);
        assertThat(result.providerName()).isEqualTo("테스트은행");
        assertThat(result.subscriptionNote()).isEqualTo("청약: 금리 비교 대상 아님");
    }

    @Test
    void 청약옵션과_일반옵션이_같은_상품에_있으면_각각_분리해서_반환한다() {
        Product product = createProduct("GOV001", "복합정책상품", "ONTONG");
        ProductKeyword keyword = new ProductKeyword();
        ProductProperty subscriptionProperty = createProperty(10L, "정책기관", null, null);
        ProductProperty rateProperty = createProperty(11L, "정책기관", "4.00", "4.50");
        ReflectionTestUtils.setField(keyword, "keywordCode", KeywordValueEnum.INTEREST_SAVINGS);
        ReflectionTestUtils.setField(subscriptionProperty, "keywords", new ArrayList<>(List.of(keyword)));
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(subscriptionProperty, rateProperty)));

        List<ProductRateDto> results = rateCalculatorService.calculate(product, createRequest());

        assertThat(results).hasSize(2);
        assertThat(results.get(0).isSubscription()).isTrue();
        assertThat(results.get(0).productPropertyId()).isEqualTo(10L);
        assertThat(results.get(1).isSubscription()).isFalse();
        assertThat(results.get(1).productPropertyId()).isEqualTo(11L);
        assertThat(results.get(1).achievableRate()).isEqualTo(4.0);
    }

    private Product createProduct(String code, String name, String sourceCode) {
        ProductSource source = new ProductSource();
        ReflectionTestUtils.setField(source, "code", sourceCode);

        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "productCode", code);
        ReflectionTestUtils.setField(product, "productName", name);
        ReflectionTestUtils.setField(product, "source", source);
        return product;
    }

    private ProductProperty createProperty(Long id, String providerName, String baseRate, String maxRate) {
        ProductProperty property = new ProductProperty();
        Provider provider = new Provider();
        ReflectionTestUtils.setField(provider, "name", providerName);
        ReflectionTestUtils.setField(property, "id", id);
        ReflectionTestUtils.setField(property, "provider", provider);
        ReflectionTestUtils.setField(property, "keywords", new ArrayList<>());
        if (baseRate != null) {
            ReflectionTestUtils.setField(property, "baseRate", new BigDecimal(baseRate));
        }
        if (maxRate != null) {
            ReflectionTestUtils.setField(property, "maxRate", new BigDecimal(maxRate));
        }
        return property;
    }

    private SearchRequestDto createRequest() {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(null, null, null, null, null, null, null, null, null, null, List.of())
        );
    }
}
