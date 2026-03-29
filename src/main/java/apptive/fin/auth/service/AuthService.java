package apptive.fin.auth.service;


import apptive.fin.auth.AuthErrorCode;
import apptive.fin.auth.dto.LoginResponseDto;
import apptive.fin.auth.entity.RefreshToken;
import apptive.fin.auth.repository.RefreshTokenRepository;
import apptive.fin.global.error.BusinessException;
import apptive.fin.auth.util.JwtUtil;
import apptive.fin.term.service.TermService;
import apptive.fin.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TermService termService;

    @Transactional
    public LoginResponseDto refresh(String token) {
        try {
            byte[] rawToken = Base64.getDecoder().decode(token);
            String hashedToken = jwtUtil.hashToken(rawToken);

            RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hashedToken)
                    .orElseThrow(()->new BusinessException(AuthErrorCode.UNAUTHORIZED));

            if (!refreshToken.checkValidity()) {
                throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
            }

            User user = refreshToken.getUser();

            if (refreshTokenRepository.deactivateIfActive(hashedToken) < 1) {
                throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
            }

            String accessToken = jwtUtil.generateAccessToken(
                    user.getId().toString(),
                    user.getUserRole(),
                    termService.didUserAgreeAllRequiredTerms(user.getId())
            );

            return LoginResponseDto.builder()
                    .refreshToken(getRefreshToken(user))
                    .accessToken(accessToken)
                    .build();
        }
        catch (IllegalArgumentException e) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }

    }

    @Transactional
    public String getRefreshToken(User user) {
        byte[] rawRefreshToken = jwtUtil.generateRefreshToken();
        String rawRefreshTokenStr = Base64.getEncoder().encodeToString(rawRefreshToken);
        String hashedRefreshToken = jwtUtil.hashToken(rawRefreshToken);


        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hashedRefreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getRefreshExpiration()))
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);
        return rawRefreshTokenStr;
    }

    @Transactional
    public void logout(String refreshToken) {
        try {
            byte[] rawToken = Base64.getDecoder().decode(refreshToken);
            String hashedToken = jwtUtil.hashToken(rawToken);
            refreshTokenRepository.deleteByTokenHash(hashedToken);
        }
        catch (IllegalArgumentException e) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }
    }
}
