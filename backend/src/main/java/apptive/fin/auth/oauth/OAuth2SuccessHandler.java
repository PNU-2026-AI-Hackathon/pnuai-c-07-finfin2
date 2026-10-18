package apptive.fin.auth.oauth;

import apptive.fin.auth.service.AuthService;
import apptive.fin.auth.util.RefreshTokenCookieProvider;
import apptive.fin.global.properties.AppProperties;
import apptive.fin.auth.util.JwtUtil;
import apptive.fin.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;
    private final AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        String refreshToken = authService.getRefreshToken(user);

        ResponseCookie cookie = refreshTokenCookieProvider.createRefreshTokenCookie(refreshToken);
//        log.info("Refresh Token: {}", cookie.getValue());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        getRedirectStrategy().sendRedirect(
                request, response,
                appProperties.frontend().url() + appProperties.oAuth2().successRedirectUrl()
        );
    }

}
