package apptive.fin.user;

import apptive.fin.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "유저를 찾을 수 없음."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,"002","이미 사용중인 이메일."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"003","인증되지 않음."),
    PROFILE_CONSENT_REQUIRED(HttpStatus.FORBIDDEN,"004","개인정보 저장 동의가 필요합니다.") ;


    private final String codePrefix = "U";
    private final HttpStatus httpStatus;
    private final String errNum;
    private final String message;
}
