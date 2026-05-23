package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.search.entity.Provider;
import apptive.fin.search.service.MatchScoreService;
import apptive.fin.search.service.ResolveKeywordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchScoreServiceTest {

    @Mock
    private ResolveKeywordService resolveKeywordService;

    @Test
    void 월저축목표가_null이어도_예외없이_저축액점수를_제외한다() {
        MatchScoreService matchScoreService = new MatchScoreService(resolveKeywordService);
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
        when(resolveKeywordService.resolveKeywords(request.options()))
                .thenReturn(new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()));

        // List → 단일 반환으로 변경
        ProductMatchDto result = matchScoreService.score(product, request);

        assertThat(result.depositScore()).isZero();
        assertThat(result.productPropertyId()).isEqualTo(10L);
        assertThat(result.providerName()).isEqualTo("테스트은행");
    }

    @Test
    void 옵션이_여러개이면_최고점수_옵션_하나만_반환한다() {
        MatchScoreService matchScoreService = new MatchScoreService(resolveKeywordService);
        Product product = new Product();
        ProductSource source = new ProductSource();

        // maxMonthlyLimit이 더 큰 쪽이 depositScore 높음
        ProductProperty firstProperty  = createProperty(10L, "테스트은행A", 300_000L);
        ProductProperty secondProperty = createProperty(11L, "테스트은행B", 500_000L);

        ReflectionTestUtils.setField(source, "code", "FSS");
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "productName", "청년우대적금");
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(firstProperty, secondProperty)));

        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, 400_000L, null, List.of()
                )
        );
        when(resolveKeywordService.resolveKeywords(request.options()))
                .thenReturn(new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of()));

        // List → 단일 반환으로 변경
        ProductMatchDto result = matchScoreService.score(product, request);

        // 상품 하나로 합쳐져서 단일 반환
        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.productName()).isEqualTo("청년우대적금");

        // maxMonthlyLimit이 더 큰 secondProperty(11L)가 최고 점수
        // 희망 40만 > 한도 30만(10L) → depositScore 감점
        // 희망 40만 ≤ 한도 50만(11L) → depositScore 만점
        assertThat(result.productPropertyId()).isEqualTo(11L);
        assertThat(result.providerName()).isEqualTo("테스트은행B");
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
}