package apptive.fin.myfin;

import apptive.fin.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MyFinErrorCode implements ErrorCode {

    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "001", "찜한 상품을 찾을 수 없습니다."),
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "002", "이미 찜한 상품입니다."),
    FAVORITE_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "003", "찜 상품은 최대 20개까지 등록할 수 있습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "004", "상품을 찾을 수 없습니다.");

    private final String codePrefix = "MF";
    private final HttpStatus httpStatus;
    private final String errNum;
    private final String message;
}
