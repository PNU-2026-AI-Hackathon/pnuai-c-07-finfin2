package apptive.fin.search.service;

import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityFilterService {

    private final ProductRepository productRepository;
    private final ResolveKeywordService resolveKeywordService;

    public List<Product> filterEligible(SearchRequestDto request){
        var detail = request.detailedOptions();
        // TODO : QueryDSL 도입
        if (detail == null) return List.of();

        ResolvedKeywords keywords = resolveKeywordService.resolveKeywords(request.options());

        Integer age = detail.birthdate() != null
                ? Period.between(detail.birthdate(), LocalDate.now()).getYears()
                : null;

        Long annualIncome = detail.annualIncome();

        Boolean isHomeless = detail.isHomeless();
        Boolean isHouseholder = detail.isHouseholder();

        Integer tenureMonths = new HashSet<>(keywords.identities())
                .contains(KeywordValueEnum.STATUS_UNEMPLOYED)
                ? Integer.valueOf(0)
                : detail.tenureMonths();


        Long monthlyDeposit = detail.monthlySavingsGoal();

        return productRepository.findEligibleProducts(
                age, annualIncome, isHomeless,
                isHouseholder,tenureMonths, monthlyDeposit
        );

    }
}
