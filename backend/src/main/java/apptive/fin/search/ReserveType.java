package apptive.fin.search;

public enum ReserveType {
    FIXED, // 정액적립식
    FREE;  // 자유적립식

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
