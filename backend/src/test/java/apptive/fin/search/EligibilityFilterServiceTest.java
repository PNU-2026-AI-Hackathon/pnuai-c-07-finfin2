package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.repository.ProductRepository;
import apptive.fin.search.service.EligibilityFilterService;
import apptive.fin.search.service.ResolveKeywordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EligibilityFilterServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ResolveKeywordService resolveKeywordService;

    @Test
    void 상세조건_미입력_필드는_null로_전달되어_필터_조건에서_제외된다() {
        EligibilityFilterService service = new EligibilityFilterService(productRepository, resolveKeywordService);
        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of()
                )
        );

        when(productRepository.findEligibleProducts(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(List.of());
        when(resolveKeywordService.resolveKeywords(request.options()))
                .thenReturn(emptyKeywords());

        service.filterEligible(request);

        ArgumentCaptor<Integer> ageCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Long> annualIncomeCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Boolean> homelessCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Boolean> householderCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Integer> tenureCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Long> monthlyDepositCaptor = ArgumentCaptor.forClass(Long.class);

        verify(productRepository).findEligibleProducts(
                ageCaptor.capture(),
                annualIncomeCaptor.capture(),
                homelessCaptor.capture(),
                householderCaptor.capture(),
                tenureCaptor.capture(),
                monthlyDepositCaptor.capture()
        );

        assertThat(ageCaptor.getValue()).isNull();
        assertThat(annualIncomeCaptor.getValue()).isNull();
        assertThat(homelessCaptor.getValue()).isNull();
        assertThat(householderCaptor.getValue()).isNull();
        assertThat(tenureCaptor.getValue()).isNull();
        assertThat(monthlyDepositCaptor.getValue()).isNull();
    }

    @Test
    void 미취업_키워드가_있으면_근속개월수를_0으로_전달한다() {
        EligibilityFilterService service = new EligibilityFilterService(productRepository, resolveKeywordService);
        SearchRequestDto request = new SearchRequestDto(
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 18L)),
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(25),
                        30_000_000L,
                        null,
                        null,
                        36,
                        true,
                        false,
                        true,
                        500_000L,
                        null,
                        List.of()
                )
        );

        when(productRepository.findEligibleProducts(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(List.of());
        when(resolveKeywordService.resolveKeywords(request.options()))
                .thenReturn(new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_UNEMPLOYED),
                        null,
                        List.of(),
                        List.of()
                ));

        service.filterEligible(request);

        ArgumentCaptor<Integer> tenureCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(productRepository).findEligibleProducts(
                any(), any(), any(), any(), tenureCaptor.capture(), any()
        );

        assertThat(tenureCaptor.getValue()).isZero();
    }

    @Test
    void 첫직장_필드가_true여도_미취업_키워드가_없으면_입력한_근속개월수를_그대로_전달한다() {
        EligibilityFilterService service = new EligibilityFilterService(productRepository, resolveKeywordService);
        SearchRequestDto request = new SearchRequestDto(
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 20L)),
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(25),
                        30_000_000L,
                        null,
                        null,
                        36,
                        true,
                        false,
                        true,
                        500_000L,
                        null,
                        List.of()
                )
        );

        when(productRepository.findEligibleProducts(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(List.of());
        when(resolveKeywordService.resolveKeywords(request.options()))
                .thenReturn(new ResolvedKeywords(
                        List.of(),
                        List.of(KeywordValueEnum.STATUS_SME_WORKER),
                        null,
                        List.of(),
                        List.of()
                ));

        service.filterEligible(request);

        ArgumentCaptor<Integer> tenureCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(productRepository).findEligibleProducts(
                any(), any(), any(), any(), tenureCaptor.capture(), any()
        );

        assertThat(tenureCaptor.getValue()).isEqualTo(36);
    }

    @Test
    void 상세조건이_null이면_저장소를_호출하지_않고_빈_목록을_반환한다() {
        EligibilityFilterService service = new EligibilityFilterService(productRepository, resolveKeywordService);
        SearchRequestDto request = new SearchRequestDto(List.of(), null);

        assertThat(service.filterEligible(request)).isEmpty();

        verifyNoInteractions(productRepository, resolveKeywordService);
    }

    private ResolvedKeywords emptyKeywords() {
        return new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of());
    }
}
