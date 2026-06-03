package apptive.fin.search.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record ProductSearchResultDto (

    // 탭 A : 적합도 기준 정렬 (나에게 맞는 순)
    List<ProductMatchDto> governmentRanked,
    List<ProductMatchDto> bankRanked,

    // 탭 B : 금리 높은 순
    List<ProductRateDto> rateRanked,
    List<ProductRateDto> subscriptionProducts
){}
