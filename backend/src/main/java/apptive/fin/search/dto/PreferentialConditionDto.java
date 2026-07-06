package apptive.fin.search.dto;

import apptive.fin.search.KeywordValueEnum;

// 우대조건 1건 (금리표·은행 충족/미충족 목록에서 공용)
public record PreferentialConditionDto(
        KeywordValueEnum keywordCode,
        Double rate,
        String description
) {
}
