package apptive.fin.search.service;

import apptive.fin.category.service.CategoryOptionService;
import apptive.fin.search.KeywordValueEnum;
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
                // 현재 신분이 군복무이면 생년월일 상한을 39로 확장한다.
                case KeywordValueEnum.STATUS_MILITARY ->  builder.ageBound(39);
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

        // 주거래 은행을 선택하면 은행의 우대금리 목록을 노출한다
        if (searchRequestDto.detailedOptions().mainBanks() != null &&
            !searchRequestDto.detailedOptions().mainBanks().isEmpty()
        )
            builder.showBankInterestRateCheckList(true);


        // 추후 은행 상품으로 확장시 우대금리 조건 추가...
//        if (searchRequestDto.detailedOptions().mainBanks() != null &&
//                !searchRequestDto.detailedOptions().mainBanks().isEmpty()) {
//
//        }
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
