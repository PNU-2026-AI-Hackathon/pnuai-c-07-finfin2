package apptive.fin.auth;

import apptive.fin.auth.entity.RefreshToken;
import apptive.fin.user.entity.User;
import apptive.fin.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    @Test
    void 새로_생성한_리프레시_토큰은_기본적으로_활성_상태다() {
        RefreshToken refreshToken = createRefreshToken(LocalDateTime.now().plusMinutes(5));

        assertThat(refreshToken.isActive()).isTrue();
    }

    @Test
    void 활성_상태이면서_만료되지_않은_토큰은_유효하다() {
        RefreshToken refreshToken = createRefreshToken(LocalDateTime.now().plusMinutes(5));

        assertThat(refreshToken.checkValidity()).isTrue();
    }

    @Test
    void 만료된_토큰은_유효하지_않다() {
        RefreshToken refreshToken = createRefreshToken(LocalDateTime.now().minusMinutes(1));

        assertThat(refreshToken.checkValidity()).isFalse();
    }

    @Test
    void 비활성_상태인_토큰은_유효하지_않다() {
        RefreshToken refreshToken = createRefreshToken(LocalDateTime.now().plusMinutes(5));
        ReflectionTestUtils.setField(refreshToken, "isActive", false);

        assertThat(refreshToken.checkValidity()).isFalse();
    }

    private RefreshToken createRefreshToken(LocalDateTime expiresAt) {
        return RefreshToken.builder()
                .tokenHash("hashed-token")
                .expiresAt(expiresAt)
                .user(User.builder()
                        .name("tester")
                        .email("tester@example.com")
                        .provider("google")
                        .providerId("provider-id")
                        .userRole(UserRole.BASIC_ACCESS)
                        .build())
                .build();
    }
}
