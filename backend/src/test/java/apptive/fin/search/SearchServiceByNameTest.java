package apptive.fin.search;

import apptive.fin.search.dto.ProductNameSearchDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.search.entity.Provider;
import apptive.fin.search.provider.ProviderDisplayResolver;
import apptive.fin.search.repository.ProductRepository;
import apptive.fin.search.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SearchServiceByNameTest {
    @InjectMocks
    private SearchService searchService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EligibilityFilterService eligibilityFilterService;

    @Mock
    private MatchScoreService matchScoreService;

    @Mock
    private RateCalculatorService rateCalculatorService;

    @Mock
    private ResolveKeywordService resolveKeywordService;

    @Mock
    private ProviderDisplayResolver providerDisplayResolver;

    @BeforeEach
    void stubProviderDisplayResolver() {
        lenient().when(providerDisplayResolver.resolveName(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> {
                    Provider provider = invocation.getArgument(0);
                    return provider != null ? provider.getName() : null;
                });
    }

    // ────────────────────────────────────────────────
    // 헬퍼: ReflectionTestUtils로 엔티티 생성
    // ────────────────────────────────────────────────

    private ProductSource createSource(String code) {
        ProductSource source = new ProductSource();
        ReflectionTestUtils.setField(source, "code", code);
        return source;
    }

    private Provider createProvider(String name, ProductSource source) {
        Provider provider = new Provider();
        ReflectionTestUtils.setField(provider, "name", name);
        ReflectionTestUtils.setField(provider, "source", source);
        return provider;
    }

    private ProductProperty createProperty(Provider provider, double baseRate, double maxRate) {
        ProductProperty property = new ProductProperty();
        ReflectionTestUtils.setField(property, "provider", provider);
        ReflectionTestUtils.setField(property, "baseRate", BigDecimal.valueOf(baseRate));
        ReflectionTestUtils.setField(property, "maxRate", BigDecimal.valueOf(maxRate));
        ReflectionTestUtils.setField(property, "keywords", Collections.emptyList());
        return property;
    }

    private Product createProduct(Long id, String productName, String sourceCode,
                                  String providerName, double baseRate, double maxRate) {
        ProductSource source = createSource(sourceCode);
        Provider provider = createProvider(providerName, source);
        ProductProperty property = createProperty(provider, baseRate, maxRate);

        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "productName", productName);
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "type", ProductType.SAVING);
        ReflectionTestUtils.setField(product, "properties", List.of(property));
        return product;
    }

    // ────────────────────────────────────────────────
    // 정상 케이스
    // ────────────────────────────────────────────────

    @Test
    @DisplayName("상품명 검색 - '청년' 키워드로 3개 반환")
    void searchByName_withKeyword_returnsMatchingProducts() {
        // given
        List<Product> mockProducts = List.of(
                createProduct(1L, "청년내일채움공제",    "ONTONG", "금융위원회", 10.0, 10.0),
                createProduct(2L, "청년우대형 청약통장", "ONTONG", "금융위원회",  4.5,  6.0),
                createProduct(3L, "청년우대적금",        "FSS",    "국민은행",    3.8,  4.5)
        );
        given(productRepository.findByProductNameContaining("청년")).willReturn(mockProducts);

        // when
        List<ProductNameSearchDto> result = searchService.searchByName("청년");

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(ProductNameSearchDto::productName)
                .containsExactlyInAnyOrder("청년내일채움공제", "청년우대형 청약통장", "청년우대적금");

        verify(productRepository).findByProductNameContaining("청년");
    }

    @Test
    @DisplayName("상품명 검색 - maxRate가 가장 높은 property가 bestProperty로 선택됨")
    void searchByName_selectsBestPropertyByMaxRate() {
        // given
        ProductSource source   = createSource("FSS");
        Provider provider      = createProvider("국민은행", source);

        ProductProperty lowRate  = createProperty(provider, 3.5, 4.2); // 낮은 maxRate
        ProductProperty highRate = createProperty(provider, 3.8, 4.5); // 높은 maxRate → bestProperty

        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "productName", "청년우대적금");
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "type", ProductType.SAVING);
        ReflectionTestUtils.setField(product, "properties", List.of(lowRate, highRate));

        given(productRepository.findByProductNameContaining("청년우대적금"))
                .willReturn(List.of(product));

        // when
        List<ProductNameSearchDto> result = searchService.searchByName("청년우대적금");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).maxRate()).isEqualTo(4.5);
        assertThat(result.get(0).baseRate()).isEqualTo(3.8);
    }

    @Test
    @DisplayName("상품명 검색 - DTO 필드 매핑이 올바르게 됨")
    void searchByName_mapsFieldsCorrectly() {
        // given
        Product product = createProduct(10L, "청년우대적금", "FSS", "국민은행", 3.8, 4.5);
        given(productRepository.findByProductNameContaining("청년우대적금"))
                .willReturn(List.of(product));

        // when
        List<ProductNameSearchDto> result = searchService.searchByName("청년우대적금");

        // then
        ProductNameSearchDto dto = result.get(0);
        assertThat(dto.productId()).isEqualTo(10L);
        assertThat(dto.productName()).isEqualTo("청년우대적금");
        assertThat(dto.source()).isEqualTo("FSS");
        assertThat(dto.providerName()).isEqualTo("국민은행");
        assertThat(dto.baseRate()).isEqualTo(3.8);
        assertThat(dto.maxRate()).isEqualTo(4.5);
    }

    // ────────────────────────────────────────────────
    // 엣지 케이스
    // ────────────────────────────────────────────────

    @Test
    @DisplayName("상품명 검색 - 결과 없을 때 빈 리스트 반환")
    void searchByName_noResult_returnsEmptyList() {
        // given
        given(productRepository.findByProductNameContaining("없는상품"))
                .willReturn(Collections.emptyList());

        // when
        List<ProductNameSearchDto> result = searchService.searchByName("없는상품");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("상품명 검색 - properties가 없을 때 null 안전하게 처리")
    void searchByName_noProperties_returnsNullSafeDto() {
        // given
        ProductSource source = createSource("FSS");

        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "productName", "청년우대적금");
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "type", ProductType.SAVING);
        ReflectionTestUtils.setField(product, "properties", Collections.emptyList()); // 빈 properties

        given(productRepository.findByProductNameContaining("청년우대적금"))
                .willReturn(List.of(product));

        // when
        List<ProductNameSearchDto> result = searchService.searchByName("청년우대적금");

        // then
        assertThat(result).hasSize(1);
        ProductNameSearchDto dto = result.get(0);
        assertThat(dto.providerName()).isNull();
        assertThat(dto.baseRate()).isNull();
        assertThat(dto.maxRate()).isNull();
    }

    @Test
    @DisplayName("상품명 검색 - maxRate가 null인 property 처리")
    void searchByName_nullMaxRate_treatedAsZero() {
        // given
        ProductSource source   = createSource("FSS");
        Provider provider      = createProvider("국민은행", source);

        ProductProperty nullRateProperty = new ProductProperty();
        ReflectionTestUtils.setField(nullRateProperty, "provider", provider);
        ReflectionTestUtils.setField(nullRateProperty, "baseRate", null);
        ReflectionTestUtils.setField(nullRateProperty, "maxRate", null); // null → 0.0으로 처리
        ReflectionTestUtils.setField(nullRateProperty, "keywords", Collections.emptyList());

        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "productName", "청년우대적금");
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "type", ProductType.SAVING);
        ReflectionTestUtils.setField(product, "properties", List.of(nullRateProperty));

        given(productRepository.findByProductNameContaining("청년우대적금"))
                .willReturn(List.of(product));

        // when & then (NPE 없이 정상 동작 확인)
        List<ProductNameSearchDto> result = searchService.searchByName("청년우대적금");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).baseRate()).isNull();
        assertThat(result.get(0).maxRate()).isNull();
    }

}
