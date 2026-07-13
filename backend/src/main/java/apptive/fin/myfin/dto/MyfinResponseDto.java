package apptive.fin.myfin.dto;

import java.util.List;

public record MyfinResponseDto() {

    // GET/Myfin : 전체 찜핀 모두 응답
    public record List_(
            List<Item> items,
            boolean showComparisonNotice
    ) {}

    public record Item(
            Long MyfinId,
            Long productPropertyId,
            String sourceCode,  // "ONTONG" | "FSS"

            String productCode,
            String productName,
            String saveTrm, // product_properties.save_trm (FSS만 값 존재)

            Integer fitScore,            // 프로필 기준 재산출
            List<String> keywords,
            String summaryLine,          // 카드 중앙에 표기 ex) 정부 지원 · 3년 만기 · 월 1만~50만원 / NH농협은행 · 1년 만기 · 월 1천~30만원

            Metrics metrics,
            String calcBasisCaption,

            boolean excludeFromRateComparison,
            String applyStatus,

            String applyUrl
            //TODO: String resolutionLevel    : Y4-3 아웃링크 해소 함수 재사용
    ) {}

    public record Metrics(
            // 정부(ONTONG) - product_properties 컬럼 기반 계산
            Double contributionYieldRate, // gov_contribution_rate/matching_ratio 등으로 계산
            Long expectedMaturityAmount,

            // 은행(FSS) - product_properties 컬럼
            Double baseRate,
            Double maxRate,
            Double achievableRate   // 우대금리(product_preferential_rates) 반영
    ) {}

}
