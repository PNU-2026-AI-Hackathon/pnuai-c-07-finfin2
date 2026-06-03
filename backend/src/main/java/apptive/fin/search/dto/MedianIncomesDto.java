package apptive.fin.search.dto;

import apptive.fin.search.entity.MedianIncome;
import lombok.Builder;

import java.util.List;

@Builder
public record MedianIncomesDto(
        Integer year,
        Integer householdSize,
        Integer p60,
        Integer p80,
        Integer p100,
        Integer p120,
        Integer p150,
        Integer p180
) {
    public static MedianIncomesDto from(List<MedianIncome> medianIncomes) {
        MedianIncomesDtoBuilder builder = MedianIncomesDto.builder();

        builder.year(medianIncomes.getFirst().getYear());
        builder.householdSize(medianIncomes.getFirst().getHouseholdSize());

        for (MedianIncome income : medianIncomes) {
            switch (income.getEarnPercent()) {
                case 60 -> builder.p60(income.getMonthlyIncome());
                case 80 -> builder.p80(income.getMonthlyIncome());
                case 100 -> builder.p100(income.getMonthlyIncome());
                case 120 -> builder.p120(income.getMonthlyIncome());
                case 150 -> builder.p150(income.getMonthlyIncome());
                case 180 -> builder.p180(income.getMonthlyIncome());
            }
        }

        return builder.build();
    }

    public boolean isEmpty() {
        return p60 == 0 && p80 == 0 && p100 == 0
                && p120 == 0 && p150 == 0 && p180 == 0;
    }
}
