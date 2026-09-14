package apptive.fin.apicollector.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum InterestRateType {

    SINGLE_INTEREST("S", "단리"),
    COMPOUND_INTEREST("M", "복리");

    private final String code;
    private final String value;

    public static InterestRateType fromCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        for (InterestRateType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
