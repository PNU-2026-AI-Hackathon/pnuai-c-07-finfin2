package apptive.fin.apicollector.normalize.dto;

import apptive.fin.apicollector.product.KeywordValueEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PreferentialRateDraftTest {

    private PreferentialRateDraft draft(KeywordValueEnum keyword, String description) {
        return PreferentialRateDraft.builder()
                .keywordCode(keyword)
                .rate(new BigDecimal("0.1"))
                .description(description)
                .build();
    }

    @Test
    void bankEtc_isAcceptedWhenDescriptionMatchesNoModeledKeyword() {
        // 나머지 BANK_* 어디에도 안 맞는 우대조건 → 기타로 인정
        assertThat(draft(KeywordValueEnum.BANK_ETC, "지점 방문 이벤트 참여 시").matchesKeywordCondition())
                .isTrue();
    }

    @Test
    void bankEtc_isRejectedWhenDescriptionClearlyMatchesModeledKeyword() {
        // 급여이체는 BANK_SALARY_TRANSFER로 분류돼야 하므로 ETC로 인정하지 않음(LLM 오분류 방지)
        assertThat(draft(KeywordValueEnum.BANK_ETC, "급여 이체 고객 우대").matchesKeywordCondition())
                .isFalse();
        assertThat(draft(KeywordValueEnum.BANK_ETC, "신용카드 결제실적 우대").matchesKeywordCondition())
                .isFalse();
    }

    @Test
    void modeledKeyword_matchesItsOwnTokens() {
        assertThat(draft(KeywordValueEnum.BANK_SALARY_TRANSFER, "급여 이체").matchesKeywordCondition())
                .isTrue();
        assertThat(draft(KeywordValueEnum.BANK_SALARY_TRANSFER, "관련 없는 조건").matchesKeywordCondition())
                .isFalse();
    }
}
