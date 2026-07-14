package apptive.fin.user;

import apptive.fin.global.error.BusinessException;
import apptive.fin.user.dto.UserResponseDto;
import apptive.fin.user.dto.UserUpdateRequestDto;
import apptive.fin.user.entity.User;
import apptive.fin.user.service.UserService;
import apptive.fin.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = Mockito.mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("test@email.com");
        when(user.getName()).thenReturn("testUser");
        when(user.getUserRole()).thenReturn(UserRole.BEFORE_AGREED);
    }

    @Test
    void 내정보조회_성공() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponseDto response = userService.getMyInfo(1L);

        assertEquals(1L, response.id());
        assertEquals("test@email.com", response.email());
        assertEquals("testUser", response.name());
    }

    @Test
    void 내정보조회_실패_USER_NOT_FOUND() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            userService.getMyInfo(1L);
        });
    }

    @Test
    void 유저정보수정_성공() {

        UserUpdateRequestDto request =
                new UserUpdateRequestDto("new@email.com", "newName");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail("new@email.com"))
                .thenReturn(false);

        userService.updateUser(1L, request);

        verify(user).updateEmail("new@email.com");
        verify(user).updateName("newName");
    }

    @Test
    void 이메일중복_예외() {

        UserUpdateRequestDto request =
                new UserUpdateRequestDto("duplicate@email.com", null);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmail("duplicate@email.com"))
                .thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            userService.updateUser(1L, request);
        });
    }

    @Test
    void 유저권한_기본값_BEFORE_AGREED() {
        User newUser = User.builder()
                .name("newUser")
                .email("new@email.com")
                .provider("kakao")
                .providerId("12345")
                .build();

        assertEquals(UserRole.BEFORE_AGREED, newUser.getUserRole());
    }

    @Test
    void 유저권한_업데이트_RECOMMENDATION() {
        User newUser = User.builder()
                .name("newUser")
                .email("new@email.com")
                .provider("kakao")
                .providerId("12345")
                .build();

        newUser.updateUserRole(UserRole.RECOMMENDATION);

        assertEquals(UserRole.RECOMMENDATION, newUser.getUserRole());
    }

    @Test
    void 유저권한_업데이트_ADMIN() {
        User newUser = User.builder()
                .name("newUser")
                .email("new@email.com")
                .provider("kakao")
                .providerId("12345")
                .build();

        newUser.updateUserRole(UserRole.ADMIN);

        assertEquals(UserRole.ADMIN, newUser.getUserRole());
    }

    @Test
    void 내정보조회_권한포함() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponseDto response = userService.getMyInfo(1L);

        assertEquals("BEFORE_AGREED", response.userRole());
    }
}