package apptive.fin.search;

import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductRateDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.Product;
import apptive.fin.search.repository.ProductRepository;
import apptive.fin.search.service.RateCalculatorService;
import apptive.fin.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

// Y3-2 정부 상품별 수익률 계산 명세 문서의 기대 수익률과 시드 데이터(src/main/resources/seed) 기반
// 실제 계산값을 대조하는 골든 테스트.
// 시드가 pg_dump INSERT 덤프이며 명세 문서와 별도로 관리되므로, 시드 값이 바뀌어도 명세와
// 어긋나면 이 테스트가 실패해서 드러나야 한다(기대값을 시드에 맞춰 조용히 바꾸지 않는다).
@Sql(
        scripts = {
                "/seed/01-providers.sql",
                "/seed/02-products.sql",
                "/seed/03-product-properties.sql",
                "/seed/04-product-keywords.sql",
                "/seed/05-product-required-keywords.sql",
                "/seed/06-product-preferential-rates.sql",
                "/sql/reset-search-path.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup-product-fixtures.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class GovernmentSeedYieldIntegrationTest extends IntegrationTestSupport {

    private static final List<String> ALL_POLICY_CODES = List.of(
            "POLICY001", "POLICY002", "POLICY003", "POLICY004", "POLICY005", "POLICY006", "POLICY007", "POLICY008",
            "POLICY009", "POLICY010", "POLICY011", "POLICY012", "POLICY013", "POLICY014", "POLICY015", "POLICY016"
    );

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RateCalculatorService rateCalculatorService;

    static Stream<GovYieldCase> 정부상품_수익률_명세() {
        return Stream.of(
                new GovYieldCase("POLICY001", "청년내일저축계좌 (정액 30만/3년)", 100_000L, 100.0),
                new GovYieldCase("POLICY002", "청년미래적금 (정률, 일반6%/우대12% 중 최고 property, 3년)", 500_000L, 4.0),
                new GovYieldCase("POLICY003", "부산 기쁨두배통장 (정률 1.0, 2년/3년 중 최고=2년)", 100_000L, 50.0),
                // 명세(Y3-2 계산 명세 4번)는 현금 매칭 기준 50.0%와 지역화폐 100만원 포함 70.8% 두 산식을 병기.
                // 시드(gov_matching_ratio=1.4167)는 지역화폐 포함 배수를 채택했으므로 그 기준(≈70.83%)으로 검증한다.
                new GovYieldCase("POLICY004", "경기 청년노동자통장 (정률 1.4167, 2년, 지역화폐 포함)", 100_000L, 70.83),
                new GovYieldCase("POLICY005", "인천 드림For청년통장 (정률 1.0, 3년)", 150_000L, 33.33),
                new GovYieldCase("POLICY006", "인천 행복씨앗통장 (정액 15만/3년)", 150_000L, 33.33),
                new GovYieldCase("POLICY007", "강원 중증장애인 자산형성지원 (정액 15만/3년)", 150_000L, 33.33),
                new GovYieldCase("POLICY008", "광주 청년13(일+삶)통장 (정률 1.0, 10개월)", 100_000L, 120.0),
                new GovYieldCase("POLICY009", "전남 희망디딤돌통장 (정률 1.0, 3년)", 100_000L, 33.33),
                new GovYieldCase("POLICY010", "경남 모다드림청년통장 (정률 1.0, 2년)", 200_000L, 50.0),
                new GovYieldCase("POLICY011", "강원 디딤돌2배적금 (정률 1.0, 3년)", 100_000L, 33.33),
                new GovYieldCase("POLICY012", "전북청년 함께 두배적금 (정률 1.0, 2년)", 100_000L, 50.0),
                new GovYieldCase("POLICY013", "대구 청년희망적금 (정률 1.0, 1년)", 100_000L, 100.0),
                new GovYieldCase("POLICY014", "세종 청년미래적금(청년희망적금) (정률 1.0, 3년)", 150_000L, 33.33),
                new GovYieldCase("POLICY015", "경북 사랑채움사업 (정률 1.0, 2년)", 200_000L, 50.0),
                new GovYieldCase("POLICY016", "함안정착 청년통장 (정률 1.5, 3년)", 200_000L, 50.0)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("정부상품_수익률_명세")
    void 시드데이터로_계산한_정부기여금_환산수익률이_명세문서의_기대값과_일치한다(GovYieldCase testCase) {
        Product product = productRepository.findByProductCodeWithProperties(testCase.code())
                .orElseThrow(() -> new AssertionError(testCase.code() + " 상품이 시드 데이터에 없습니다."));

        SearchRequestDto request = createRequest(testCase.monthlySavingsGoal());
        ProductRateDto result = rateCalculatorService.calculate(product, request, emptyKeywords());

        assertThat(result.rateComparable())
                .as("%s(%s) rateComparable", testCase.code(), testCase.description())
                .isTrue();
        assertThat(result.achievableRate())
                .as("%s(%s) achievableRate", testCase.code(), testCase.description())
                .isCloseTo(testCase.expectedAnnualYieldPercent(), offset(0.05));
    }

    @Test
    void 그룹1_정부상품_16개는_모두_기여금_데이터가_존재하고_수익률_비교가_가능하다() {
        // 기여금 데이터(gov_contribution_type)가 누락되면 계산은 null을 반환하고 rateComparable=false가 되어
        // 탭 B(수익률 비교) 목록에서 조용히 탈락한다. 시드 정합성 회귀를 막기 위한 무결성 체크.
        SearchRequestDto request = createRequest(300_000L);

        for (String code : ALL_POLICY_CODES) {
            Product product = productRepository.findByProductCodeWithProperties(code)
                    .orElseThrow(() -> new AssertionError(code + " 상품이 시드 데이터에 없습니다."));

            boolean hasGovContribution = product.getProperties().stream()
                    .anyMatch(property -> property.getGovContributionType() != null
                            && property.getGovContributionType() != ContributionType.NONE);
            assertThat(hasGovContribution)
                    .as("%s 상품에 기여금 매칭 데이터(gov_contribution_type != NONE)가 존재해야 한다", code)
                    .isTrue();

            ProductRateDto result = rateCalculatorService.calculate(product, request, emptyKeywords());
            assertThat(result.rateComparable())
                    .as("%s 상품의 calculate() 결과가 rateComparable=true여야 한다", code)
                    .isTrue();
        }
    }

    private SearchRequestDto createRequest(Long monthlySavingsGoal) {
        return new SearchRequestDto(
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
                        monthlySavingsGoal,
                        null,
                        List.of()
                )
        );
    }

    private ResolvedKeywords emptyKeywords() {
        return new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of());
    }

    private record GovYieldCase(String code, String description, long monthlySavingsGoal, double expectedAnnualYieldPercent) {
        @Override
        public String toString() {
            return code + " " + description;
        }
    }
}
