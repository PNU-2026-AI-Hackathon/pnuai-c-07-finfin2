package apptive.fin.search.repository;


import apptive.fin.search.dto.MedianIncomesDto;
import apptive.fin.search.entity.MedianIncome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedianIncomeRepository extends JpaRepository<MedianIncome, Long> {
    List<MedianIncome> findAllByYearAndHouseholdSize(int year, int householdSize);
}
