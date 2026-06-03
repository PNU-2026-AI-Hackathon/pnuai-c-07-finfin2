package apptive.fin.auth;

import apptive.fin.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "001", "인증되지 않음."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "002", "인가되지 않음.")
    ;

    private final String codePrefix = "A";
    private final HttpStatus httpStatus;
    private final String errNum;
    private final String message;
}

