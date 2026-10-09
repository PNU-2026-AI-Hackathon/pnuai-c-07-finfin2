package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductCategoryEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 대분류 라우팅 테스트.
 * 저축기간 키워드(TERM_*_MONTH)에 따라 대분류가 결정되는지 검증.
 */
class CategoryRoutingTest {

    @Test
    void saveTrm_1개월이면_단기예치_대분류다() {
        assertThat(ProductCategoryEnum.fromSaveTrm(1)).isEqualTo(ProductCategoryEnum.SHORT_TERM);
    }

    @Test
    void saveTrm_3개월이면_단기예치_대분류다() {
        assertThat(ProductCategoryEnum.fromSaveTrm(3)).isEqualTo(ProductCategoryEnum.SHORT_TERM);
    }

    @Test
    void saveTrm_6개월이면_목돈만들기_대분류다() {
        assertThat(ProductCategoryEnum.fromSaveTrm(6)).isEqualTo(ProductCategoryEnum.LONG_TERM);
    }

    @Test
    void saveTrm_12개월이면_목돈만들기_대분류다() {
        assertThat(ProductCategoryEnum.fromSaveTrm(12)).isEqualTo(ProductCategoryEnum.LONG_TERM);
    }

    @Test
    void saveTrm_null이면_기본값_목돈만들기다() {
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
    void 단기예치_정규화_월납입액은_예치액을_기간으로_나눈다() {
        DetailedOptionsDto dto = new DetailedOptionsDto(
                null, null, null, null, null,
                null, null, null,
                null, 3000000L,  // depositAmount=300만
                null, null, List.of()
        );

        // 저축기간 3개월: 300만 / 3 = 100만
        assertThat(dto.normalizedMonthlyDeposit(3)).isEqualTo(1000000L);
    }

    @Test
    void 목돈만들기_정규화_예치원금은_월저축액에_기간을_곱한다() {
        DetailedOptionsDto dto = new DetailedOptionsDto(
                null, null, null, null, null,
                null, null, null,
                500000L, null,  // monthlySavingsGoal=50만
                null, null, List.of()
        );

        // 저축기간 12개월: 50만 × 12 = 600만
        assertThat(dto.normalizedDepositPrincipal(12)).isEqualTo(6000000L);
    }

    @Test
    void SearchRequestDto_대분류_판별은_키워드_기반이다() {
        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null,
                        null, 1000000L,
                        null, null, List.of()
                )
        );

        // 키워드로 저축기간 결정
        ResolvedKeywords shortTermKeywords = new ResolvedKeywords(
                List.of(), List.of(),
                KeywordValueEnum.TERM_1_MONTH,
                List.of(), List.of()
        );
        ResolvedKeywords longTermKeywords = new ResolvedKeywords(
                List.of(), List.of(),
                KeywordValueEnum.TERM_12_MONTH,
                List.of(), List.of()
        );

        assertThat(request.isShortTerm(shortTermKeywords)).isTrue();
        assertThat(request.isLongTerm(shortTermKeywords)).isFalse();

        assertThat(request.isShortTerm(longTermKeywords)).isFalse();
        assertThat(request.isLongTerm(longTermKeywords)).isTrue();

        assertThat(request.depositAmount()).isEqualTo(1000000L);
    }
}
