package apptive.fin.global.error;

import lombok.Builder;
import org.springframework.http.ResponseEntity;

@Builder
public record ErrorResponseDto(
        String code,
        String message
) {

    public static ErrorResponseDto of(ErrorCode errorCode) {
        return ErrorResponseDto.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public static ErrorResponseDto of(ErrorCode errorCode, String customMessage) {
        return ErrorResponseDto.builder()
                .code(errorCode.getCode())
                .message(customMessage)
                .build();
    }

    public static ResponseEntity<ErrorResponseDto> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(of(errorCode));
    }
}
