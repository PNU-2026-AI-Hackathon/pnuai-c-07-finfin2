package apptive.fin.user;

import apptive.fin.category.entity.CategoryOption;
import apptive.fin.category.repository.CategoryOptionRepository;
import apptive.fin.support.IntegrationTestSupport;
import apptive.fin.user.dto.UserProfileRequestDto;
import apptive.fin.user.dto.UserProfileResponseDto;
import apptive.fin.user.entity.User;
import apptive.fin.user.repository.UserProfileRepository;
import apptive.fin.user.repository.UserRepository;
import apptive.fin.user.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 실제 DB(Testcontainers Postgres)에서 schema.sql + data.sql(약관·중위소득·카테고리 시드 포함)을
 * 로드해 저장→조회→삭제 전 과정을 검증. 이 테스트가 통과하면 ddl-auto=validate와 엔티티/DDL 정합성,
 * JSON 컨버터 왕복, 서버 계산 표시값 산출, 프로필 삭제가 모두 실 DB에서 동작함을 보장한다.
 * (저장에 대한 법적 근거는 회원가입 필수 약관으로 확보되고, 엔드포인트 접근 권한은 컨트롤러 권한에서
 *  통제하므로 서비스 계층에는 별도 동의 게이트가 없다.)
 */
class UserProfileIntegrationTest extends IntegrationTestSupport {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Autowired private UserRepository userRepository;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private UserProfileService userProfileService;
    @Autowired private CategoryOptionRepository categoryOptionRepository;

    @Test
    void 저장_조회_삭제까지_동작한다() {
        Long userId = createUser();

        Map<String, Long> optionIdByCode = categoryOptionRepository.findAll().stream()
                .collect(Collectors.toMap(CategoryOption::getCode, CategoryOption::getId, (a, b) -> a));
        Long seoulId = optionIdByCode.get("REGION_SEOUL");
        Long salaryTransferId = optionIdByCode.get("BANK_SALARY_TRANSFER");

        LocalDate birthdate = LocalDate.now(KST).minusYears(30);
        UserProfileRequestDto request = new UserProfileRequestDto(
                birthdate, 3600L, 1, 100, 24,
                true, true, true, 500_000L,
                List.of("SH"), List.of("WR"),
                List.of(seoulId, salaryTransferId));

        // 1) 저장 후 조회 → 서버 계산값 재산출 확인
        userProfileService.upsert(userId, request);
        UserProfileResponseDto response = userProfileService.getProfile(userId);

        assertThat(response.hasProfile()).isTrue();
        // 원본 저장값(프리필용) 왕복
        assertThat(response.birthdate()).isEqualTo(birthdate);
        assertThat(response.neverUsedBanks()).containsExactly("SH");
        assertThat(response.selectedOptionIds()).containsExactly(seoulId, salaryTransferId);
        // 서버 계산 표시값
        UserProfileResponseDto.Display display = response.display();
        assertThat(display.age()).isEqualTo(30);
        assertThat(display.householdIncomeGuide()).isEqualTo(256);   // data.sql: 2026·1인·100% = 256
        assertThat(display.region()).isEqualTo("서울");
        assertThat(display.preferentialConditions()).contains("급여이체 가능");
        // provider 미시드 → 첫거래/재예치 코드는 그대로 폴백
        assertThat(display.transactionHistory().firstTransactionBanks()).containsExactly("SH");
        assertThat(display.transactionHistory().redepositBanks()).containsExactly("WR");

        // 2) 삭제 → 프로필 사라지고 hasProfile=false
        userProfileService.deleteProfile(userId);
        assertThat(userProfileRepository.findByUserId(userId)).isEmpty();
        assertThat(userProfileService.getProfile(userId).hasProfile()).isFalse();
    }

    private Long createUser() {
        User user = userRepository.save(User.builder()
                .name("profile-tester")
                .email("profile-tester@example.com")
                .provider("google")
                .providerId("pid-" + System.nanoTime())
                .userRole(UserRole.RECOMMENDATION)
                .build());
        return user.getId();
    }
}
