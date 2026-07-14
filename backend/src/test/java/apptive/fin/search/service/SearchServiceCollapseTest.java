package apptive.fin.search.service;

import apptive.fin.search.dto.ProductMatchDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 상품별 최고점 (Product, ProductProperty) 쌍만 남기고 총점 내림차순 정렬하는 리듀서 단위 테스트.
// (기존 MatchScoreService의 property 반복-최고점 선택 로직이 SearchService로 일원화되면서 이관됨)
class SearchServiceCollapseTest {

    private ProductMatchDto match(long productId, long propertyId, double totalScore) {
        return ProductMatchDto.builder()
                .productId(productId)
                .productPropertyId(propertyId)
                .totalScore(totalScore)
                .build();
    }

    @Test
    void 같은_상품의_여러_property_중_총점이_가장_높은_하나만_남는다() {
        // 상품 1L에 property 10L(총점 60), 11L(총점 80) → 더 높은 11L만 생존
        List<ProductMatchDto> result = SearchService.collapseToBestPerProduct(List.of(
                match(1L, 10L, 60.0),
                match(1L, 11L, 80.0)
        ).stream());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).productPropertyId()).isEqualTo(11L);
        assertThat(result.get(0).totalScore()).isEqualTo(80.0);
    }

    @Test
    void 총점이_동점이면_먼저_계산된_property가_생존한다() {
        // >= 병합이므로 동점 시 먼저 들어온 10L 유지
        List<ProductMatchDto> result = SearchService.collapseToBestPerProduct(List.of(
                match(1L, 10L, 70.0),
                match(1L, 11L, 70.0)
        ).stream());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).productPropertyId()).isEqualTo(10L);
    }

    @Test
    void 여러_상품은_각각_최고점만_남기고_총점_내림차순으로_정렬된다() {
        List<ProductMatchDto> result = SearchService.collapseToBestPerProduct(List.of(
                match(1L, 10L, 60.0),
                match(1L, 11L, 90.0), // 상품 1L 최고점 90
                match(2L, 20L, 75.0), // 상품 2L 최고점 75
                match(2L, 21L, 50.0)
        ).stream());

        assertThat(result).hasSize(2);
        // 총점 내림차순: 상품1L(90) → 상품2L(75)
        assertThat(result.get(0).productId()).isEqualTo(1L);
        assertThat(result.get(0).totalScore()).isEqualTo(90.0);
        assertThat(result.get(1).productId()).isEqualTo(2L);
        assertThat(result.get(1).totalScore()).isEqualTo(75.0);
    }

    @Test
    void 빈_스트림이면_빈_리스트를_반환한다() {
        assertThat(SearchService.collapseToBestPerProduct(List.<ProductMatchDto>of().stream())).isEmpty();
    }
}
