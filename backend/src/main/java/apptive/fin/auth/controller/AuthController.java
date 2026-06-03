package apptive.fin.auth.controller;

import apptive.fin.auth.AuthErrorCode;
import apptive.fin.auth.service.AuthService;
import apptive.fin.auth.dto.LoginResponseDto;
import apptive.fin.auth.util.RefreshTokenCookieProvider;
import apptive.fin.global.dto.GenericDataDto;
import apptive.fin.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        checkRefreshToken(refreshToken);
        authService.logout(refreshToken);

        ResponseCookie cookie = refreshTokenCookieProvider.createLogoutCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();

    }

    @PostMapping("/refresh")
    public ResponseEntity<GenericDataDto<String>> refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        checkRefreshToken(refreshToken);

        LoginResponseDto res = authService.refresh(refreshToken);
        ResponseCookie cookie
                = refreshTokenCookieProvider.createRefreshTokenCookie(res.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new GenericDataDto<>(res.accessToken()));
    }

    private void checkRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}
