package apptive.fin.auth;

import apptive.fin.auth.dto.LoginResponseDto;
import apptive.fin.auth.entity.RefreshToken;
import apptive.fin.auth.repository.RefreshTokenRepository;
import apptive.fin.auth.service.AuthService;
import apptive.fin.global.error.BusinessException;
import apptive.fin.auth.util.JwtUtil;
import apptive.fin.term.service.TermService;
import apptive.fin.user.entity.User;
import apptive.fin.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TermService termService;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    @Test
    void 리프레시_토큰을_발급하면_해시를_저장하고_raw_토큰을_반환한다() {
        User user = createUser(1L, UserRole.BASIC_ACCESS);
        byte[] rawRefreshToken = new byte[]{1, 2, 3, 4};
        LocalDateTime beforeCall = LocalDateTime.now();

        when(jwtUtil.generateRefreshToken()).thenReturn(rawRefreshToken);
        when(jwtUtil.hashToken(rawRefreshToken)).thenReturn("hashed-token");
        when(jwtUtil.getRefreshExpiration()).thenReturn(120);

        String issuedToken = authService.getRefreshToken(user);

        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
        RefreshToken savedToken = refreshTokenCaptor.getValue();

        assertThat(issuedToken).isEqualTo(Base64.getEncoder().encodeToString(rawRefreshToken));
        assertThat(savedToken.getTokenHash()).isEqualTo("hashed-token");
        assertThat(savedToken.getUser()).isSameAs(user);
        assertThat(savedToken.isActive()).isTrue();
        assertThat(savedToken.getExpiresAt())
                .isBetween(beforeCall.plusSeconds(120), LocalDateTime.now().plusSeconds(120));
    }

    @Test
    void 리프레시_요청이_성공하면_이전_토큰을_비활성화하고_새_토큰을_재발급한다() {
        byte[] oldRawToken = new byte[]{9, 8, 7, 6};
        byte[] newRawToken = new byte[]{1, 2, 3, 4};
        String encodedOldToken = Base64.getEncoder().encodeToString(oldRawToken);
        User user = createUser(1L, UserRole.ADMIN);
        RefreshToken storedToken = RefreshToken.builder()
                .tokenHash("old-hash")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .build();

        when(jwtUtil.hashToken(oldRawToken)).thenReturn("old-hash");
        when(refreshTokenRepository.findByTokenHash("old-hash")).thenReturn(Optional.of(storedToken));
        when(refreshTokenRepository.deactivateIfActive("old-hash")).thenReturn(1);
        when(jwtUtil.generateAccessToken("1", UserRole.ADMIN, true)).thenReturn("new-access-token");
        when(jwtUtil.generateRefreshToken()).thenReturn(newRawToken);
        when(jwtUtil.hashToken(newRawToken)).thenReturn("new-hash");
        when(jwtUtil.getRefreshExpiration()).thenReturn(300);
        when(termService.didUserAgreeAllRequiredTerms(1L)).thenReturn(true);

        LoginResponseDto response = authService.refresh(encodedOldToken);

        verify(refreshTokenRepository).deactivateIfActive("old-hash");
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo(Base64.getEncoder().encodeToString(newRawToken));
        assertThat(refreshTokenCaptor.getValue().getTokenHash()).isEqualTo("new-hash");
    }

    @Test
    void 저장된_리프레시_토큰이_없으면_재발급은_인증_실패다() {
        byte[] rawToken = new byte[]{9, 8, 7, 6};
        String encodedToken = Base64.getEncoder().encodeToString(rawToken);

        when(jwtUtil.hashToken(rawToken)).thenReturn("missing-hash");
        when(refreshTokenRepository.findByTokenHash("missing-hash")).thenReturn(Optional.empty());

        assertUnauthorized(() -> authService.refresh(encodedToken));
    }

    @Test
    void 저장된_리프레시_토큰이_비활성_상태면_재발급은_인증_실패다() {
        byte[] rawToken = new byte[]{9, 8, 7, 6};
        String encodedToken = Base64.getEncoder().encodeToString(rawToken);
        RefreshToken inactiveToken = RefreshToken.builder()
                .tokenHash("inactive-hash")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .user(createUser(1L, UserRole.BASIC_ACCESS))
                .build();
        ReflectionTestUtils.setField(inactiveToken, "isActive", false);

        when(jwtUtil.hashToken(rawToken)).thenReturn("inactive-hash");
        when(refreshTokenRepository.findByTokenHash("inactive-hash")).thenReturn(Optional.of(inactiveToken));

        assertUnauthorized(() -> authService.refresh(encodedToken));
    }

    @Test
    void 저장된_리프레시_토큰이_만료됐으면_재발급은_인증_실패다() {
        byte[] rawToken = new byte[]{9, 8, 7, 6};
        String encodedToken = Base64.getEncoder().encodeToString(rawToken);
        RefreshToken expiredToken = RefreshToken.builder()
                .tokenHash("expired-hash")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .user(createUser(1L, UserRole.BASIC_ACCESS))
                .build();

        when(jwtUtil.hashToken(rawToken)).thenReturn("expired-hash");
        when(refreshTokenRepository.findByTokenHash("expired-hash")).thenReturn(Optional.of(expiredToken));

        assertUnauthorized(() -> authService.refresh(encodedToken));
    }

    @Test
    void base64가_아닌_리프레시_토큰으로_재발급하면_인증_실패다() {
        assertUnauthorized(() -> authService.refresh("not-base64%%%"));
    }

    @Test
    void 로그아웃하면_리프레시_토큰_해시로_저장소에서_삭제한다() {
        byte[] rawToken = new byte[]{4, 3, 2, 1};
        String encodedToken = Base64.getEncoder().encodeToString(rawToken);

        when(jwtUtil.hashToken(rawToken)).thenReturn("hashed-token");

        authService.logout(encodedToken);

        verify(refreshTokenRepository).deleteByTokenHash("hashed-token");
    }

    @Test
    void base64가_아닌_토큰으로_로그아웃하면_인증_실패다() {
        assertUnauthorized(() -> authService.logout("not-base64%%%"));
    }

    @Test
    void 리프레시_토큰을_연속으로_발급하면_서로_다른_raw_토큰이_반환된다() {
        User user = createUser(1L, UserRole.BASIC_ACCESS);
        byte[] firstRawToken = new byte[]{1, 2, 3, 4};
        byte[] secondRawToken = new byte[]{4, 3, 2, 1};

        when(jwtUtil.generateRefreshToken()).thenReturn(firstRawToken, secondRawToken);
        when(jwtUtil.hashToken(firstRawToken)).thenReturn("first-hash");
        when(jwtUtil.hashToken(secondRawToken)).thenReturn("second-hash");
        when(jwtUtil.getRefreshExpiration()).thenReturn(120);

        String firstIssuedToken = authService.getRefreshToken(user);
        String secondIssuedToken = authService.getRefreshToken(user);

        verify(refreshTokenRepository, times(2)).save(refreshTokenCaptor.capture());

        assertThat(firstIssuedToken).isNotEqualTo(secondIssuedToken);
        assertThat(refreshTokenCaptor.getAllValues())
                .extracting(RefreshToken::getTokenHash)
                .containsExactly("first-hash", "second-hash");
    }

    private void assertUnauthorized(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(BusinessException.class)
                .satisfies(exception ->
                        assertThat(((BusinessException) exception).getErrorCode()).isEqualTo(AuthErrorCode.UNAUTHORIZED)
                );
    }

    private User createUser(Long id, UserRole role) {
        User user = User.builder()
                .name("tester")
                .email("tester@example.com")
                .provider("google")
                .providerId("provider-id")
                .userRole(role)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
