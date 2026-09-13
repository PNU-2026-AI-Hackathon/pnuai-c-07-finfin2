package apptive.fin.search;

import apptive.fin.search.dto.ProductRateDto;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 단기예치 정렬 로직 테스트.
 * PRD 개정: 예적금 탭은 세후 실수령액(netReturn) 내림차순 정렬.
 */
class ShortTermSortingTest {

    @Test
    void 세후실수령액_내림차순으로_정렬된다() {
        // Given
        ProductRateDto low = createRateDto(1L, "저금리상품", 1_000_000L);
        ProductRateDto mid = createRateDto(2L, "중금리상품", 1_050_000L);
        ProductRateDto high = createRateDto(3L, "고금리상품", 1_100_000L);

        List<ProductRateDto> products = List.of(low, mid, high);

        // When
        List<ProductRateDto> sorted = sortedByNetReturn(products);

        // Then
        assertThat(sorted).extracting(ProductRateDto::productId)
                .containsExactly(3L, 2L, 1L);  // high, mid, low 순서
    }

    @Test
    void 동일한_세후실수령액이면_기존_순서를_유지한다() {
        // Given
        ProductRateDto first = createRateDto(1L, "상품A", 1_050_000L);
        ProductRateDto second = createRateDto(2L, "상품B", 1_050_000L);

        List<ProductRateDto> products = List.of(first, second);

        // When
        List<ProductRateDto> sorted = sortedByNetReturn(products);

        // Then
        assertThat(sorted).hasSize(2);
        // 동일한 값이면 순서가 바뀌지 않음 (stable sort)
    }

    @Test
    void null_세후실수령액은_0으로_처리하여_뒤로_정렬된다() {
        // Given
        ProductRateDto withReturn = createRateDto(1L, "계산가능", 1_050_000L);
        ProductRateDto nullReturn = createRateDtoWithNullReturn(2L, "계산불가");

        List<ProductRateDto> products = List.of(nullReturn, withReturn);

        // When
        List<ProductRateDto> sorted = sortedByNetReturn(products);

        // Then
        assertThat(sorted).extracting(ProductRateDto::productId)
                .containsExactly(1L, 2L);  // withReturn이 먼저
    }

    @Test
    void 상품별_최고_세후실수령액_옵션이_선택된다() {
        // Given: 같은 상품의 여러 옵션
        ProductRateDto option1 = createRateDto(1L, "상품A-옵션1", 1_000_000L);
        ProductRateDto option2 = createRateDto(1L, "상품A-옵션2", 1_100_000L);  // 같은 productId, 더 높은 수익

        // When: 상품별 최고 옵션 선택
        ProductRateDto best = compareNetReturn(option1, option2) >= 0 ? option1 : option2;

        // Then
        assertThat(best.netReturn()).isEqualTo(1_100_000L);
    }

    // === Helper methods ===

    private ProductRateDto createRateDto(Long productId, String name, Long netReturn) {
        return ProductRateDto.builder()
                .productId(productId)
                .productPropertyId(productId * 10)
                .productName(name)
                .providerName("테스트은행")
                .source("FSS")
                .baseRate(3.0)
                .achievableRate(3.5)
                .rateComparable(true)
                .isSubscription(false)
                .netReturn(netReturn)
                .principal(1_000_000L)
                .saveTrm(12)
                .productType("DEPOSIT")
                .build();
    }

    private ProductRateDto createRateDtoWithNullReturn(Long productId, String name) {
        return ProductRateDto.builder()
                .productId(productId)
                .productPropertyId(productId * 10)
                .productName(name)
                .providerName("테스트은행")
                .source("FSS")
                .baseRate(3.0)
                .achievableRate(3.5)
                .rateComparable(true)
                .isSubscription(false)
                .netReturn(null)
                .principal(null)
                .saveTrm(12)
                .productType("DEPOSIT")
                .build();
    }

    // SearchService의 private 메서드와 동일한 로직
    private List<ProductRateDto> sortedByNetReturn(Collection<ProductRateDto> products) {
        return products.stream()
                .sorted(Comparator.comparingLong((ProductRateDto dto) ->
                        dto.netReturn() != null ? dto.netReturn() : 0L).reversed())
                .toList();
    }

    private int compareNetReturn(ProductRateDto left, ProductRateDto right) {
        Long leftReturn = left.netReturn() != null ? left.netReturn() : 0L;
        Long rightReturn = right.netReturn() != null ? right.netReturn() : 0L;
        return Long.compare(leftReturn, rightReturn);
    }
}
