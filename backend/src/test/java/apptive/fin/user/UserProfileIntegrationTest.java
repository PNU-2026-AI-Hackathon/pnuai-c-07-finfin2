package apptive.fin.user;

import apptive.fin.category.entity.CategoryOption;
import apptive.fin.category.repository.CategoryOptionRepository;
import apptive.fin.global.error.BusinessException;
import apptive.fin.support.IntegrationTestSupport;
import apptive.fin.term.dto.TermResponseDto;
import apptive.fin.term.dto.UserTermRequestDto;
import apptive.fin.term.service.TermService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 실제 DB(Testcontainers Postgres)에서 schema.sql + data.sql(신규 약관·중위소득·카테고리 시드 포함)을
 * 로드해 저장→조회→철회 전 과정을 검증. 이 테스트가 통과하면 ddl-auto=validate와 엔티티/DDL 정합성,
 * JSON 컨버터 왕복, 동의 게이트, 철회 삭제가 모두 실 DB에서 동작함을 보장한다.
 */
class UserProfileIntegrationTest extends IntegrationTestSupport {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Autowired private UserRepository userRepository;
    @Autowired private UserProfileRepository userProfileRepository;
    @Autowired private UserProfileService userProfileService;
    @Autowired private TermService termService;
    @Autowired private CategoryOptionRepository categoryOptionRepository;

    @Test
    void 동의없이_저장하면_거부되고_동의후_저장조회_철회삭제까지_동작한다() {
        Long userId = createUser();

        Map<String, Long> optionIdByCode = categoryOptionRepository.findAll().stream()
                .collect(Collectors.toMap(CategoryOption::getCode, CategoryOption::getId, (a, b) -> a));
        Long seoulId = optionIdByCode.get("REGION_SEOUL");
        Long salaryTransferId = optionIdByCode.get("BANK_SALARY_TRANSFER");

        LocalDate birthdate = LocalDate.now(KST).minusYears(30);
        UserProfileRequestDto request = new UserProfileRequestDto(
                birthdate, 3600L, 1, 100, 24,
                true, true, true, 500_000L,
                List.of("MB"), List.of("SH"), List.of("WR"),
                List.of(seoulId, salaryTransferId));

        // 1) 동의 없이 저장 → 거부, 저장 안 됨
        assertThatThrownBy(() -> userProfileService.upsert(userId, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(UserErrorCode.PROFILE_CONSENT_REQUIRED));
        assertThat(userProfileRepository.findByUserId(userId)).isEmpty();

        // 2) 저장 동의(PRIVACY_STORAGE_CONSENT) 기록
        Long storageConsentVersionId = currentStorageConsentVersionId(userId);
        agree(userId, storageConsentVersionId, true);

        // 3) 저장 성공 후 조회 → 서버 계산값 재산출 확인
        userProfileService.upsert(userId, request);
        UserProfileResponseDto response = userProfileService.getProfile(userId);

        assertThat(response.hasProfile()).isTrue();
        // 원본 저장값(프리필용) 왕복
        assertThat(response.birthdate()).isEqualTo(birthdate);
        assertThat(response.mainBanks()).containsExactly("MB");
        assertThat(response.neverUsedBanks()).containsExactly("SH");
        assertThat(response.selectedOptionIds()).containsExactly(seoulId, salaryTransferId);
        // 서버 계산 표시값
        UserProfileResponseDto.Display display = response.display();
        assertThat(display.age()).isEqualTo(30);
        assertThat(display.householdIncomeGuide()).isEqualTo(256);   // data.sql: 2026·1인·100% = 256
        assertThat(display.region()).isEqualTo("서울");
        assertThat(display.preferentialConditions()).contains("급여이체 가능");
        // 주거래(MB) 제외, 첫거래/재예치만 (provider 미시드 → 코드 그대로 폴백)
        assertThat(display.transactionHistory().firstTransactionBanks()).containsExactly("SH");
        assertThat(display.transactionHistory().redepositBanks()).containsExactly("WR");

        // 4) 동의 철회 → 저장된 프로필 삭제
        agree(userId, storageConsentVersionId, false);
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

    private Long currentStorageConsentVersionId(Long userId) {
        // TermResponseDto 프로젝션은 code/versionId를 미리 담아 반환하므로 지연로딩 문제가 없다.
        return termService.getTermsForUser(userId).stream()
                .filter(t -> UserProfileService.STORAGE_CONSENT_CODE.equals(t.code()))
                .map(TermResponseDto::versionId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("PRIVACY_STORAGE_CONSENT 약관 시드 누락"));
    }

    private void agree(Long userId, Long versionId, boolean agreed) {
        termService.saveTermAgreementResults(userId, new UserTermRequestDto(List.of(
                new UserTermRequestDto.TermAgreement(versionId, agreed))));
    }
}
