package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ParkingProductDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.search.enums.ProductType;
import apptive.fin.search.repository.ProductRepository;
import apptive.fin.search.service.ParkingProductService;
import apptive.fin.provider.entity.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ParkingProductService parkingProductService;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 mock 초기화
    }

    @Test
    void 파킹통장_목록을_최고금리순으로_정렬한다() {
        // Given
        Product parking1 = createParkingProduct(1L, "파킹통장A", "3.00", "3.50");
        Product parking2 = createParkingProduct(2L, "파킹통장B", "3.20", "4.00");
        Product parking3 = createParkingProduct(3L, "파킹통장C", "2.80", "3.80");

        when(productRepository.findByType(ProductType.PARKING))
                .thenReturn(List.of(parking1, parking2, parking3));

        SearchRequestDto request = createRequest();

        // When
        List<ParkingProductDto> result = parkingProductService.findParkingProducts(request);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).productName()).isEqualTo("파킹통장B"); // 4.00
        assertThat(result.get(1).productName()).isEqualTo("파킹통장C"); // 3.80
        assertThat(result.get(2).productName()).isEqualTo("파킹통장A"); // 3.50
    }

    @Test
    void 가입불가_상품은_제외한다() {
        // Given
        Product joinable = createParkingProduct(1L, "가입가능", "3.00", "3.50");
        Product notJoinable = createParkingProduct(2L, "가입불가", "4.00", "5.00");
        ReflectionTestUtils.setField(notJoinable.getProperties().get(0), "isJoinable", false);

        when(productRepository.findByType(ProductType.PARKING))
                .thenReturn(List.of(joinable, notJoinable));

        SearchRequestDto request = createRequest();

        // When
        List<ParkingProductDto> result = parkingProductService.findParkingProducts(request);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).productName()).isEqualTo("가입가능");
    }

    @Test
    void 예치액_필터를_적용하면_한도_내_상품만_반환한다() {
        // Given
        Product highLimit = createParkingProduct(1L, "고액한도", "3.50", "4.00");
        ReflectionTestUtils.setField(highLimit.getProperties().get(0), "maxMonthlyLimit", 100_000_000L);

        Product lowLimit = createParkingProduct(2L, "저액한도", "3.80", "4.20");
        ReflectionTestUtils.setField(lowLimit.getProperties().get(0), "maxMonthlyLimit", 10_000_000L);

        when(productRepository.findByType(ProductType.PARKING))
                .thenReturn(List.of(highLimit, lowLimit));

        // 예치액 5천만원
        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, null,
                        50_000_000L, 1,
                        null, null, List.of()
                )
        );

        // When
        List<ParkingProductDto> result = parkingProductService.findParkingProductsWithDepositFilter(request);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).productName()).isEqualTo("고액한도");
    }

    @Test
    void DTO에_필요한_정보가_모두_포함된다() {
        // Given
        Product parking = createParkingProduct(1L, "테스트파킹", "3.00", "3.50");
        ProductProperty property = parking.getProperties().get(0);
        ReflectionTestUtils.setField(property, "applyUrl", "https://example.com/apply");
        ReflectionTestUtils.setField(property, "maxMonthlyLimit", 50_000_000L);

        when(productRepository.findByType(ProductType.PARKING))
                .thenReturn(List.of(parking));

        SearchRequestDto request = createRequest();

        // When
        List<ParkingProductDto> result = parkingProductService.findParkingProducts(request);

        // Then
        assertThat(result).hasSize(1);
        ParkingProductDto dto = result.get(0);
        assertThat(dto.productId()).isEqualTo(1L);
        assertThat(dto.productName()).isEqualTo("테스트파킹");
        assertThat(dto.providerName()).isEqualTo("테스트은행");
        assertThat(dto.providerCode()).isEqualTo("TEST");
        assertThat(dto.baseRate()).isEqualTo(3.00);
        assertThat(dto.maxRate()).isEqualTo(3.50);
        assertThat(dto.applicableLimit()).isEqualTo(50_000_000L);
        assertThat(dto.interestPaymentMethod()).isEqualTo("매일");
        assertThat(dto.depositProtection()).isTrue();
        assertThat(dto.applyUrl()).isEqualTo("https://example.com/apply");
    }

    @Test
    void 파킹통장이_없으면_빈_리스트를_반환한다() {
        // Given
        when(productRepository.findByType(ProductType.PARKING))
                .thenReturn(List.of());

        SearchRequestDto request = createRequest();

        // When
        List<ParkingProductDto> result = parkingProductService.findParkingProducts(request);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void 동점시_기본금리_그다음_상품명으로_정렬한다() {
        // Given: 최고금리 동일, 기본금리 다름
        Product parking1 = createParkingProduct(1L, "B파킹", "3.00", "4.00");
        Product parking2 = createParkingProduct(2L, "A파킹", "3.20", "4.00");  // 기본금리 높음

        when(productRepository.findByType(ProductType.PARKING))
                .thenReturn(List.of(parking1, parking2));

        SearchRequestDto request = createRequest();

        // When
        List<ParkingProductDto> result = parkingProductService.findParkingProducts(request);

        // Then: 최고금리 동일 → 기본금리 높은 순서
        assertThat(result).hasSize(2);
        assertThat(result.get(0).productName()).isEqualTo("A파킹");  // 기본금리 3.20
        assertThat(result.get(1).productName()).isEqualTo("B파킹");  // 기본금리 3.00
    }

    @Test
    void 동점시_기본금리도_같으면_상품명순으로_정렬한다() {
        // Given: 최고금리, 기본금리 모두 동일
        Product parking1 = createParkingProduct(1L, "다파킹", "3.00", "4.00");
        Product parking2 = createParkingProduct(2L, "가파킹", "3.00", "4.00");
        Product parking3 = createParkingProduct(3L, "나파킹", "3.00", "4.00");

        when(productRepository.findByType(ProductType.PARKING))
                .thenReturn(List.of(parking1, parking2, parking3));

        SearchRequestDto request = createRequest();

        // When
        List<ParkingProductDto> result = parkingProductService.findParkingProducts(request);

        // Then: 상품명 오름차순
        assertThat(result).hasSize(3);
        assertThat(result.get(0).productName()).isEqualTo("가파킹");
        assertThat(result.get(1).productName()).isEqualTo("나파킹");
        assertThat(result.get(2).productName()).isEqualTo("다파킹");
    }

    // === Helper methods ===

    private Product createParkingProduct(Long id, String name, String baseRate, String maxRate) {
        ProductSource source = new ProductSource();
        ReflectionTestUtils.setField(source, "code", "FSS");

        Provider provider = new Provider();
        ReflectionTestUtils.setField(provider, "code", "TEST");
        ReflectionTestUtils.setField(provider, "name", "테스트은행");

        ProductProperty property = new ProductProperty();
        ReflectionTestUtils.setField(property, "id", id);
        ReflectionTestUtils.setField(property, "provider", provider);
        ReflectionTestUtils.setField(property, "baseRate", new BigDecimal(baseRate));
        ReflectionTestUtils.setField(property, "maxRate", new BigDecimal(maxRate));
        ReflectionTestUtils.setField(property, "isJoinable", true);
        ReflectionTestUtils.setField(property, "keywords", new ArrayList<>());
        ReflectionTestUtils.setField(property, "preferentialRates", new ArrayList<>());

        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "productName", name);
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "type", ProductType.PARKING);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));
        ReflectionTestUtils.setField(property, "product", product);

        return product;
    }

    private SearchRequestDto createRequest() {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, null,
                        null, null,
                        null, null, List.of()
                )
        );
    }
}
