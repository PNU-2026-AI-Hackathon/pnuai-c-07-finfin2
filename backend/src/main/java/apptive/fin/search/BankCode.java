package apptive.fin.search;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum BankCode {
    KB("국민은행", "obank.kbstar.com"),
    SHINHAN("신한은행", "shinhan.com"),
    HANA("하나은행", "kebhana.com"),
    WOORI("우리은행", "spot.wooribank.com"),
    NH("농협은행", "nhlink.nonghyup.com"),
    IBK("중소기업은행", "mybank.ibk.co.kr"),
    SC("제일은행", "standardchartered.co.kr"),
    IM("아이엠뱅크", "imbank.co.kr"),
    BUSAN("부산은행", "busanbank.co.kr"),
    KJB("광주은행", "www.kjbank.com"),
    JB("전북은행", "www.jbbank.co.kr"),
    JEJU("제주은행", "www.e-jejubank.com"),
    KYONGNAM("경남은행", "knbank.co.kr"),
    SUHYUP("수협은행", "www.suhyup-bank.com"),
    KAKAO("주식회사 카카오뱅크", "kakaobank.com"),
    KBANK("주식회사 케이뱅크", "kbanknow.com"),
    TOSS("토스뱅크 주식회사", "tossbank.com");

    private final String displayName;
    private final String officialHost;

    public String displayName() {
        return displayName;
    }

    public String officialHost() {
        return officialHost;
    }

    public static boolean contains(String code) {
        if (code == null) return false;

        return Arrays.stream(values())
                .anyMatch(bankCode -> bankCode.name().equals(code));
    }
}
