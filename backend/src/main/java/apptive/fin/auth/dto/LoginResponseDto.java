package apptive.fin.auth.dto;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {
}
