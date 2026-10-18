package apptive.fin.search.service;

import apptive.fin.search.dto.MedianIncomesDto;
import apptive.fin.search.entity.MedianIncome;
import apptive.fin.search.repository.MedianIncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedianIncomeService {
    private final MedianIncomeRepository medianIncomeRepository;

    @Cacheable(value = "medianIncome", unless = "#result.isEmpty()")
    public MedianIncomesDto getMedianIncomesDto(int year, int householdSize) {
        List<MedianIncome> medianIncomes = medianIncomeRepository.findAllByYearAndHouseholdSize(year, householdSize);

        if (medianIncomes.isEmpty()) {
            return MedianIncomesDto.builder()
                    .year(year)
                    .householdSize(householdSize)
                    .p60(0).p80(0).p100(0).p120(0).p150(0).p180(0)
                    .build();
        }

        return MedianIncomesDto.from(medianIncomes);
    }

}
