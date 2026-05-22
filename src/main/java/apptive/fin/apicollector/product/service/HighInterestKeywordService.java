package apptive.fin.apicollector.product.service;

import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.repository.ProductPropertyRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HighInterestKeywordService {

    private static final KeywordValueEnum HIGH_INTEREST_KEYWORD = KeywordValueEnum.BENEFIT_MAX_INTEREST;

    private final ProductPropertyRepository productPropertyRepository;

    @Transactional
    public HighInterestKeywordUpdateResult refreshHighInterestKeywords() {
        List<BigDecimal> rates = productPropertyRepository.findJoinableMaxRatesOrderByMaxRate();
        BigDecimal median = calculateMedian(rates);
        String keywordCode = HIGH_INTEREST_KEYWORD.name();

        if (median == null) {
            int removed = productPropertyRepository.deleteHighInterestKeywords(keywordCode);
            return new HighInterestKeywordUpdateResult(median, rates.size(), 0, removed);
        }

        int removed = productPropertyRepository.deleteHighInterestKeywordsNotExceedingMedian(keywordCode, median);
        int added = productPropertyRepository.insertMissingHighInterestKeywords(keywordCode, median);

        return new HighInterestKeywordUpdateResult(median, rates.size(), added, removed);
    }

    private @Nullable BigDecimal calculateMedian(List<BigDecimal> sortedRates) {
        if (sortedRates.isEmpty()) {
            return null;
        }

        int size = sortedRates.size();
        int middle = size / 2;
        if (size % 2 == 1) {
            return sortedRates.get(middle);
        }

        return sortedRates.get(middle - 1)
                .add(sortedRates.get(middle))
                .divide(BigDecimal.valueOf(2), 4, RoundingMode.HALF_UP);
    }

    public record HighInterestKeywordUpdateResult(
            @Nullable BigDecimal median,
            int rateCount,
            int addedCount,
            int removedCount
    ) {
    }
}
