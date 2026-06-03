package apptive.fin.global.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getHttpStatus();
    default String getCode() {
        return getCodePrefix() + getErrNum();
    }

    String getMessage();
    String getCodePrefix();
    String getErrNum();

}
