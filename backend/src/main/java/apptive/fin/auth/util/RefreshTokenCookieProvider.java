package apptive.fin.auth.util;

import apptive.fin.global.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieProvider {

    private final JwtUtil jwtUtil;
    private final AppProperties appProperties;

    // 리프레시 토큰용 쿠키 생성 로직 캡슐화
    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(appProperties.cookie().secure())
                .path("/")
                .maxAge(jwtUtil.getRefreshExpiration())
                .sameSite(appProperties.cookie().sameSite())
                .build();
    }

    // 로그아웃 시 쿠키 삭제용 (maxAge=0)
    public ResponseCookie createLogoutCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(appProperties.cookie().secure())
                .path("/")
                .maxAge(0)
                .sameSite(appProperties.cookie().sameSite())
                .build();
    }
}
