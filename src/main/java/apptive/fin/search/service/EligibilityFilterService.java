package apptive.fin.search.service;

import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityFilterService {

    private final ProductRepository productRepository;

    public List<Product> filterEligible(SearchRequestDto request){
        var detail = request.detailedOptions();

        // TODO : QueryDSL 도입
        if (detail == null) return List.of();

        int age = detail.birthdate() != null
                ? Period.between(detail.birthdate(), LocalDate.now()).getYears()
                : 0;

        long annualIncome = detail.annualIncome() != null
                ? detail.annualIncome()
                : 0L;

        boolean isHomeless = Boolean.TRUE.equals(detail.isHomeless());
        boolean isHouseholder = Boolean.TRUE.equals(detail.isHouseholder());

        int tenureMonths =Boolean.TRUE.equals(detail.isFirstJob())
                ? -1
                : (detail.tenureMonths() != null ? detail.tenureMonths() : 0);

        long monthlyDeposit = detail.monthlySavingsGoal() != null
                ? detail.monthlySavingsGoal()
                : 0L;

        return productRepository.findEligibleProducts(
                age, annualIncome, isHomeless,
                isHouseholder,tenureMonths, monthlyDeposit
        );

    }
}
