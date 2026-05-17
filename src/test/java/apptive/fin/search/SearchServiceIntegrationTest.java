package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.ProductSearchResultDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

@SpringBootTest
@Sql(
        scripts = "/sql/search-products.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup-product-fixtures.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class SearchServiceIntegrationTest {

    @Autowired
    private SearchService searchService;

    @Test
    void 기본상황이면_정부와_은행_상품을_적합도와_금리순으로_반환한다() {
        ProductSearchResultDto result = searchService.search(createRequest(50, List.of()));

        assertThat(matchNames(result.governmentRanked()))
                .containsExactlyInAnyOrder("청년내일채움공제", "청년우대형 청약통장");
        assertThat(matchNames(result.bankRanked()))
                .containsExactlyInAnyOrder("e-쎄이프 정기예금", "청년우대적금");

        assertThat(result.governmentRanked())
                .allSatisfy(product -> assertThat(product.totalScore()).isCloseTo(60.2941, offset(0.0001)));
        assertThat(result.bankRanked())
                .allSatisfy(product -> assertThat(product.totalScore()).isCloseTo(85.0, offset(0.0001)));

        assertThat(rateNames(result.rateRanked()))
                .containsExactly("청년내일채움공제", "청년우대적금", "e-쎄이프 정기예금");
        assertThat(rateNames(result.subscriptionProducts()))
                .containsExactly("청년우대형 청약통장");
        assertThat(result.rateRanked())
                .allSatisfy(product -> {
                    assertThat(product.productPropertyId()).isNotNull();
                    assertThat(product.providerName()).isNotBlank();
                });
    }

    @Test
    void 저축기간_1년을_선택하면_기간점수가_반영된다() {
        ProductSearchResultDto result = searchService.search(createRequest(
                50,
                List.of(new OptionRequestDto(CategoryIdEnum.PERIOD.getId(), 24L))
        ));

        ProductMatchDto oneYearBankProduct = findMatch(result.bankRanked(), "e-쎄이프 정기예금");
        ProductMatchDto adjacentGovProduct = findMatch(result.governmentRanked(), "청년내일채움공제");

        assertThat(oneYearBankProduct.periodScore()).isCloseTo(50.0, offset(0.0001));
        assertThat(oneYearBankProduct.totalScore()).isCloseTo(92.5, offset(0.0001));
        assertThat(adjacentGovProduct.periodScore()).isCloseTo(18.5185, offset(0.0001));
    }

    @Test
    void 납입한도를_초과하면_가입조건을_충족하는_상품만_남는다() {
        ProductSearchResultDto result = searchService.search(createRequest(100, List.of()));

        assertThat(result.governmentRanked()).isEmpty();
        assertThat(matchNames(result.bankRanked()))
                .containsExactly("e-쎄이프 정기예금");
        assertThat(rateNames(result.rateRanked()))
                .containsExactly("e-쎄이프 정기예금");

        ProductMatchDto bankProduct = result.bankRanked().get(0);
        assertThat(bankProduct.depositScore()).isCloseTo(75.0, offset(0.0001));
        assertThat(bankProduct.totalScore()).isCloseTo(85.0, offset(0.0001));
    }

    @Test
    void 군복무_신분을_선택하면_키워드가_일치하는_상품의_신분점수가_상승한다() {
        ProductSearchResultDto result = searchService.search(createRequest(
                50,
                List.of(new OptionRequestDto(CategoryIdEnum.IDENTITY.getId(), 21L))
        ));

        assertThat(matchNames(result.bankRanked()))
                .containsExactly("청년우대적금", "e-쎄이프 정기예금");

        ProductMatchDto militaryProduct = result.bankRanked().get(0);
        ProductMatchDto generalProduct = result.bankRanked().get(1);
        assertThat(militaryProduct.identityScore()).isCloseTo(25.0, offset(0.0001));
        assertThat(militaryProduct.totalScore()).isCloseTo(100.0, offset(0.0001));
        assertThat(generalProduct.identityScore()).isCloseTo(10.0, offset(0.0001));
    }

    @Test
    void 거주지역을_선택하면_상품옵션_키워드의_지역과_비교한다() {
        ProductSearchResultDto result = searchService.search(createRequest(
                50,
                List.of(new OptionRequestDto(CategoryIdEnum.REGION.getId(), 1L))
        ));

        assertThat(matchNames(result.bankRanked()))
                .containsExactly("e-쎄이프 정기예금");
        assertThat(matchNames(result.bankRanked()))
                .doesNotContain("청년우대적금");
    }

    private SearchRequestDto createRequest(long monthlySavingsGoal, List<OptionRequestDto> options) {
        return new SearchRequestDto(
                options,
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(27),
                        null,
                        null,
                        null,
                        12,
                        null,
                        true,
                        null,
                        monthlySavingsGoal,
                        null,
                        List.of()
                )
        );
    }

    private ProductMatchDto findMatch(List<ProductMatchDto> products, String productName) {
        return products.stream()
                .filter(product -> product.productName().equals(productName))
                .findFirst()
                .orElseThrow();
    }

    private List<String> matchNames(List<ProductMatchDto> products) {
        return products.stream()
                .map(ProductMatchDto::productName)
                .toList();
    }

    private List<String> rateNames(List<ProductRateDto> products) {
        return products.stream()
                .map(ProductRateDto::productName)
                .toList();
    }
}
