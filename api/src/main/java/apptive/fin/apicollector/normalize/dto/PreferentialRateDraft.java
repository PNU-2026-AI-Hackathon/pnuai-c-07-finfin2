package apptive.fin.apicollector.normalize.dto;

import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.util.TextMatch;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record PreferentialRateDraft(
        KeywordValueEnum keywordCode,
        BigDecimal rate,
        String description,
        Integer minAge,
        Integer maxAge
) {

    public boolean matchesKeywordCondition() {
        return switch (keywordCode) {
            case BANK_SALARY_TRANSFER -> containsAny(description, "급여", "월급", "salary");
            case BANK_CARD_USAGE -> containsAny(description, "카드", "체크카드", "신용카드", "결제실적", "전월결제", "card", "payment");
            case BANK_AUTO_TRANSFER -> containsAny(description, "자동이체", "자동 이체");
            case BANK_MARKETING -> containsAny(description, "마케팅", "상품서비스", "개인정보", "개인(신용)정보", "수집이용", "동의");
            case BANK_FIRST_TRANSACTION -> containsAny(description, "첫거래", "최초거래", "신규고객", "신규 고객", "첫 예금거래", "입출금통장 최초");
            case BANK_REDEPOSIT -> containsAny(description, "재예치", "재가입") && !hasAmountOrBalanceCondition(description);
            case BANK_ONLINE_JOIN -> containsAny(description, "인터넷 가입", "스마트뱅킹 가입", "비대면 가입", "모바일 가입", "온라인 가입", "online join", "mobile join");
            // BANK_AGE는 나이 구간(minAge/maxAge)이 있어야 유효하다. 구간 없는 나이 우대는
            // 백엔드가 나이로 필터링할 수 없어 모든 연령에 잘못 합산되므로 BANK_AGE로 인정하지 않는다.
            case BANK_AGE -> minAge != null || maxAge != null;
            // 기타: 나머지 BANK_* 키워드 어디에도 해당하지 않는 우대조건. 단, 특정 키워드에
            // 명백히 해당하면(LLM 오분류) ETC로 인정하지 않는다.
            case BANK_ETC -> !looksLikeModeledCondition();
            default -> false;
        };
    }

    // description이 기존 BANK_* 키워드(기타 제외) 중 하나에 명확히 매칭되는지 판정
    private boolean looksLikeModeledCondition() {
        for (KeywordValueEnum keyword : KeywordValueEnum.values()) {
            if (keyword != KeywordValueEnum.BANK_ETC
                    && keyword.name().startsWith("BANK_")
                    && toBuilder().keywordCode(keyword).build().matchesKeywordCondition()) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsAny(String value, String... tokens) {
        return TextMatch.containsAny(value, tokens);
    }

    private static boolean hasAmountOrBalanceCondition(String value) {
        return containsAny(value, "금액", "잔액", "평잔", "평균잔액", "요구불", "만원", "백만원", "억원");
    }
}
