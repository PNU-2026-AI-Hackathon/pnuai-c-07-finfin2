package apptive.fin.search.service;

import apptive.fin.category.service.CategoryOptionService;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.dto.DynamicFormResponseDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.SearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class DynamicFormService {
    private final MedianIncomeService medianIncomeService;
    private final CategoryOptionService categoryOptionService;

    public DynamicFormResponseDto calcFormCondition(SearchRequestDto searchRequestDto) {

        List<KeywordValueEnum> keywords = optionsToKeywords(searchRequestDto.options());
        var builder = DynamicFormResponseDto.builder();

        for (KeywordValueEnum keyword : keywords) {
            switch (keyword) {
                // 현재 신분이 미취업이면 연소득 기본값을 0으로, 근속연수 메뉴를 숨기도록 설정한다.
                case KeywordValueEnum.STATUS_UNEMPLOYED -> builder.yearlyEarnDefault(0).showTenure(false);
            }
        }

        // 사용자가 입력한 가구원 수에 따라 중위소득 데이터를 반환한다
        if (searchRequestDto.detailedOptions().householdSize() != null) {
            int currentYear = Year.now(ZoneId.of("Asia/Seoul")).getValue();
            builder.medianIncomes(
                    medianIncomeService.getMedianIncomesDto(
                            currentYear,
                            searchRequestDto.detailedOptions().householdSize()
                    )
            );
        }

        return builder.build();
    }

    private List<KeywordValueEnum> optionsToKeywords(List<OptionRequestDto> options) {
        Map<Long, KeywordValueEnum> mapping = categoryOptionService.getOptionMap();

        return options.stream()
                .map((e)->mapping.get(e.optionId()))
                .filter(Objects::nonNull)
                .toList();
    }

}
