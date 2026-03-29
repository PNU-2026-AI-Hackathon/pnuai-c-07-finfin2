package apptive.fin.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "001", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "002", "서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "003", "올바르지 않은 입력입니다."),
    ;

    private final String codePrefix = "C";  // 공통 오류 -> 'C'ommon
    private final HttpStatus httpStatus;
    private final String errNum;
    private final String message;
}
