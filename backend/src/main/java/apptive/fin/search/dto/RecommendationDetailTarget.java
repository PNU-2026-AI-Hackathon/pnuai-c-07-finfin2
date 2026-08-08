package apptive.fin.search.dto;

import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;

/**
 * 추천 응답에 함께 실을 상세 정보 1건의 대상.
 * 리스트 카드가 고른 (상품, 옵션) 쌍을 id가 아닌 엔티티로 그대로 들고 간다 —
 * 상세 쪽에서 재조회·대표 옵션 재선정을 거치지 않아야 카드와 같은 값이 나온다.
 */
public record RecommendationDetailTarget(
        Product product,
        ProductProperty selectedProperty
) {
}
