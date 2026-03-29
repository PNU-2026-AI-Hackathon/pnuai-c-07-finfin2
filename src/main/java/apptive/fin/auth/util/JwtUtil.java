package apptive.fin.auth.util;

import apptive.fin.global.properties.JwtProperties;
import apptive.fin.user.UserRole;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

//    private final String SECRET;
//    private final int EXPIRATION;
//    private final int REFRESH_EXPIRATION;
    private final SecureRandom secureRandom;
    private final JwtProperties jwtProperties;


    public JwtUtil(
        JwtProperties jwtProperties
    ) {
        this.secureRandom = new SecureRandom();
        this.jwtProperties = jwtProperties;
    }

    private SecretKey getKey() {
        String secret = jwtProperties.secret();
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public int getRefreshExpiration() {
        return jwtProperties.refreshExpiration();
    }

    public String generateAccessToken(String userId, UserRole role, boolean requiredTermsAgreement) {
        return Jwts.builder()
                .subject(userId)
                .claim("role", role.name())
                .claim("required_terms_agreement", requiredTermsAgreement)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiration() * 1000L))
                .signWith(getKey())
                .compact();
    }

    public byte[] generateRefreshToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    public String hashToken(byte[] tokenBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedToken = md.digest(tokenBytes); // 32바이트
            return Base64.getEncoder().encodeToString(hashedToken);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not supported", e);
        }
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(
                Jwts.parser()
                        .verifyWith(getKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }

    public boolean getRequiredTermsAgreementFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("required_terms_agreement", Boolean.class);
    }

    public UserRole getRoleFromToken(String token) {
        String roleStr = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
        return UserRole.valueOf(roleStr);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}