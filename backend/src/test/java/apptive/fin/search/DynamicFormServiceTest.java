package apptive.fin.search;

import apptive.fin.category.service.CategoryOptionService;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.DynamicFormResponseDto;
import apptive.fin.search.dto.MedianIncomesDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.service.DynamicFormService;
import apptive.fin.search.service.MedianIncomeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicFormServiceTest {

    @Mock
    private MedianIncomeService medianIncomeService;

    @Mock
    private CategoryOptionService categoryOptionService;

    @InjectMocks
    private DynamicFormService dynamicFormService;

    @Test
    void 미취업_옵션이면_연소득_기본값을_0으로_근속기간을_숨김으로_설정한다() {
        SearchRequestDto request = createRequest(
                List.of(new OptionRequestDto(1L, 10L)),
                null,
                List.of()
        );

        when(categoryOptionService.getOptionMap()).thenReturn(Map.of(
                10L, KeywordValueEnum.STATUS_UNEMPLOYED
        ));

        DynamicFormResponseDto result = dynamicFormService.calcFormCondition(request);

        assertThat(result.yearlyEarnDefault()).isEqualTo(0);
        assertThat(result.showTenure()).isEqualTo(false);
        assertThat(result.showTenure()).isFalse();
        assertThat(result.ageBound()).isEqualTo(34);
        assertThat(result.showBankInterestRateCheckList()).isFalse();
        assertThat(result.medianIncomes()).isNull();
        assertThat(result.preferentialInterestRateOptions()).isEmpty();

        verifyNoInteractions(medianIncomeService);
    }

    @Test
    void 군복무_옵션이면_나이상한을_39로_설정한다() {
        SearchRequestDto request = createRequest(
                List.of(new OptionRequestDto(1L, 20L)),
                null,
                List.of()
        );

        when(categoryOptionService.getOptionMap()).thenReturn(Map.of(
                20L, KeywordValueEnum.STATUS_MILITARY
        ));

        DynamicFormResponseDto result = dynamicFormService.calcFormCondition(request);

        assertThat(result.ageBound()).isEqualTo(39);
        assertThat(result.yearlyEarnDefault()).isNull();
        assertThat(result.showBankInterestRateCheckList()).isFalse();
        assertThat(result.medianIncomes()).isNull();

        verifyNoInteractions(medianIncomeService);
    }

    @Test
    void 가구원수가_있으면_현재연도와_가구원수로_중위소득을_조회한다() {
        int currentYear = Year.now(ZoneId.of("Asia/Seoul")).getValue();
        MedianIncomesDto medianIncomesDto = createMedianIncomesDto(currentYear, 3);
        SearchRequestDto request = createRequest(List.of(), 3, List.of());

        when(categoryOptionService.getOptionMap()).thenReturn(Map.of());
        when(medianIncomeService.getMedianIncomesDto(currentYear, 3)).thenReturn(medianIncomesDto);

        DynamicFormResponseDto result = dynamicFormService.calcFormCondition(request);

        assertThat(result.medianIncomes()).isEqualTo(medianIncomesDto);
        assertThat(result.showBankInterestRateCheckList()).isFalse();

        verify(medianIncomeService).getMedianIncomesDto(currentYear, 3);
    }

    @Test
    void 가구원수가_없으면_중위소득을_조회하지_않는다() {
        SearchRequestDto request = createRequest(List.of(), null, List.of());

        when(categoryOptionService.getOptionMap()).thenReturn(Map.of());

        DynamicFormResponseDto result = dynamicFormService.calcFormCondition(request);

        assertThat(result.medianIncomes()).isNull();

        verifyNoInteractions(medianIncomeService);
    }

    @Test
    void 주거래은행이_있으면_우대금리_체크리스트를_노출한다() {
        SearchRequestDto request = createRequest(List.of(), null, List.of("KB"));

        when(categoryOptionService.getOptionMap()).thenReturn(Map.of());

        DynamicFormResponseDto result = dynamicFormService.calcFormCondition(request);

        assertThat(result.showBankInterestRateCheckList()).isTrue();

        verifyNoInteractions(medianIncomeService);
    }

    @Test
    void 주거래은행이_null이면_우대금리_체크리스트를_노출하지_않는다() {
        SearchRequestDto request = createRequest(List.of(), null, null);

        when(categoryOptionService.getOptionMap()).thenReturn(Map.of());

        DynamicFormResponseDto result = dynamicFormService.calcFormCondition(request);

        assertThat(result.showBankInterestRateCheckList()).isFalse();

        verifyNoInteractions(medianIncomeService);
    }

    @Test
    void 키워드로_매핑되지_않는_옵션은_무시한다() {
        SearchRequestDto request = createRequest(
                List.of(new OptionRequestDto(1L, 999L)),
                null,
                List.of()
        );

        when(categoryOptionService.getOptionMap()).thenReturn(Map.of());

        DynamicFormResponseDto result = dynamicFormService.calcFormCondition(request);

        assertThat(result.showTenure()).isTrue();
        assertThat(result.ageBound()).isEqualTo(34);
        assertThat(result.yearlyEarnDefault()).isNull();
        assertThat(result.showBankInterestRateCheckList()).isFalse();
        assertThat(result.medianIncomes()).isNull();

        verifyNoInteractions(medianIncomeService);
    }

    @Test
    void 추가조건이_없으면_기본값으로_응답한다() {
        SearchRequestDto request = createRequest(List.of(), null, List.of());

        when(categoryOptionService.getOptionMap()).thenReturn(Map.of());

        DynamicFormResponseDto result = dynamicFormService.calcFormCondition(request);

        assertThat(result.showTenure()).isTrue();
        assertThat(result.ageBound()).isEqualTo(34);
        assertThat(result.yearlyEarnDefault()).isNull();
        assertThat(result.showBankInterestRateCheckList()).isFalse();
        assertThat(result.medianIncomes()).isNull();
        assertThat(result.preferentialInterestRateOptions()).isEmpty();

        verifyNoInteractions(medianIncomeService);
    }

    @Test
    void 복합조건이_들어오면_여러_분기를_동시에_반영한다() {
        int currentYear = Year.now(ZoneId.of("Asia/Seoul")).getValue();
        MedianIncomesDto medianIncomesDto = createMedianIncomesDto(currentYear, 3);
        SearchRequestDto request = createRequest(
                List.of(
                        new OptionRequestDto(1L, 10L),
                        new OptionRequestDto(2L, 20L)
                ),
                3,
                List.of("KB")
        );

        when(categoryOptionService.getOptionMap()).thenReturn(Map.of(
                10L, KeywordValueEnum.STATUS_UNEMPLOYED,
                20L, KeywordValueEnum.STATUS_MILITARY
        ));
        when(medianIncomeService.getMedianIncomesDto(currentYear, 3)).thenReturn(medianIncomesDto);

        DynamicFormResponseDto result = dynamicFormService.calcFormCondition(request);

        assertThat(result.yearlyEarnDefault()).isEqualTo(0);
        assertThat(result.ageBound()).isEqualTo(39);
        assertThat(result.showBankInterestRateCheckList()).isTrue();
        assertThat(result.medianIncomes()).isEqualTo(medianIncomesDto);
        assertThat(result.showTenure()).isFalse();
        assertThat(result.preferentialInterestRateOptions()).isEmpty();

        verify(medianIncomeService).getMedianIncomesDto(currentYear, 3);
    }

    private SearchRequestDto createRequest(List<OptionRequestDto> options, Integer householdSize, List<String> mainBanks) {
        return new SearchRequestDto(options, createDetailedOptions(householdSize, mainBanks));
    }

    private DetailedOptionsDto createDetailedOptions(Integer householdSize, List<String> mainBanks) {
        return new DetailedOptionsDto(
                LocalDate.of(2000, 1, 1),
                30_000_000L,
                householdSize,
                null,
                null,
                null,
                null,
                null,
                null,
                mainBanks,
                List.of()
        );
    }

    private MedianIncomesDto createMedianIncomesDto(int year, int householdSize) {
        return MedianIncomesDto.builder()
                .year(year)
                .householdSize(householdSize)
                .p60(200)
                .p80(300)
                .p100(400)
                .p120(500)
                .p150(600)
                .p180(700)
                .build();
    }
}
