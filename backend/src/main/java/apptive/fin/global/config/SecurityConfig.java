package apptive.fin.global.config;

import apptive.fin.auth.oauth.OAuth2FailureHandler;
import apptive.fin.auth.oauth.OAuth2SuccessHandler;
import apptive.fin.auth.oauth.OAuth2UserService;
import apptive.fin.auth.security.BusinessAccessDeniedHandler;
import apptive.fin.auth.security.BusinessAuthenticationEntryPoint;
import apptive.fin.auth.security.JwtAuthFilter;
import apptive.fin.auth.util.JwtUtil;
import apptive.fin.global.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] SWAGGER_PATHS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    private final JwtUtil jwtUtil;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final BusinessAuthenticationEntryPoint businessAuthenticationEntryPoint;
    private final BusinessAccessDeniedHandler businessAccessDeniedHandler;
    private final AppProperties appProperties;
    private final Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    if (isSwaggerEnabledProfile()) {
                        auth.requestMatchers(SWAGGER_PATHS).permitAll();
                    } else {
                        auth.requestMatchers(SWAGGER_PATHS).denyAll();
                    }

                    auth
                            .requestMatchers("/oauth2/authorization/**", "/login/oauth2/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/auth/logout", "/auth/refresh").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                            .requestMatchers(HttpMethod.POST, "/search/dynamic-form").permitAll()
                            .requestMatchers(HttpMethod.GET, "/search/products").permitAll()
                            .requestMatchers(HttpMethod.POST, "/search/products").permitAll()
                            .requestMatchers(HttpMethod.POST, "/search/products/*/detail").permitAll()
                            .requestMatchers(HttpMethod.GET, "/providers/banks").permitAll()
                            .anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
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

        configuration.setAllowedOrigins(List.of(
                appProperties.frontend().url()
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private boolean isSwaggerEnabledProfile() {
        return environment.acceptsProfiles(Profiles.of("dev", "test"));
    }
}
