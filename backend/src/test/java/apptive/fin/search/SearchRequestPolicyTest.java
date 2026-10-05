package apptive.fin.search;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.enums.ProductCategoryEnum;
import apptive.fin.search.service.SearchRequestPolicy;
import apptive.fin.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchRequestPolicyTest {

    private SearchRequestPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new SearchRequestPolicy();
    }

    @Nested
    class 단기예치_검증 {

        @Test
        void 예치액이_있으면_검증_통과() {
            SearchRequestDto request = createShortTermRequest(1_000_000L, 1);
            ResolvedKeywords keywords = createShortTermKeywords();

            // 예외 없이 통과
            policy.validateForShortTerm(request, keywords);
        }

        @Test
        void 예치액이_없으면_예외발생() {
            SearchRequestDto request = createShortTermRequest(null, 1);
            ResolvedKeywords keywords = createShortTermKeywords();

            assertThatThrownBy(() -> policy.validateForShortTerm(request, keywords))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .extracting("errNum")
                    .isEqualTo("007");  // DEPOSIT_AMOUNT_REQUIRED
        }

        @Test
        void 예치액이_0이면_예외발생() {
            SearchRequestDto request = createShortTermRequest(0L, 1);
            ResolvedKeywords keywords = createShortTermKeywords();

            assertThatThrownBy(() -> policy.validateForShortTerm(request, keywords))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void 저축기간이_키워드와_saveTrmExact_모두_없으면_예외발생() {
            SearchRequestDto request = createShortTermRequest(1_000_000L, null);
            ResolvedKeywords keywordsWithoutPeriod = ResolvedKeywords.emptyKeywords();

            assertThatThrownBy(() -> policy.validateForShortTerm(request, keywordsWithoutPeriod))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .extracting("errNum")
                    .isEqualTo("005");  // SAVING_PERIOD_REQUIRED
        }

        @Test
        void saveTrmExact_없어도_키워드에_저축기간_있으면_통과() {
            SearchRequestDto request = createShortTermRequest(1_000_000L, null);  // saveTrmExact 없음
            ResolvedKeywords keywords = createShortTermKeywords();  // TERM_1_MONTH 있음

            // 키워드에서 저축기간 추출되므로 예외 없이 통과
            policy.validateForShortTerm(request, keywords);
        }

        @Test
        void request가_null이면_예외발생() {
            ResolvedKeywords keywords = createShortTermKeywords();

            assertThatThrownBy(() -> policy.validateForShortTerm(null, keywords))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .extracting("errNum")
                    .isEqualTo("008");  // INVALID_REQUEST
        }

        @Test
        void 유효하지_않은_저축기간이면_예외발생() {
            // 2개월은 허용되지 않음 (허용값: 1, 3, 6, 12, 24, 36)
            SearchRequestDto request = createShortTermRequest(1_000_000L, 2);
            ResolvedKeywords keywordsWithoutPeriod = ResolvedKeywords.emptyKeywords();

            assertThatThrownBy(() -> policy.validateForShortTerm(request, keywordsWithoutPeriod))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .extracting("errNum")
                    .isEqualTo("009");  // INVALID_SAVING_PERIOD
        }

        @Test
        void 음수_저축기간이면_예외발생() {
            SearchRequestDto request = createShortTermRequest(1_000_000L, -1);
            ResolvedKeywords keywordsWithoutPeriod = ResolvedKeywords.emptyKeywords();

            assertThatThrownBy(() -> policy.validateForShortTerm(request, keywordsWithoutPeriod))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .extracting("errNum")
                    .isEqualTo("009");  // INVALID_SAVING_PERIOD
        }
    }

    @Nested
    class 단기예치_개인화_활성화 {

        @Test
        void 모든조건_충족시_활성화() {
            SearchRequestDto request = createShortTermRequestComplete(1_000_000L, 1);
            ResolvedKeywords keywords = createShortTermKeywords();
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUseShortTermPersonalization(request, keywords, userDetails);

            assertThat(result).isTrue();
        }

        @Test
        void 비로그인이면_비활성화() {
            SearchRequestDto request = createShortTermRequestComplete(1_000_000L, 1);
            ResolvedKeywords keywords = createShortTermKeywords();

            boolean result = policy.canUseShortTermPersonalization(request, keywords, null);

            assertThat(result).isFalse();
        }

        @Test
        void 예치액이_없으면_비활성화() {
            SearchRequestDto request = createShortTermRequestComplete(null, 1);
            ResolvedKeywords keywords = createShortTermKeywords();
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUseShortTermPersonalization(request, keywords, userDetails);

            assertThat(result).isFalse();
        }

        @Test
        void 생년월일이_없으면_비활성화() {
            SearchRequestDto request = createShortTermRequestWithoutBirthdate(1_000_000L, 1);
            ResolvedKeywords keywords = createShortTermKeywords();
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUseShortTermPersonalization(request, keywords, userDetails);

            assertThat(result).isFalse();
        }

        @Test
        void 우대조건이_없으면_비활성화() {
            SearchRequestDto request = createShortTermRequestComplete(1_000_000L, 1);
            ResolvedKeywords keywords = ResolvedKeywords.emptyKeywords();  // bankConditions 비어있음
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUseShortTermPersonalization(request, keywords, userDetails);

            assertThat(result).isFalse();
        }

        @Test
        void 거래이력이_없으면_비활성화() {
            SearchRequestDto request = createShortTermRequestWithoutTransactionHistory(1_000_000L, 1);
            ResolvedKeywords keywords = createShortTermKeywords();
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUseShortTermPersonalization(request, keywords, userDetails);

            assertThat(result).isFalse();
        }

        @Test
        void 약관동의_전_역할은_비활성화() {
            SearchRequestDto request = createShortTermRequestComplete(1_000_000L, 1);
            ResolvedKeywords keywords = createShortTermKeywords();
            AuthUserDetails userDetails = mockUserDetails(UserRole.BEFORE_AGREED);

            boolean result = policy.canUseShortTermPersonalization(request, keywords, userDetails);

            assertThat(result).isFalse();
        }
    }

    @Nested
    class 대분류별_통합_정책 {

        @Test
        void 단기예치_대분류면_단기예치_검증_사용() {
            SearchRequestDto request = createShortTermRequest(1_000_000L, 1);
            ResolvedKeywords keywords = ResolvedKeywords.emptyKeywords();

            // 예외 없이 통과
            policy.validateForCategory(request, keywords, ProductCategoryEnum.SHORT_TERM);
        }

        @Test
        void 목돈만들기_대분류면_기존_검증_사용() {
            SearchRequestDto request = createLongTermRequest(100_000L);
            ResolvedKeywords keywords = createLongTermKeywords();

            // 예외 없이 통과
            policy.validateForCategory(request, keywords, ProductCategoryEnum.LONG_TERM);
        }

        @Test
        void 단기예치_대분류_개인화는_단기예치_정책_사용() {
            SearchRequestDto request = createShortTermRequestComplete(1_000_000L, 1);
            ResolvedKeywords keywords = createShortTermKeywords();
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUsePersonalization(
                    request, keywords, userDetails, ProductCategoryEnum.SHORT_TERM);

            assertThat(result).isTrue();
        }

        @Test
        void 목돈만들기_대분류_개인화는_기존_정책_사용() {
            SearchRequestDto request = createLongTermRequestComplete();
            ResolvedKeywords keywords = createLongTermKeywords();
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUsePersonalization(
                    request, keywords, userDetails, ProductCategoryEnum.LONG_TERM);

            assertThat(result).isTrue();
        }
    }

    // === Helper methods ===

    private SearchRequestDto createShortTermRequest(Long depositAmount, Integer saveTrmExact) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, null,
                        depositAmount, saveTrmExact,
                        null, null, List.of()
                )
        );
    }

    // 단기예치 모든 조건 충족 (생년월일 + 거래이력)
    private SearchRequestDto createShortTermRequestComplete(Long depositAmount, Integer saveTrmExact) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(25), null, null, null, null,
                        null, null, null, null,
                        depositAmount, saveTrmExact,
                        List.of("KB"), List.of("NH"), List.of()  // 거래이력 포함
                )
        );
    }

    // 단기예치 생년월일 없음
    private SearchRequestDto createShortTermRequestWithoutBirthdate(Long depositAmount, Integer saveTrmExact) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, null,
                        depositAmount, saveTrmExact,
                        List.of("KB"), List.of("NH"), List.of()  // 거래이력 있음
                )
        );
    }

    // 단기예치 거래이력 없음
    private SearchRequestDto createShortTermRequestWithoutTransactionHistory(Long depositAmount, Integer saveTrmExact) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(25), null, null, null, null,
                        null, null, null, null,
                        depositAmount, saveTrmExact,
                        null, null, List.of()  // 거래이력 없음
                )
        );
    }

    private ResolvedKeywords createShortTermKeywords() {
        return new ResolvedKeywords(
                List.of(),
                List.of(),
                KeywordValueEnum.TERM_1_MONTH,
                List.of(),
                List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)  // 우대조건 있음
        );
    }

    private SearchRequestDto createLongTermRequest(Long monthlySavingsGoal) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, monthlySavingsGoal,
                        null, null,
                        null, null, List.of()
                )
        );
    }

    private SearchRequestDto createLongTermRequestComplete() {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(25),
                        50_000_000L,  // annualIncome
                        3,            // householdSize
                        100,          // householdIncomePercent
                        null,
                        null, null, null,
                        100_000L,     // monthlySavingsGoal
                        null, null,
                        List.of(), List.of(), List.of()
                )
        );
    }

    private ResolvedKeywords createLongTermKeywords() {
        return new ResolvedKeywords(
                List.of(),
                List.of(),
                KeywordValueEnum.TERM_12_MONTH,
                List.of(),
                List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)
        );
    }

    private AuthUserDetails mockUserDetails(UserRole role) {
        AuthUserDetails userDetails = mock(AuthUserDetails.class);
        when(userDetails.getRole()).thenReturn(role);
        return userDetails;
    }
}
