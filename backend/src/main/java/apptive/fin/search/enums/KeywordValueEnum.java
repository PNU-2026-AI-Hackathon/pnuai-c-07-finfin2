package apptive.fin.search.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KeywordValueEnum {

    // 1. 거주 지역
    REGION_SEOUL("REGION_SEOUL"),
    REGION_BUSAN("REGION_BUSAN"),
    REGION_DAEGU("REGION_DAEGU"),
    REGION_INCHEON("REGION_INCHEON"),
    REGION_GWANGJU("REGION_GWANGJU"),
    REGION_DAEJEON("REGION_DAEJEON"),
    REGION_ULSAN("REGION_ULSAN"),
    REGION_SEJONG("REGION_SEJONG"),
    REGION_GYEONGGI("REGION_GYEONGGI"),
    REGION_GANGWON("REGION_GANGWON"),
    REGION_CHUNGBUK("REGION_CHUNGBUK"),
    REGION_CHUNGNAM("REGION_CHUNGNAM"),
    REGION_JEONBUK("REGION_JEONBUK"),
    REGION_JEONNAM("REGION_JEONNAM"),
    REGION_GYEONGBUK("REGION_GYEONGBUK"),
    REGION_GYEONGNAM("REGION_GYEONGNAM"),
    REGION_JEJU("REGION_JEJU"),

    // 2. 현재 신분
    STATUS_UNEMPLOYED("STATUS_UNEMPLOYED"),
    STATUS_PART_TIME("STATUS_PART_TIME"),
    STATUS_SME_WORKER("STATUS_SME_WORKER"),
    STATUS_MILITARY("STATUS_MILITARY"),

    // 3. 저축 기간
    TERM_OVER_3_YEARS("TERM_OVER_3_YEARS"),
    TERM_2_TO_3_YEARS("TERM_2_TO_3_YEARS"),
    TERM_AROUND_1_YEAR("TERM_AROUND_1_YEAR"),

    // 4. 핵심 혜택 (핵심 기간)
    BENEFIT_MAX_INTEREST("BENEFIT_MAX_INTEREST"),
    BENEFIT_TAX_FREE("BENEFIT_TAX_FREE"),
    BENEFIT_EASY_CONDITION("BENEFIT_EASY_CONDITION"),
    BENEFIT_GOV_SUBSIDY("BENEFIT_GOV_SUBSIDY"),

    // 5. 상품 관심사
    INTEREST_SAVINGS("INTEREST_SAVINGS"),
    INTEREST_LOAN("INTEREST_LOAN"),

    // 6. 은행 거래 - 우대 사항
    BANK_FIRST_TRANSACTION("BANK_FIRST_TRANSACTION"), // 첫 거래 우대
    BANK_SALARY_TRANSFER("BANK_SALARY_TRANSFER"), // 급여이체우대
    BANK_CARD_USAGE("BANK_CARD_USAGE"), // 카드 실적 우대
    BANK_AUTO_TRANSFER("BANK_AUTO_TRANSFER"), // 자동이체 우대
    BANK_MARKETING("BANK_MARKETING"), // 마케팅 동의 우대
    BANK_REDEPOSIT("BANK_REDEPOSIT"), // 재예치
    BANK_ONLINE_JOIN("BANK_ONLINE_JOIN"),
    BANK_AGE("BANK_AGE"),
    BANK_ETC("BANK_ETC"), // 기타(키워드 미분류 우대금리, 기본 불충족)
    ;

    private final String code;

    public static KeywordValueEnum from(String code) {
        try {
            return KeywordValueEnum.valueOf(code);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** 우대금리 조건 키워드(BANK_*)인지. */
    public boolean isPreferentialRate() {
        return name().startsWith("BANK_");
    }

    /** 가입 자격 제한 키워드(STATUS_*)인지. */
    public boolean isRequired() {
        return name().startsWith("STATUS_");
    }

    public boolean isTransactionHistoryCondition() {
        return this == BANK_FIRST_TRANSACTION || this == BANK_REDEPOSIT;
    }
}
