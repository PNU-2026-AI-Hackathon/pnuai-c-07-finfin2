package apptive.fin.search.dto;

import apptive.fin.search.InterestRateType;

import java.util.List;

// 금리 안내 표의 한 행(= 하나의 저축기간/옵션). 잠금 시 응답에서 null.
public record RateTableRowDto(
        Integer saveTrm,
        Double baseRate,
        Double maxRate,
        InterestRateType intrRateType,
        List<PreferentialConditionDto> preferentialRates
) {
}
