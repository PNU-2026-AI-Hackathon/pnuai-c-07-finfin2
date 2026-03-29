package apptive.fin.global.config;

import apptive.fin.auth.security.BusinessAccessDeniedHandler;
import apptive.fin.auth.security.BusinessAuthenticationEntryPoint;
import apptive.fin.auth.security.JwtAuthFilter;
import apptive.fin.auth.oauth.OAuth2SuccessHandler;
import apptive.fin.auth.oauth.OAuth2UserService;
import apptive.fin.global.properties.AppProperties;
import apptive.fin.auth.util.JwtUtil;
import apptive.fin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final BusinessAuthenticationEntryPoint businessAuthenticationEntryPoint;
    private final BusinessAccessDeniedHandler businessAccessDeniedHandler;
    private final AppProperties appProperties;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
           //                     .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(businessAuthenticationEntryPoint)
                        .accessDeniedHandler(businessAccessDeniedHandler)
                )
                .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트엔드 도메인 명시 (로컬 개발용 및 실제 배포 도메인 추가)
        configuration.setAllowedOrigins(List.of(
                appProperties.frontend().url()
        ));

        // 허용할 HTTP 메서드 (OPTIONS는 Preflight 요청을 위해 필수)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));

        // 프론트엔드에서 응답 헤더를 읽을 수 있도록 노출 (Set-Cookie 등)
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        // 쿠키 및 인증 정보 전송 허용 (Refresh Token 쿠키를 위해 필수)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}