package apptive.fin.search;

import apptive.fin.search.enums.CategoryIdEnum;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.category.service.CategoryOptionService;
import apptive.fin.category.service.CategoryOptionService.OptionMapping;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.service.ResolveKeywordService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResolveKeywordServiceTest {

    @Test
    void 옵션을_카테고리별_키워드로_분류한다() {
        CategoryOptionService categoryOptionService = mock(CategoryOptionService.class);
        ResolveKeywordService resolveKeywordService = new ResolveKeywordService(categoryOptionService);
        when(categoryOptionService.getOptionMappings()).thenReturn(Map.of(
                1L, mapping(CategoryIdEnum.REGION, KeywordValueEnum.REGION_SEOUL),
                2L, mapping(CategoryIdEnum.IDENTITY, KeywordValueEnum.STATUS_UNEMPLOYED),
                3L, mapping(CategoryIdEnum.PERIOD, KeywordValueEnum.TERM_AROUND_1_YEAR),
                4L, mapping(CategoryIdEnum.BENEFIT, KeywordValueEnum.BENEFIT_MAX_INTEREST),
                5L, mapping(CategoryIdEnum.BANK_COND, KeywordValueEnum.BANK_SALARY_TRANSFER),
                6L, mapping(CategoryIdEnum.INTEREST, KeywordValueEnum.INTEREST_SAVINGS)
        ));

        ResolvedKeywords result = resolveKeywordService.resolveKeywords(List.of(
                new OptionRequestDto(CategoryIdEnum.REGION.getId(), 1L),
                new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 2L),
                new OptionRequestDto(CategoryIdEnum.PERIOD.getId(), 3L),
                new OptionRequestDto(CategoryIdEnum.BENEFIT.getId(), 4L),
                new OptionRequestDto(CategoryIdEnum.BANK_COND.getId(), 5L),
                new OptionRequestDto(CategoryIdEnum.INTEREST.getId(), 6L)
        ));

        assertThat(result.regions()).containsExactly(KeywordValueEnum.REGION_SEOUL);
        assertThat(result.identities()).containsExactly(KeywordValueEnum.STATUS_UNEMPLOYED);
        assertThat(result.savingPeriod()).isEqualTo(KeywordValueEnum.TERM_AROUND_1_YEAR);
        assertThat(result.coreBenefits()).containsExactly(KeywordValueEnum.BENEFIT_MAX_INTEREST);
        assertThat(result.bankConditions()).containsExactly(KeywordValueEnum.BANK_SALARY_TRANSFER);
    }

    @Test
    void 옵션이_요청한_카테고리에_속하지_않으면_거절한다() {
        CategoryOptionService categoryOptionService = mock(CategoryOptionService.class);
        ResolveKeywordService resolveKeywordService = new ResolveKeywordService(categoryOptionService);
        when(categoryOptionService.getOptionMappings()).thenReturn(Map.of(
                1L, mapping(CategoryIdEnum.PERIOD, KeywordValueEnum.TERM_AROUND_1_YEAR)
        ));

        assertThatThrownBy(() -> resolveKeywordService.resolveKeywords(List.of(
                new OptionRequestDto(CategoryIdEnum.BANK_COND.getId(), 1L)
        )))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
                        .isEqualTo(SearchErrorCode.OPTION_CATEGORY_NOT_FOUND));
    }

    private static OptionMapping mapping(CategoryIdEnum category, KeywordValueEnum keyword) {
        return new OptionMapping(category.getId(), keyword);
    }
}
