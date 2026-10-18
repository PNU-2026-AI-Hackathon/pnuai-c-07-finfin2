package apptive.fin.search;

import apptive.fin.search.dto.MedianIncomesDto;
import apptive.fin.search.entity.MedianIncome;
import apptive.fin.search.repository.MedianIncomeRepository;
import apptive.fin.search.service.MedianIncomeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedianIncomeServiceTest {

    @Mock
    private MedianIncomeRepository medianIncomeRepository;

    @InjectMocks
    private MedianIncomeService medianIncomeService;

    @Test
    void 중위소득_데이터가_없으면_0으로_채운_dto를_반환한다() {
        when(medianIncomeRepository.findAllByYearAndHouseholdSize(2026, 2)).thenReturn(List.of());

        MedianIncomesDto result = medianIncomeService.getMedianIncomesDto(2026, 2);

        assertThat(result.year()).isEqualTo(2026);
        assertThat(result.householdSize()).isEqualTo(2);
        assertThat(result.p60()).isZero();
        assertThat(result.p80()).isZero();
        assertThat(result.p100()).isZero();
        assertThat(result.p120()).isZero();
        assertThat(result.p150()).isZero();
        assertThat(result.p180()).isZero();
        assertThat(result.isEmpty()).isTrue();

        verify(medianIncomeRepository).findAllByYearAndHouseholdSize(2026, 2);
    }

    @Test
    void 중위소득_데이터가_있으면_비율별_금액을_dto로_변환한다() {
        when(medianIncomeRepository.findAllByYearAndHouseholdSize(2026, 3)).thenReturn(List.of(
                createMedianIncome(2026, 3, 60, 1_200_000),
                createMedianIncome(2026, 3, 80, 1_600_000),
                createMedianIncome(2026, 3, 100, 2_000_000),
                createMedianIncome(2026, 3, 120, 2_400_000),
                createMedianIncome(2026, 3, 150, 3_000_000),
                createMedianIncome(2026, 3, 180, 3_600_000)
        ));

        MedianIncomesDto result = medianIncomeService.getMedianIncomesDto(2026, 3);

        assertThat(result.year()).isEqualTo(2026);
        assertThat(result.householdSize()).isEqualTo(3);
        assertThat(result.p60()).isEqualTo(1_200_000);
        assertThat(result.p80()).isEqualTo(1_600_000);
        assertThat(result.p100()).isEqualTo(2_000_000);
        assertThat(result.p120()).isEqualTo(2_400_000);
        assertThat(result.p150()).isEqualTo(3_000_000);
        assertThat(result.p180()).isEqualTo(3_600_000);
        assertThat(result.isEmpty()).isFalse();

        verify(medianIncomeRepository).findAllByYearAndHouseholdSize(2026, 3);
    }

    private MedianIncome createMedianIncome(int year, int householdSize, int earnPercent, int monthlyIncome) {
        return MedianIncome.builder()
                .year(year)
                .householdSize(householdSize)
                .earnPercent(earnPercent)
                .monthlyIncome(monthlyIncome)
                .build();
    }
}
