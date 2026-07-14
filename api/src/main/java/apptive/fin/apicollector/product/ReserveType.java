package apptive.fin.apicollector.product;

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
        if (code == null || code.isBlank()) {
            return null;
        }

        for (ReserveType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
