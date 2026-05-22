package apptive.fin.apicollector.product.service;

import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.repository.ProductPropertyRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HighInterestKeywordServiceTest {

    private static final String HIGH_INTEREST_KEYWORD = KeywordValueEnum.BENEFIT_MAX_INTEREST.name();

    private final ProductPropertyRepository productPropertyRepository = mock(ProductPropertyRepository.class);
    private final HighInterestKeywordService service = new HighInterestKeywordService(productPropertyRepository);

    @Test
    void refreshesHighInterestKeywordsWithMedianFromOddRateCount() {
        when(productPropertyRepository.findJoinableMaxRatesOrderByMaxRate())
                .thenReturn(List.of(
                        BigDecimal.valueOf(2),
                        BigDecimal.valueOf(3),
                        BigDecimal.valueOf(4)
                ));
        when(productPropertyRepository.deleteHighInterestKeywordsNotExceedingMedian(
                HIGH_INTEREST_KEYWORD,
                BigDecimal.valueOf(3)
        )).thenReturn(2);
        when(productPropertyRepository.insertMissingHighInterestKeywords(
                HIGH_INTEREST_KEYWORD,
                BigDecimal.valueOf(3)
        )).thenReturn(1);

        HighInterestKeywordService.HighInterestKeywordUpdateResult result = service.refreshHighInterestKeywords();

        assertThat(result.median()).isEqualByComparingTo("3.00");
        assertThat(result.rateCount()).isEqualTo(3);
        assertThat(result.addedCount()).isEqualTo(1);
        assertThat(result.removedCount()).isEqualTo(2);
        verify(productPropertyRepository).deleteHighInterestKeywordsNotExceedingMedian(
                HIGH_INTEREST_KEYWORD,
                BigDecimal.valueOf(3)
        );
        verify(productPropertyRepository).insertMissingHighInterestKeywords(
                HIGH_INTEREST_KEYWORD,
                BigDecimal.valueOf(3)
        );
        verify(productPropertyRepository, never()).deleteHighInterestKeywords(HIGH_INTEREST_KEYWORD);
    }

    @Test
    void usesAverageOfTwoMiddleRatesAsMedianWhenRateCountIsEven() {
        BigDecimal expectedMedian = new BigDecimal("5.0000");
        when(productPropertyRepository.findJoinableMaxRatesOrderByMaxRate())
                .thenReturn(List.of(
                        BigDecimal.valueOf(2),
                        BigDecimal.valueOf(4),
                        BigDecimal.valueOf(6),
                        BigDecimal.valueOf(10)
                ));
        when(productPropertyRepository.deleteHighInterestKeywordsNotExceedingMedian(
                HIGH_INTEREST_KEYWORD,
                expectedMedian
        )).thenReturn(1);
        when(productPropertyRepository.insertMissingHighInterestKeywords(
                HIGH_INTEREST_KEYWORD,
                expectedMedian
        )).thenReturn(3);

        HighInterestKeywordService.HighInterestKeywordUpdateResult result = service.refreshHighInterestKeywords();

        assertThat(result.median()).isEqualByComparingTo("5.0000");
        assertThat(result.rateCount()).isEqualTo(4);
        assertThat(result.addedCount()).isEqualTo(3);
        assertThat(result.removedCount()).isEqualTo(1);
        verify(productPropertyRepository).deleteHighInterestKeywordsNotExceedingMedian(
                HIGH_INTEREST_KEYWORD,
                expectedMedian
        );
        verify(productPropertyRepository).insertMissingHighInterestKeywords(
                HIGH_INTEREST_KEYWORD,
                expectedMedian
        );
    }

    @Test
    void removesAllHighInterestKeywordsWhenThereAreNoEligibleRates() {
        when(productPropertyRepository.findJoinableMaxRatesOrderByMaxRate())
                .thenReturn(List.of());
        when(productPropertyRepository.deleteHighInterestKeywords(HIGH_INTEREST_KEYWORD))
                .thenReturn(4);

        HighInterestKeywordService.HighInterestKeywordUpdateResult result = service.refreshHighInterestKeywords();

        assertThat(result.median()).isNull();
        assertThat(result.rateCount()).isZero();
        assertThat(result.addedCount()).isZero();
        assertThat(result.removedCount()).isEqualTo(4);
        verify(productPropertyRepository).deleteHighInterestKeywords(HIGH_INTEREST_KEYWORD);
        verify(productPropertyRepository, never()).deleteHighInterestKeywordsNotExceedingMedian(any(), any());
        verify(productPropertyRepository, never()).insertMissingHighInterestKeywords(any(), any());
    }
}
