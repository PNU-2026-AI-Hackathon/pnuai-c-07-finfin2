package apptive.fin.search.dto;

import java.util.List;

// 상품 상세 요청. 공개 정보 조회를 위해 모든 필드는 optional.
// productPropertyId: 클릭한 리스트 행의 property. 없으면 개인화 수익 지표를 잠근다.
public record ProductDetailRequestDto(
        Long productPropertyId,
        List<OptionRequestDto> options,
        DetailedOptionsDto detailedOptions
) {
}
