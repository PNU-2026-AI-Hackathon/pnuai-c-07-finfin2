package apptive.fin.search.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record DynamicFormResponseDto(
    Boolean showTenure,
    Integer ageBound,
    Integer yearlyEarnDefault,
    Boolean showBankInterestRateCheckList,
    MedianIncomesDto medianIncomes,
    List<PreferentialInterestRateOption> preferentialInterestRateOptions
) {

    public DynamicFormResponseDto {
        if (showTenure == null) showTenure = true;
        if (ageBound == null) ageBound = 34;
        // if (yearlyEarnDefault == null);
        if (showBankInterestRateCheckList == null) showBankInterestRateCheckList = false;
        // if (medianIncomes == null) medianIncomes = null;
        if (preferentialInterestRateOptions == null) preferentialInterestRateOptions = List.of();
    }

}
