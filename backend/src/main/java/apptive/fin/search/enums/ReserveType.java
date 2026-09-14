package apptive.fin.search.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReserveType {
    FIXED("S", "정액적립식"),
    FREE("F", "자유적립식");

    private final String code;
    private final String label;

    public static ReserveType fromApiCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("rsrv_type 값이 없습니다.");
        }
        return switch (code) {
            case "S" -> FIXED;
            case "F" -> FREE;
            default -> throw new IllegalArgumentException("알 수 없는 rsrv_type: " + code);
        };
    }
}
