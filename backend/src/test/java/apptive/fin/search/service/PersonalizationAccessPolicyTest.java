package apptive.fin.search.service;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.search.dto.DetailedOptionsDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalizationAccessPolicyTest {

    private final PersonalizationAccessPolicy policy = new PersonalizationAccessPolicy();

    @Test
    void 로그인하고_2단계_필수정보를_입력하면_개인화를_허용한다() {
        SearchRequestDto request = request(detail(
                LocalDate.of(2000, 1, 1),
                0L,
                1,
                100,
                List.of(),
                List.of()
        ));

        boolean allowed = policy.canUsePersonalization(request, authenticatedUser());

        assertThat(allowed).isTrue();
    }

    @ParameterizedTest(name = "{0} 누락")
    @MethodSource("incompleteDetails")
    void 단계2_필수정보가_하나라도_누락되면_개인화를_허용하지않는다(
            String field,
            DetailedOptionsDto detail
    ) {
        boolean allowed = policy.canUsePersonalization(request(detail), authenticatedUser());

        assertThat(allowed).as(field).isFalse();
    }

    @Test
    void 비로그인이면_2단계_필수정보를_입력해도_개인화를_허용하지않는다() {
        SearchRequestDto request = request(detail(
                LocalDate.of(2000, 1, 1),
                30_000_000L,
                3,
                100,
                List.of(),
                List.of()
        ));

        boolean allowed = policy.canUsePersonalization(request, null);

        assertThat(allowed).isFalse();
    }

    private static Stream<Arguments> incompleteDetails() {
        LocalDate birthdate = LocalDate.of(2000, 1, 1);
        List<String> answeredHistory = List.of();

        return Stream.of(
                Arguments.of("생년월일", detail(null, 30_000_000L, 3, 100, answeredHistory, answeredHistory)),
                Arguments.of("개인 연 소득", detail(birthdate, null, 3, 100, answeredHistory, answeredHistory)),
                Arguments.of("가구원 수", detail(birthdate, 30_000_000L, null, 100, answeredHistory, answeredHistory)),
                Arguments.of("가구 소득", detail(birthdate, 30_000_000L, 3, null, answeredHistory, answeredHistory)),
                Arguments.of("첫 거래 이력", detail(birthdate, 30_000_000L, 3, 100, null, answeredHistory)),
                Arguments.of("재예치 거래 이력", detail(birthdate, 30_000_000L, 3, 100, answeredHistory, null)),
                Arguments.of("상세 정보", null)
        );
    }

    private static SearchRequestDto request(DetailedOptionsDto detail) {
        return new SearchRequestDto(List.of(), detail);
    }

    private static DetailedOptionsDto detail(
            LocalDate birthdate,
            Long annualIncome,
            Integer householdSize,
            Integer householdIncomePercent,
            List<String> neverUsedBanks,
            List<String> maturedSavingBanks
    ) {
        return new DetailedOptionsDto(
                birthdate,
                annualIncome,
                householdSize,
                householdIncomePercent,
                null,
                null,
                null,
                null,
                50L,
                null,
                neverUsedBanks,
                maturedSavingBanks,
                null
        );
    }

    private AuthUserDetails authenticatedUser() {
        return new AuthUserDetails(1L, UserRole.RECOMMENDATION);
    }
}
