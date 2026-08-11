package apptive.fin.user;

import apptive.fin.category.entity.CategoryOption;
import apptive.fin.provider.entity.Provider;
import apptive.fin.provider.repository.ProviderRepository;
import apptive.fin.search.dto.MedianIncomesDto;
import apptive.fin.search.service.MedianIncomeService;
import apptive.fin.category.repository.CategoryOptionRepository;
import apptive.fin.user.dto.UserProfileRequestDto;
import apptive.fin.user.dto.UserProfileResponseDto;
import apptive.fin.user.entity.User;
import apptive.fin.user.entity.UserProfile;
import apptive.fin.user.repository.UserProfileRepository;
import apptive.fin.user.repository.UserRepository;
import apptive.fin.user.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final Long USER_ID = 1L;

    @Mock private UserProfileRepository userProfileRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryOptionRepository categoryOptionRepository;
    @Mock private ProviderRepository providerRepository;
    @Mock private MedianIncomeService medianIncomeService;

    @InjectMocks private UserProfileService userProfileService;

    @Captor private ArgumentCaptor<UserProfile> profileCaptor;

    // ── upsert (저장) ──

    @Test
    void upsert_기존_프로필이_없으면_새로_생성해_저장한다() {
        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser()));

        userProfileService.upsert(USER_ID, sampleRequest());

        verify(userProfileRepository).save(profileCaptor.capture());
        UserProfile saved = profileCaptor.getValue();
        assertThat(saved.getBirthdate()).isEqualTo(LocalDate.of(1996, 5, 20));
        assertThat(saved.getAnnualIncome()).isEqualTo(3600L);
        assertThat(saved.getHouseholdSize()).isEqualTo(1);
        assertThat(saved.getNeverUsedBanks()).containsExactly("SHINHAN");
        assertThat(saved.getSelectedOptionIds()).containsExactly(100L, 200L);
    }

    @Test
    void upsert_기존_프로필이_있으면_덮어쓰고_유저조회는_하지_않는다() {
        UserProfile existing = new UserProfile(mockUser());
        existing.apply(sampleRequest());

        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        UserProfileRequestDto updated = new UserProfileRequestDto(
                LocalDate.of(2000, 1, 1), 5000L, 2, 120, 12,
                false, true, false, 700_000L,
                List.of("SHINHAN"), List.of("WOORI"), List.of(300L));

        userProfileService.upsert(USER_ID, updated);

        assertThat(existing.getBirthdate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(existing.getAnnualIncome()).isEqualTo(5000L);
        assertThat(existing.getHouseholdIncomePercent()).isEqualTo(120);
        assertThat(existing.getSelectedOptionIds()).containsExactly(300L);
        verify(userRepository, never()).findById(eq(USER_ID));
    }

    @Test
    void deleteProfile_리포지토리_삭제를_위임한다() {
        userProfileService.deleteProfile(USER_ID);

        verify(userProfileRepository).deleteByUserId(USER_ID);
    }

    // ── getProfile (조회 + 서버 계산) ──

    @Test
    void getProfile_저장된_프로필이_없으면_hasProfile_false를_반환한다() {
        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        UserProfileResponseDto response = userProfileService.getProfile(USER_ID);

        assertThat(response.hasProfile()).isFalse();
        assertThat(response.display()).isNull();
    }

    @Test
    void getProfile_만나이_가구소득가이드_라벨_은행명을_계산해_반환한다() {
        LocalDate today = LocalDate.now(KST);
        UserProfile profile = new UserProfile(mockUser());
        profile.apply(new UserProfileRequestDto(
                today.minusYears(28), 3600L, 1, 100, 24,
                true, true, true, 500_000L,
                List.of("SHINHAN"), List.of("WOORI"),
                List.of(100L, 200L)));

        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(profile));
        when(categoryOptionRepository.findAllById(List.of(100L, 200L))).thenReturn(List.of(
                categoryOption("REGION_SEOUL", "서울"),
                categoryOption("BANK_SALARY_TRANSFER", "급여이체 가능")));
        when(providerRepository.findByCodeIn(anyList())).thenReturn(List.of(
                provider("SHINHAN", "신한은행"),
                provider("WOORI", "우리은행")));
        when(medianIncomeService.getMedianIncomesDto(anyInt(), eq(1))).thenReturn(
                MedianIncomesDto.builder().year(today.getYear()).householdSize(1)
                        .p60(0).p80(0).p100(256).p120(0).p150(0).p180(0).build());

        UserProfileResponseDto response = userProfileService.getProfile(USER_ID);

        assertThat(response.hasProfile()).isTrue();

        UserProfileResponseDto.Display display = response.display();
        assertThat(display.age()).isEqualTo(28);
        assertThat(display.householdIncomeGuide()).isEqualTo(256);
        assertThat(display.region()).isEqualTo("서울");
        assertThat(display.preferentialConditions()).containsExactly("급여이체 가능");
        assertThat(display.transactionHistory().firstTransactionBanks()).containsExactly("신한은행");
        assertThat(display.transactionHistory().redepositBanks()).containsExactly("우리은행");
    }

    @Test
    void getProfile_중위소득_데이터가_없으면_가구소득가이드는_null이다() {
        UserProfile profile = new UserProfile(mockUser());
        profile.apply(new UserProfileRequestDto(
                LocalDate.of(1995, 1, 1), 3000L, 1, 100, null,
                null, null, null, null, null, null, null));

        when(userProfileRepository.findByUserId(USER_ID)).thenReturn(Optional.of(profile));
        when(medianIncomeService.getMedianIncomesDto(anyInt(), eq(1))).thenReturn(
                MedianIncomesDto.builder().year(2026).householdSize(1)
                        .p60(0).p80(0).p100(0).p120(0).p150(0).p180(0).build());

        UserProfileResponseDto response = userProfileService.getProfile(USER_ID);

        assertThat(response.display().householdIncomeGuide()).isNull();
    }

    // ── helpers ──

    private UserProfileRequestDto sampleRequest() {
        return new UserProfileRequestDto(
                LocalDate.of(1996, 5, 20), 3600L, 1, 100, 24,
                true, true, true, 500_000L,
                List.of("SHINHAN"), List.of("WOORI"),
                List.of(100L, 200L));
    }

    private User mockUser() {
        return User.builder()
                .name("tester").email("t@e.com").provider("google").providerId("pid")
                .userRole(UserRole.RECOMMENDATION).build();
    }

    private CategoryOption categoryOption(String code, String value) {
        CategoryOption option = BeanUtils.instantiateClass(CategoryOption.class);
        ReflectionTestUtils.setField(option, "code", code);
        ReflectionTestUtils.setField(option, "value", value);
        return option;
    }

    private Provider provider(String code, String name) {
        Provider provider = BeanUtils.instantiateClass(Provider.class);
        ReflectionTestUtils.setField(provider, "code", code);
        ReflectionTestUtils.setField(provider, "name", name);
        return provider;
    }
}
