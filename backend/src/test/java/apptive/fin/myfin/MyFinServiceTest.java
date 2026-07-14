package apptive.fin.myfin;

import apptive.fin.global.error.BusinessException;
import apptive.fin.myfin.dto.MyfinResponseDto;
import apptive.fin.myfin.entity.MyFin;
import apptive.fin.myfin.repository.MyFinRepository;
import apptive.fin.myfin.service.MyFinService;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.repository.ProductPropertyRepository;
import apptive.fin.search.service.MatchScoreService;
import apptive.fin.search.service.RateCalculatorService;
import apptive.fin.user.entity.User;
import apptive.fin.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

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
        when(productPropertyRepository.findById(100L)).thenReturn(Optional.of(productProperty));
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
}
