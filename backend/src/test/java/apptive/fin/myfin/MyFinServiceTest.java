package apptive.fin.myfin;

import apptive.fin.global.error.BusinessException;
import apptive.fin.myfin.dto.MyfinResponseDto;
import apptive.fin.myfin.entity.MyFin;
import apptive.fin.myfin.repository.MyFinRepository;
import apptive.fin.myfin.service.MyFinService;
import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductPreferentialRate;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.entity.ProductSource;
import apptive.fin.provider.entity.Provider;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductApplyStatus;
import apptive.fin.search.enums.ProductType;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ProductMatchDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.repository.ProductPropertyRepository;
import apptive.fin.search.service.EligibilityFilterService;
import apptive.fin.search.service.MatchScoreService;
import apptive.fin.search.service.RateCalculatorService;
import apptive.fin.search.service.ResolveKeywordService;
import apptive.fin.search.service.SearchRequestPolicy;
import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.user.entity.User;
import apptive.fin.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MyFinServiceTest {

    @Mock
    private MyFinRepository myFinRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductPropertyRepository productPropertyRepository;
    @Mock
    private MatchScoreService matchScoreService;
    @Mock
    private RateCalculatorService rateCalculatorService;
    @Mock
    private ResolveKeywordService resolveKeywordService;
    @Mock
    private EligibilityFilterService eligibilityFilterService;
    @Mock
    private SearchRequestPolicy searchRequestPolicy;

    @InjectMocks
    private MyFinService myFinService;

    private User user;
    private ProductProperty productProperty;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        productProperty = mock(ProductProperty.class);
        when(productProperty.getId()).thenReturn(100L);
    }

    @Test
    void 찜추가_성공() {
        when(myFinRepository.countByUserId(1L)).thenReturn(0);
        when(myFinRepository.existsByUserIdAndProductPropertyId(1L, 100L)).thenReturn(false);
        when(productPropertyRepository.findByIdAndIsJoinableTrue(100L)).thenReturn(Optional.of(productProperty));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> myFinService.addFavorite(1L, 100L));
        verify(myFinRepository).save(any(MyFin.class));
    }

    @Test
    void 찜추가_실패_한도초과() {
        when(myFinRepository.countByUserId(1L)).thenReturn(20);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> myFinService.addFavorite(1L, 100L));

        assertEquals(MyFinErrorCode.FAVORITE_LIMIT_EXCEEDED, ex.getErrorCode());
    }

    @Test
    void 찜추가_실패_중복() {
        when(myFinRepository.countByUserId(1L)).thenReturn(5);
        when(myFinRepository.existsByUserIdAndProductPropertyId(1L, 100L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> myFinService.addFavorite(1L, 100L));

        assertEquals(MyFinErrorCode.FAVORITE_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void 비활성_상품속성은_새로_찜할_수_없다() {
        when(myFinRepository.countByUserId(1L)).thenReturn(0);
        when(myFinRepository.existsByUserIdAndProductPropertyId(1L, 100L)).thenReturn(false);
        when(productPropertyRepository.findByIdAndIsJoinableTrue(100L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> myFinService.addFavorite(1L, 100L));

        assertEquals(MyFinErrorCode.PRODUCT_NOT_FOUND, ex.getErrorCode());
        verify(myFinRepository, never()).save(any(MyFin.class));
    }

    @Test
    void 찜삭제_성공() {
        when(myFinRepository.existsByUserIdAndProductPropertyId(1L, 100L)).thenReturn(true);

        assertDoesNotThrow(() -> myFinService.removeFavorite(1L, 100L));
        verify(myFinRepository).deleteByUserIdAndProductPropertyId(1L, 100L);
    }

    @Test
    void 찜삭제_실패_존재하지않음() {
        when(myFinRepository.existsByUserIdAndProductPropertyId(1L, 100L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> myFinService.removeFavorite(1L, 100L));

        assertEquals(MyFinErrorCode.FAVORITE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 찜여부확인() {
        when(myFinRepository.existsByUserIdAndProductPropertyId(1L, 100L)).thenReturn(true);
        assertTrue(myFinService.isFavorite(1L, 100L));

        when(myFinRepository.existsByUserIdAndProductPropertyId(1L, 200L)).thenReturn(false);
        assertFalse(myFinService.isFavorite(1L, 200L));
    }

    @Test
    void 찜목록조회_빈목록() {
        when(myFinRepository.findAllByUserIdWithDetails(1L)).thenReturn(List.of());

        MyfinResponseDto.List_ result = myFinService.getFavorites(1L);

        assertTrue(result.items().isEmpty());
        assertFalse(result.showComparisonNotice());
    }

    @Test
    void 찜개수조회() {
        when(myFinRepository.countByUserId(1L)).thenReturn(5);
        assertEquals(5, myFinService.getFavoriteCount(1L));
    }

    @Test
    void 찜목록은_productProperty에서_해석한_신청URL을_반환한다() {
        MyFin favorite = mock(MyFin.class);
        Product product = mock(Product.class);
        ProductSource source = mock(ProductSource.class);

        when(myFinRepository.findAllByUserIdWithDetails(1L)).thenReturn(List.of(favorite));
        when(favorite.getId()).thenReturn(10L);
        when(favorite.getProductProperty()).thenReturn(productProperty);
        when(productProperty.getProduct()).thenReturn(product);
        when(productProperty.keywordCodes()).thenReturn(Set.of());
        when(productProperty.getExcludeFromRateComparison()).thenReturn(false);
        when(productProperty.isJoinable()).thenReturn(true);
        when(productProperty.resolvedApplyUrl()).thenReturn("https://product.example/apply");
        when(product.getSource()).thenReturn(source);
        when(source.getCode()).thenReturn("ONTONG");
        when(product.getProductCode()).thenReturn("POLICY001");
        when(product.getProductName()).thenReturn("청년정책상품");

        MyfinResponseDto.List_ result = myFinService.getFavorites(1L);

        assertEquals("https://product.example/apply", result.items().getFirst().applyUrl());
        verify(productProperty).resolvedApplyUrl();
    }

    @ParameterizedTest
    @ValueSource(strings = {"FSS", "ONTONG"})
    void 기존_비활성_상품_찜은_소스와_무관하게_종료상태로_남기고_신청URL을_숨긴다(String sourceCode) {
        MyFin favorite = mock(MyFin.class);
        Product product = mock(Product.class);
        ProductSource source = mock(ProductSource.class);
        Provider provider = mock(Provider.class);

        when(myFinRepository.findAllByUserIdWithDetails(1L)).thenReturn(List.of(favorite));
        when(favorite.getId()).thenReturn(10L);
        when(favorite.getProductProperty()).thenReturn(productProperty);
        when(productProperty.getProduct()).thenReturn(product);
        when(productProperty.getProvider()).thenReturn(provider);
        when(productProperty.keywordCodes()).thenReturn(Set.of());
        when(productProperty.getExcludeFromRateComparison()).thenReturn(false);
        when(productProperty.isJoinable()).thenReturn(false);
        when(productProperty.resolvedApplyUrl()).thenReturn("https://bank.example/apply");
        when(provider.getName()).thenReturn("국민은행");
        when(product.getSource()).thenReturn(source);
        when(source.getCode()).thenReturn(sourceCode);
        when(product.getProductCode()).thenReturn("CLOSED_BANK");
        when(product.getProductName()).thenReturn("판매종료 적금");

        MyfinResponseDto.Item item = myFinService.getFavorites(1L).items().getFirst();

        assertEquals(ProductApplyStatus.RECRUIT_CLOSED, item.applyStatus());
        assertNull(item.applyUrl());
    }

    @Test
    void 프로필_없는_찜목록은_실제_금리계산기를_사용해도_지표가_null이다() {
        MyFinService service = new MyFinService(
                myFinRepository,
                userRepository,
                productPropertyRepository,
                matchScoreService,
                new RateCalculatorService(),
                resolveKeywordService,
                eligibilityFilterService,
                searchRequestPolicy
        );
        MyFin favorite = mock(MyFin.class);
        Product product = mock(Product.class);
        ProductSource source = mock(ProductSource.class);

        when(myFinRepository.findAllByUserIdWithDetails(1L)).thenReturn(List.of(favorite));
        when(favorite.getId()).thenReturn(10L);
        when(favorite.getProductProperty()).thenReturn(productProperty);
        when(productProperty.getProduct()).thenReturn(product);
        when(productProperty.keywordCodes()).thenReturn(Set.of());
        when(productProperty.getExcludeFromRateComparison()).thenReturn(false);
        when(productProperty.isJoinable()).thenReturn(true);
        when(product.getSource()).thenReturn(source);
        when(source.getCode()).thenReturn("ONTONG");
        when(product.getProductCode()).thenReturn("POLICY001");
        when(product.getProductName()).thenReturn("청년정책상품");

        MyfinResponseDto.List_ result = service.getFavorites(1L);

        assertNull(result.items().getFirst().fitScore());
        assertNull(result.items().getFirst().metrics());
    }

    @Test
    void 찜목록의_적합도와_달성금리는_같은_거래이력_게이트를_따른다() {
        MyFinService service = new MyFinService(
                myFinRepository,
                userRepository,
                productPropertyRepository,
                new MatchScoreService(),
                new RateCalculatorService(),
                resolveKeywordService,
                eligibilityFilterService,
                searchRequestPolicy
        );
        MyFin favorite = mock(MyFin.class);
        Product product = bankProductWithFirstTransactionRate();
        ProductProperty property = product.getProperties().getFirst();
        AuthUserDetails userDetails = mock(AuthUserDetails.class);

        when(myFinRepository.findAllByUserIdWithDetails(1L)).thenReturn(List.of(favorite));
        when(favorite.getId()).thenReturn(10L);
        when(favorite.getProductProperty()).thenReturn(property);
        // 최고이율 상위 30% 임계값 계산용 모집단(전체 가입가능 은행상품)은 이 테스트의 관심사가 아니므로 빈 결과로 스텁
        when(eligibilityFilterService.filterEligibleOptions(any(), any())).thenReturn(List.of());

        SearchRequestDto complete = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, null,
                        List.of("KB"), List.of(), List.of()
                )
        );
        SearchRequestDto incomplete = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, null,
                        List.of("KB"), null, List.of()
                )
        );
        when(resolveKeywordService.resolveKeywords(List.of()))
                .thenReturn(ResolvedKeywords.emptyKeywords());
        // tabBEnabled: complete 요청은 true, incomplete 요청은 false
        when(searchRequestPolicy.canUsePersonalization(eq(complete), any(), eq(userDetails))).thenReturn(true);
        when(searchRequestPolicy.canUsePersonalization(eq(incomplete), any(), eq(userDetails))).thenReturn(false);

        MyfinResponseDto.Item completeItem = service.getFavorites(1L, complete, userDetails).items().getFirst();
        MyfinResponseDto.Item incompleteItem = service.getFavorites(1L, incomplete, userDetails).items().getFirst();

        // 검증: fitScore 값 출력 (PR 첨부용)
        System.out.println("=== fitScore 계산 결과 비교 ===");
        System.out.println("tabBEnabled=true  (거래이력 반영)  : fitScore = " + completeItem.fitScore());
        System.out.println("tabBEnabled=false (거래이력 미반영): fitScore = " + incompleteItem.fitScore());
        System.out.println("achievableRate (tabBEnabled=true) : " + completeItem.metrics().achievableRate() + "%");
        System.out.println("achievableRate (tabBEnabled=false): " + incompleteItem.metrics().achievableRate() + "%");

        assertEquals(100, completeItem.fitScore());
        assertTrue(completeItem.fitScore() > incompleteItem.fitScore());
        assertEquals(4.0, completeItem.metrics().achievableRate());
        assertEquals(3.5, incompleteItem.metrics().achievableRate());
        assertFalse(completeItem.keywords().contains(KeywordValueEnum.BANK_ETC.name()));
    }

    @Test
    void Search와_MyFin의_fitScore가_동일한_조건에서_일치한다() {
        // Given: 동일한 MatchScoreService 사용
        MatchScoreService sharedMatchScoreService = new MatchScoreService();

        MyFinService myFinService = new MyFinService(
                myFinRepository,
                userRepository,
                productPropertyRepository,
                sharedMatchScoreService,
                new RateCalculatorService(),
                resolveKeywordService,
                eligibilityFilterService,
                searchRequestPolicy
        );

        MyFin favorite = mock(MyFin.class);
        Product product = bankProductWithFirstTransactionRate();
        ProductProperty property = product.getProperties().getFirst();
        AuthUserDetails userDetails = mock(AuthUserDetails.class);

        when(myFinRepository.findAllByUserIdWithDetails(1L)).thenReturn(List.of(favorite));
        when(favorite.getId()).thenReturn(10L);
        when(favorite.getProductProperty()).thenReturn(property);
        when(eligibilityFilterService.filterEligibleOptions(any(), any())).thenReturn(List.of());

        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, null,
                        List.of("KB"), List.of(), List.of()
                )
        );
        ResolvedKeywords keywords = ResolvedKeywords.emptyKeywords();
        when(resolveKeywordService.resolveKeywords(List.of())).thenReturn(keywords);
        when(searchRequestPolicy.canUsePersonalization(any(), any(), any())).thenReturn(true);

        // When: Search 방식으로 직접 계산
        boolean searchIncludeTransactionHistory = true; // 은행상품, tabBEnabled=true
        Double searchThreshold = null; // 빈 모집단
        ProductMatchDto searchResult = sharedMatchScoreService.score(
                product, property, request, keywords, searchIncludeTransactionHistory, searchThreshold
        );
        int searchFitScore = (int) searchResult.totalScore();

        // When: MyFin 방식으로 계산
        MyfinResponseDto.Item myFinItem = myFinService.getFavorites(1L, request, userDetails).items().getFirst();
        int myFinFitScore = myFinItem.fitScore();

        // Then: 동일한 값이 나와야 함
        System.out.println("=== Search vs MyFin fitScore 비교 ===");
        System.out.println("Search fitScore: " + searchFitScore);
        System.out.println("MyFin  fitScore: " + myFinFitScore);
        System.out.println("일치 여부: " + (searchFitScore == myFinFitScore ? "✓ 일치" : "✗ 불일치"));

        assertEquals(searchFitScore, myFinFitScore, "Search와 MyFin의 fitScore가 동일해야 합니다");
    }

    private Product bankProductWithFirstTransactionRate() {
        ProductSource source = new ProductSource();
        ReflectionTestUtils.setField(source, "code", "FSS");

        Provider provider = new Provider();
        ReflectionTestUtils.setField(provider, "code", "KB");
        ReflectionTestUtils.setField(provider, "name", "국민은행");

        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "source", source);
        ReflectionTestUtils.setField(product, "type", ProductType.SAVING);
        ReflectionTestUtils.setField(product, "productCode", "FAVORITE_FIRST");
        ReflectionTestUtils.setField(product, "productName", "첫거래 상품");

        ProductProperty property = new ProductProperty();
        ReflectionTestUtils.setField(property, "id", 100L);
        ReflectionTestUtils.setField(property, "product", product);
        ReflectionTestUtils.setField(property, "provider", provider);
        ReflectionTestUtils.setField(property, "baseRate", new BigDecimal("3.50"));
        ReflectionTestUtils.setField(property, "maxRate", new BigDecimal("5.00"));
        ReflectionTestUtils.setField(property, "saveTrm", 12);

        ProductPreferentialRate rate = new ProductPreferentialRate();
        ReflectionTestUtils.setField(rate, "productProperty", property);
        ReflectionTestUtils.setField(rate, "keywordCode", KeywordValueEnum.BANK_FIRST_TRANSACTION);
        ReflectionTestUtils.setField(rate, "rate", new BigDecimal("0.50"));
        ProductPreferentialRate etcRate = new ProductPreferentialRate();
        ReflectionTestUtils.setField(etcRate, "productProperty", property);
        ReflectionTestUtils.setField(etcRate, "keywordCode", KeywordValueEnum.BANK_ETC);
        ReflectionTestUtils.setField(etcRate, "rate", new BigDecimal("1.00"));
        ReflectionTestUtils.setField(
                property,
                "preferentialRates",
                new ArrayList<>(List.of(rate, etcRate))
        );
        ReflectionTestUtils.setField(product, "properties", new ArrayList<>(List.of(property)));
        return product;
    }
}
