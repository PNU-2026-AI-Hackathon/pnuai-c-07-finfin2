package apptive.fin.myfin;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.myfin.dto.MyfinResponseDto;
import apptive.fin.myfin.entity.MyFin;
import apptive.fin.myfin.repository.MyFinRepository;
import apptive.fin.myfin.service.MyFinService;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ProductSearchResultDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.enums.CategoryIdEnum;
import apptive.fin.search.repository.ProductPropertyRepository;
import apptive.fin.search.service.SearchService;
import apptive.fin.support.IntegrationTestSupport;
import apptive.fin.user.UserRole;
import apptive.fin.user.entity.User;
import apptive.fin.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * #33 회귀 테스트: 추천 목록(SearchService)과 찜 목록(MyFinService)이 같은 프로필/옵션 조건에서
 * 같은 상품에 대해 같은 적합도를 반환하는지 검증한다.
 *
 * 원인이었던 은행 #최고이율 상위 30% 임계금리가 "찜한 상품만"이 아니라 SearchService와 동일하게
 * "가입 가능한 전체 은행상품" 결과셋 기준으로 계산되는지가 핵심이라, 컷을 넘는 상품과 못 넘는 상품을
 * 둘 다 검증한다.
 */
@Sql(
        scripts = "/sql/search-products.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
        scripts = "/sql/cleanup-product-fixtures.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
)
class MyFinFitScoreParityIntegrationTest extends IntegrationTestSupport {

    // data.sql의 category_option 삽입 순서로 결정되는 옵션 id (SearchServiceIntegrationTest와 동일)
    private static final Long BUSAN_REGION_OPTION_ID = 2L;          // REGION_BUSAN
    private static final Long AROUND_1_YEAR_PERIOD_OPTION_ID = 24L; // TERM_AROUND_1_YEAR
    private static final Long MAX_INTEREST_BENEFIT_OPTION_ID = 25L; // BENEFIT_MAX_INTEREST
    private static final Long FIRST_TRANSACTION_OPTION_ID = 31L;    // BANK_FIRST_TRANSACTION

    @Autowired
    private SearchService searchService;
    @Autowired
    private MyFinService myFinService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MyFinRepository myFinRepository;
    @Autowired
    private ProductPropertyRepository productPropertyRepository;

    @Test
    void 찜한_상품의_적합도는_추천목록의_적합도와_같다() {
        SearchRequestDto request = createRequest(List.of(
                new OptionRequestDto(CategoryIdEnum.REGION.getId(), BUSAN_REGION_OPTION_ID),
                new OptionRequestDto(CategoryIdEnum.BENEFIT.getId(), MAX_INTEREST_BENEFIT_OPTION_ID)
        ));

        Long userId = createUser();
        AuthUserDetails userDetails = new AuthUserDetails(userId, UserRole.RECOMMENDATION);

        ProductSearchResultDto searchResult = searchService.search(request, userDetails);
        ProductMatchDto qualifiedCard = findMatch(searchResult.bankRanked(), "청년우대적금"); // 컷(4.5) 충족
        ProductMatchDto belowCutCard = findMatch(searchResult.bankRanked(), "e-쎄이프 정기예금"); // 컷 미달
        assertThat(qualifiedCard.benefitScore()).isPositive();
        assertThat(belowCutCard.benefitScore()).isZero();

        favorite(userId, qualifiedCard.productPropertyId());
        favorite(userId, belowCutCard.productPropertyId());

        MyfinResponseDto.List_ favorites = myFinService.getFavorites(userId, request, userDetails);

        MyfinResponseDto.Item qualifiedItem = findItem(favorites, qualifiedCard.productPropertyId());
        MyfinResponseDto.Item belowCutItem = findItem(favorites, belowCutCard.productPropertyId());

        // 핵심 검증: 추천 목록과 찜 목록의 적합도가 (int 변환까지) 완전히 일치해야 한다.
        assertThat(qualifiedItem.fitScore()).isEqualTo((int) qualifiedCard.totalScore());
        assertThat(belowCutItem.fitScore()).isEqualTo((int) belowCutCard.totalScore());
        assertThat(qualifiedItem.fitScore()).isNotEqualTo(belowCutItem.fitScore());
    }

    private void favorite(Long userId, Long productPropertyId) {
        User user = userRepository.getReferenceById(userId);
        ProductProperty property = productPropertyRepository.findByIdAndIsJoinableTrue(productPropertyId)
                .orElseThrow();
        myFinRepository.save(MyFin.builder().user(user).productProperty(property).build());
    }

    private Long createUser() {
        User user = userRepository.save(User.builder()
                .name("fitscore-tester")
                .email("fitscore-tester@example.com")
                .provider("google")
                .providerId("pid-" + System.nanoTime())
                .userRole(UserRole.RECOMMENDATION)
                .build());
        return user.getId();
    }

    private SearchRequestDto createRequest(List<OptionRequestDto> options) {
        return new SearchRequestDto(
                requiredStep1Options(options),
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(27),
                        30_000_000L,
                        3,
                        100,
                        12,
                        null,
                        true,
                        null,
                        50L,
                        List.of(),
                        List.of(),
                        List.of()
                )
        );
    }

    private List<OptionRequestDto> requiredStep1Options(List<OptionRequestDto> options) {
        List<OptionRequestDto> completed = new ArrayList<>(options);
        if (completed.stream().noneMatch(option -> CategoryIdEnum.PERIOD.getId().equals(option.categoryId()))) {
            completed.add(new OptionRequestDto(CategoryIdEnum.PERIOD.getId(), AROUND_1_YEAR_PERIOD_OPTION_ID));
        }
        if (completed.stream().noneMatch(option -> CategoryIdEnum.BANK_COND.getId().equals(option.categoryId()))) {
            completed.add(new OptionRequestDto(CategoryIdEnum.BANK_COND.getId(), FIRST_TRANSACTION_OPTION_ID));
        }
        return List.copyOf(completed);
    }

    private ProductMatchDto findMatch(List<ProductMatchDto> products, String productName) {
        return products.stream()
                .filter(product -> product.productName().equals(productName))
                .findFirst()
                .orElseThrow();
    }

    private MyfinResponseDto.Item findItem(MyfinResponseDto.List_ favorites, Long productPropertyId) {
        return favorites.items().stream()
                .filter(item -> item.productPropertyId().equals(productPropertyId))
                .findFirst()
                .orElseThrow();
    }
}
