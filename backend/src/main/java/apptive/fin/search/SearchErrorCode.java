package apptive.fin.search;

import apptive.fin.global.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SearchErrorCode implements ErrorCode {

    OPTION_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"001","올바르지 않은 옵션 카테고리."),
    KEYWORD_REQUIRED(HttpStatus.BAD_REQUEST, "002", "키워드를 1개 이상 선택해주세요."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "003", "상품을 찾을 수 없습니다."),
    ;

    private final String codePrefix = "S";
    private final HttpStatus httpStatus;
    private final String errNum;
    private final String message;
}
