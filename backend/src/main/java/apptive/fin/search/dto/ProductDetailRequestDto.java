package apptive.fin.search.dto;

import java.util.List;

// 상품 상세 요청. 모든 필드 optional (비로그인/단계2 미입력 지원 위해 SearchRequestDto와 달리 @NotNull 없음).
// productPropertyId: 클릭한 리스트 행의 property → 리스트-상세 숫자 일치의 핵심.
public record ProductDetailRequestDto(
        Long productPropertyId,
        List<OptionRequestDto> options,
        DetailedOptionsDto detailedOptions
) {
}
