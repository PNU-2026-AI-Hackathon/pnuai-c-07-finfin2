package apptive.fin.global.util;

import apptive.fin.auth.util.JwtUtil;
import apptive.fin.global.properties.JwtProperties;
import apptive.fin.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private static final String SECRET = "test-secret-key-that-is-long-enough-123456";
    private static final String OTHER_SECRET = "other-secret-key-that-is-long-enough-654321";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties(SECRET, 600, 86400);
        jwtUtil = new JwtUtil(jwtProperties);
    }

    @Test
    void 액세스_토큰을_생성하면_subject및_role과_requiredTermsAgreement를_담고_검증된다() {
        String token = jwtUtil.generateAccessToken("1", UserRole.ADMIN, true);

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.getUserIdFromToken(token)).isEqualTo(1L);
        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("role", String.class)).isEqualTo(UserRole.ADMIN.name());
        assertThat(claims.get("required_terms_agreement", Boolean.class)).isEqualTo(true);
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void 리프레시_토큰은_32바이트로_생성된다() {
        byte[] refreshToken = jwtUtil.generateRefreshToken();

        assertThat(refreshToken).hasSize(32);
    }

    @Test
    void 동일한_토큰은_항상_같은_SHA256_해시값으로_변환된다() {
        byte[] tokenBytes = "refresh-token".getBytes(StandardCharsets.UTF_8);

        String firstHash = jwtUtil.hashToken(tokenBytes);
        String secondHash = jwtUtil.hashToken(tokenBytes);

        assertThat(firstHash).isEqualTo(secondHash);
        assertThat(firstHash).isEqualTo("DrF2Q9TpJhFjeDpCCFnJLH0hL6liQQahK1EK++wmYSA=");
    }

    @Test
    void 형식이_잘못된_토큰은_유효하지_않다() {
        assertThat(jwtUtil.validateToken("not-a-jwt")).isFalse();
    }

    @Test
    void 다른_secret으로_서명된_토큰은_유효하지_않다() {
        String token = Jwts.builder()
                .subject("1")
                .claim("role", UserRole.ADMIN.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(OTHER_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThat(jwtUtil.validateToken(token)).isFalse();
    }

    @Test
    void 만료된_토큰은_유효하지_않다() {
        String token = Jwts.builder()
                .subject("1")
                .claim("role", UserRole.ADMIN.name())
                .issuedAt(new Date(System.currentTimeMillis() - 2_000))
                .expiration(new Date(System.currentTimeMillis() - 1_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThat(jwtUtil.validateToken(token)).isFalse();
    }

    @Test
    void subject가_숫자가_아닌_토큰에서_사용자_ID를_조회하면_예외가_발생한다() {
        String token = Jwts.builder()
                .subject("not-a-number")
                .claim("role", UserRole.ADMIN.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThatThrownBy(() -> jwtUtil.getUserIdFromToken(token))
                .isInstanceOf(NumberFormatException.class);
    }
}
