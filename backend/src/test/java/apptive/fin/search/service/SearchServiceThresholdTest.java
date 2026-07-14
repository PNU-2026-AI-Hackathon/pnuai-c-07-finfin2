package apptive.fin.search.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// [A] #최고이율_중심 상위 30% 컷 금리 계산 로직 단위 테스트.
class SearchServiceThresholdTest {

    @Test
    void 상위30퍼센트_컷금리는_5개중_두번째로_높은_금리다() {
        // 5개 → ceil(5×0.3)=2, cutoffIndex=1 → 내림차순 2번째(=4.0)가 컷. 4.0 이상이 상위 30%(반올림).
        Double threshold = SearchService.computeTopRateThreshold(List.of(1.0, 2.0, 3.0, 4.0, 5.0));

        assertThat(threshold).isEqualTo(4.0);
    }

    @Test
    void 동점이_있어도_컷금리는_같고_그_이상은_모두_상위권에_포함된다() {
        // 5,4,4,3,2 → cutoffIndex=1 → 컷 4.0. 4.0 이상은 3개(동점 포함).
        Double threshold = SearchService.computeTopRateThreshold(List.of(2.0, 3.0, 4.0, 4.0, 5.0));

        assertThat(threshold).isEqualTo(4.0);
    }

    @Test
    void 금리가_하나도_없으면_null을_반환해_정적태그로_폴백한다() {
        assertThat(SearchService.computeTopRateThreshold(List.of())).isNull();
        assertThat(SearchService.computeTopRateThreshold(null)).isNull();
    }

    @Test
    void 금리가_하나면_그_값이_컷금리다() {
        assertThat(SearchService.computeTopRateThreshold(List.of(3.5))).isEqualTo(3.5);
    }
}
