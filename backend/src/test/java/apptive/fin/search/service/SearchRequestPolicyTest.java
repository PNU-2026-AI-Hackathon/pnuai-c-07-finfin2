package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.SearchErrorCode;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.ResolvedKeywords;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchRequestPolicyTest {

    private final SearchRequestPolicy policy = new SearchRequestPolicy();

    @Test
    void 월납입희망액이_없으면_추천요청을_거절한다() {
        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, null, null,
                        null, null, null
                )
        );
        ResolvedKeywords keywords = new ResolvedKeywords(
                List.of(), List.of(), null, List.of(), List.of()
        );

        assertThatThrownBy(() -> policy.validateForRecommendation(request, keywords))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
                        .isEqualTo(SearchErrorCode.MONTHLY_SAVINGS_GOAL_REQUIRED));
    }

    @Test
    void 저축기간이_없으면_추천요청을_거절한다() {
        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, 50L, null,
                        null, null, null
                )
        );
        ResolvedKeywords keywords = new ResolvedKeywords(
                List.of(), List.of(), null, List.of(), List.of()
        );

        assertThatThrownBy(() -> policy.validateForRecommendation(request, keywords))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
                        .isEqualTo(SearchErrorCode.SAVING_PERIOD_REQUIRED));
    }

    @Test
    void 은행거래조건이_없으면_추천요청을_거절한다() {
        SearchRequestDto request = new SearchRequestDto(
                List.of(),
                new DetailedOptionsDto(
                        null, null, null, null, null,
                        null, null, null, 50L, null,
                        null, null, null
                )
        );
        ResolvedKeywords keywords = new ResolvedKeywords(
                List.of(), List.of(), KeywordValueEnum.TERM_AROUND_1_YEAR, List.of(), List.of()
        );

        assertThatThrownBy(() -> policy.validateForRecommendation(request, keywords))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getErrorCode())
                        .isEqualTo(SearchErrorCode.BANK_CONDITION_REQUIRED));
    }

    @Test
    void 로그인하고_1단계와_2단계를_완료하면_개인화를_허용한다() {
        boolean allowed = policy.canUsePersonalization(
                request(completeDetailedOptions()),
                completeKeywords(),
                new AuthUserDetails(1L, UserRole.RECOMMENDATION)
        );

        assertThat(allowed).isTrue();
    }

    @Test
    void 비로그인이면_입력을_완료해도_개인화를_허용하지_않는다() {
        boolean allowed = policy.canUsePersonalization(
                request(completeDetailedOptions()), completeKeywords(), null);

        assertThat(allowed).isFalse();
    }

    @Test
    void 단계1_필수값이_하나라도_없으면_개인화를_허용하지_않는다() {
        DetailedOptionsDto withoutMonthlySavingsGoal = detailedOptions(
                LocalDate.of(2000, 1, 1), 30_000_000L, 3, 100,
                null, List.of(), List.of(), List.of());
        ResolvedKeywords withoutSavingPeriod = new ResolvedKeywords(
                List.of(), List.of(), null,
                List.of(), List.of(KeywordValueEnum.BANK_SALARY_TRANSFER));
        ResolvedKeywords withoutBankCondition = new ResolvedKeywords(
                List.of(), List.of(), KeywordValueEnum.TERM_AROUND_1_YEAR,
                List.of(), List.of());
        AuthUserDetails user = new AuthUserDetails(1L, UserRole.RECOMMENDATION);

        assertThat(policy.canUsePersonalization(
                request(withoutMonthlySavingsGoal), completeKeywords(), user)).isFalse();
        assertThat(policy.canUsePersonalization(
                request(completeDetailedOptions()), withoutSavingPeriod, user)).isFalse();
        assertThat(policy.canUsePersonalization(
                request(completeDetailedOptions()), withoutBankCondition, user)).isFalse();
    }

    @ParameterizedTest(name = "{0} 미입력 시 개인화 불가")
    @MethodSource("missingStep2Fields")
    void 단계2_필수값이_하나라도_없으면_개인화를_허용하지_않는다(
            String fieldName,
            DetailedOptionsDto detailedOptions
    ) {
        boolean allowed = policy.canUsePersonalization(
                request(detailedOptions),
                completeKeywords(),
                new AuthUserDetails(1L, UserRole.RECOMMENDATION)
        );

        assertThat(allowed).as(fieldName).isFalse();
    }

    private static Stream<Arguments> missingStep2Fields() {
        return Stream.of(
                Arguments.of("생년월일", detailedOptions(
                        null, 30_000_000L, 3, 100, 50L, List.of(), List.of(), List.of())),
                Arguments.of("개인 연 소득", detailedOptions(
                        LocalDate.of(2000, 1, 1), null, 3, 100, 50L, List.of(), List.of(), List.of())),
                Arguments.of("가구원 수", detailedOptions(
                        LocalDate.of(2000, 1, 1), 30_000_000L, null, 100, 50L, List.of(), List.of(), List.of())),
                Arguments.of("가구 소득", detailedOptions(
                        LocalDate.of(2000, 1, 1), 30_000_000L, 3, null, 50L, List.of(), List.of(), List.of())),
                Arguments.of("주거래 은행", detailedOptions(
                        LocalDate.of(2000, 1, 1), 30_000_000L, 3, 100, 50L, null, List.of(), List.of())),
                Arguments.of("미거래 은행", detailedOptions(
                        LocalDate.of(2000, 1, 1), 30_000_000L, 3, 100, 50L, List.of(), null, List.of())),
                Arguments.of("만기 거래 은행", detailedOptions(
                        LocalDate.of(2000, 1, 1), 30_000_000L, 3, 100, 50L, List.of(), List.of(), null))
        );
    }

    private static SearchRequestDto request(DetailedOptionsDto detailedOptions) {
        return new SearchRequestDto(List.of(), detailedOptions);
    }

    private static ResolvedKeywords completeKeywords() {
        return new ResolvedKeywords(
                List.of(), List.of(), KeywordValueEnum.TERM_AROUND_1_YEAR,
                List.of(), List.of(KeywordValueEnum.BANK_SALARY_TRANSFER)
        );
    }

    private static DetailedOptionsDto completeDetailedOptions() {
        return detailedOptions(
                LocalDate.of(2000, 1, 1), 30_000_000L, 3, 100,
                50L, List.of(), List.of(), List.of());
    }

    private static DetailedOptionsDto detailedOptions(
            LocalDate birthdate,
            Long annualIncome,
            Integer householdSize,
            Integer householdIncomePercent,
            Long monthlySavingsGoal,
            List<String> mainBanks,
            List<String> neverUsedBanks,
            List<String> maturedSavingBanks
    ) {
        return new DetailedOptionsDto(
                birthdate, annualIncome, householdSize, householdIncomePercent,
                null, null, null, null, monthlySavingsGoal,
                mainBanks, neverUsedBanks, maturedSavingBanks, null
        );
    }
}
