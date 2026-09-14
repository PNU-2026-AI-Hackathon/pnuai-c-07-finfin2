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

            // 예외 없이 통과
            policy.validateForShortTerm(request);
        }

        @Test
        void 예치액이_없으면_예외발생() {
            SearchRequestDto request = createShortTermRequest(null, 1);

            assertThatThrownBy(() -> policy.validateForShortTerm(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .extracting("errNum")
                    .isEqualTo("007");  // DEPOSIT_AMOUNT_REQUIRED
        }

        @Test
        void 예치액이_0이면_예외발생() {
            SearchRequestDto request = createShortTermRequest(0L, 1);

            assertThatThrownBy(() -> policy.validateForShortTerm(request))
                    .isInstanceOf(BusinessException.class);
        }

        @Test
        void 저축기간이_없으면_예외발생() {
            SearchRequestDto request = createShortTermRequest(1_000_000L, null);

            assertThatThrownBy(() -> policy.validateForShortTerm(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .extracting("errNum")
                    .isEqualTo("005");  // SAVING_PERIOD_REQUIRED
        }

        @Test
        void request가_null이면_예외발생() {
            assertThatThrownBy(() -> policy.validateForShortTerm(null))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .extracting("errNum")
                    .isEqualTo("008");  // INVALID_REQUEST
        }
    }

    @Nested
    class 단기예치_개인화_활성화 {

        @Test
        void 로그인하고_예치액과_생년월일_입력시_활성화() {
            SearchRequestDto request = createShortTermRequestWithBirthdate(1_000_000L, 1);
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUseShortTermPersonalization(request, userDetails);

            assertThat(result).isTrue();
        }

        @Test
        void 비로그인이면_비활성화() {
            SearchRequestDto request = createShortTermRequestWithBirthdate(1_000_000L, 1);

            boolean result = policy.canUseShortTermPersonalization(request, null);

            assertThat(result).isFalse();
        }

        @Test
        void 예치액이_없으면_비활성화() {
            SearchRequestDto request = createShortTermRequestWithBirthdate(null, 1);
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUseShortTermPersonalization(request, userDetails);

            assertThat(result).isFalse();
        }

        @Test
        void 생년월일이_없으면_비활성화() {
            SearchRequestDto request = createShortTermRequest(1_000_000L, 1);
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUseShortTermPersonalization(request, userDetails);

            assertThat(result).isFalse();
        }

        @Test
        void 약관동의_전_역할은_비활성화() {
            SearchRequestDto request = createShortTermRequestWithBirthdate(1_000_000L, 1);
            AuthUserDetails userDetails = mockUserDetails(UserRole.BEFORE_AGREED);

            boolean result = policy.canUseShortTermPersonalization(request, userDetails);

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
            SearchRequestDto request = createShortTermRequestWithBirthdate(1_000_000L, 1);
            AuthUserDetails userDetails = mockUserDetails(UserRole.RECOMMENDATION);

            boolean result = policy.canUsePersonalization(
                    request, null, userDetails, ProductCategoryEnum.SHORT_TERM);

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

    private SearchRequestDto createShortTermRequestWithBirthdate(Long depositAmount, Integer saveTrmExact) {
        return new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        LocalDate.now().minusYears(25), null, null, null, null,
                        null, null, null, null,
                        depositAmount, saveTrmExact,
                        null, null, List.of()
                )
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
