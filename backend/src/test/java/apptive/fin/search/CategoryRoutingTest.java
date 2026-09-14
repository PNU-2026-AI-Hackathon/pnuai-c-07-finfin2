package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductCategoryEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRoutingTest {

    @Test
    void saveTrmExact가_1개월이면_단기예치_대분류다() {
        assertThat(ProductCategoryEnum.fromSaveTrm(1)).isEqualTo(ProductCategoryEnum.SHORT_TERM);
    }

    @Test
    void saveTrmExact가_3개월이면_단기예치_대분류다() {
        assertThat(ProductCategoryEnum.fromSaveTrm(3)).isEqualTo(ProductCategoryEnum.SHORT_TERM);
    }

    @Test
    void saveTrmExact가_6개월이면_목돈만들기_대분류다() {
        assertThat(ProductCategoryEnum.fromSaveTrm(6)).isEqualTo(ProductCategoryEnum.LONG_TERM);
    }

    @Test
    void saveTrmExact가_12개월이면_목돈만들기_대분류다() {
        assertThat(ProductCategoryEnum.fromSaveTrm(12)).isEqualTo(ProductCategoryEnum.LONG_TERM);
    }

    @Test
    void saveTrmExact가_null이면_기본값_목돈만들기다() {
        assertThat(ProductCategoryEnum.fromSaveTrm(null)).isEqualTo(ProductCategoryEnum.LONG_TERM);
    }

    @Test
    void TERM_1_MONTH_키워드는_단기예치다() {
        assertThat(ProductCategoryEnum.fromKeyword(KeywordValueEnum.TERM_1_MONTH))
                .isEqualTo(ProductCategoryEnum.SHORT_TERM);
    }

    @Test
    void TERM_3_MONTH_키워드는_단기예치다() {
        assertThat(ProductCategoryEnum.fromKeyword(KeywordValueEnum.TERM_3_MONTH))
                .isEqualTo(ProductCategoryEnum.SHORT_TERM);
    }

    @Test
    void TERM_12_MONTH_키워드는_목돈만들기다() {
        assertThat(ProductCategoryEnum.fromKeyword(KeywordValueEnum.TERM_12_MONTH))
                .isEqualTo(ProductCategoryEnum.LONG_TERM);
    }

    @Test
    void 레거시_TERM_AROUND_1_YEAR_키워드는_목돈만들기다() {
        assertThat(ProductCategoryEnum.fromKeyword(KeywordValueEnum.TERM_AROUND_1_YEAR))
                .isEqualTo(ProductCategoryEnum.LONG_TERM);
    }

    @Test
    void DetailedOptionsDto_isShortTerm_1개월() {
        DetailedOptionsDto dto = new DetailedOptionsDto(
                null, null, null, null, null,
                null, null, null,
                null, 1000000L, 1,  // depositAmount=100만, saveTrmExact=1
                null, null, List.of()
        );

        assertThat(dto.isShortTerm()).isTrue();
        assertThat(dto.isLongTerm()).isFalse();
    }

    @Test
    void DetailedOptionsDto_isLongTerm_12개월() {
        DetailedOptionsDto dto = new DetailedOptionsDto(
                null, null, null, null, null,
                null, null, null,
                500000L, null, 12,  // monthlySavingsGoal=50만, saveTrmExact=12
                null, null, List.of()
        );

        assertThat(dto.isShortTerm()).isFalse();
        assertThat(dto.isLongTerm()).isTrue();
    }

    @Test
    void 단기예치_정규화_월납입액은_예치액을_기간으로_나눈다() {
        DetailedOptionsDto dto = new DetailedOptionsDto(
                null, null, null, null, null,
                null, null, null,
                null, 3000000L, 3,  // depositAmount=300만, saveTrmExact=3
                null, null, List.of()
        );

        assertThat(dto.normalizedMonthlyDeposit()).isEqualTo(1000000L);  // 300만 / 3 = 100만
    }

    @Test
    void 목돈만들기_정규화_예치원금은_월저축액에_기간을_곱한다() {
        DetailedOptionsDto dto = new DetailedOptionsDto(
                null, null, null, null, null,
                null, null, null,
                500000L, null, 12,  // monthlySavingsGoal=50만, saveTrmExact=12
                null, null, List.of()
        );

        assertThat(dto.normalizedDepositPrincipal()).isEqualTo(6000000L);  // 50만 × 12 = 600만
    }

    @Test
    void SearchRequestDto_대분류_판별_위임() {
        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null,
                        null, 1000000L, 1,
                        null, null, List.of()
                )
        );

        assertThat(request.isShortTerm()).isTrue();
        assertThat(request.isLongTerm()).isFalse();
        assertThat(request.saveTrmExact()).isEqualTo(1);
        assertThat(request.depositAmount()).isEqualTo(1000000L);
    }
}
