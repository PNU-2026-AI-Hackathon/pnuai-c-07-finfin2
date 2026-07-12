package apptive.fin.user.service;

import apptive.fin.global.error.BusinessException;
import apptive.fin.term.service.TermService;
import apptive.fin.user.UserErrorCode;
import apptive.fin.user.dto.UserProfileRequestDto;
import apptive.fin.user.entity.User;
import apptive.fin.user.entity.UserProfile;
import apptive.fin.user.repository.UserProfileRepository;
import apptive.fin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    /** 개인정보 저장 동의 약관 code (data.sql seed). */
    public static final String STORAGE_CONSENT_CODE = "PRIVACY_STORAGE_CONSENT";

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final TermService termService;

    /**
     * 정보 입력값 저장(upsert). 저장 전 개인정보 저장 동의 여부를 확인하고,
     * 미동의면 저장하지 않고 403을 던진다. 한 번 동의하면 이후 저장은 통과.
     */
    @Transactional
    public void upsert(Long userId, UserProfileRequestDto request) {
        if (!termService.hasAgreed(userId, STORAGE_CONSENT_CODE)) {
            throw new BusinessException(UserErrorCode.PROFILE_CONSENT_REQUIRED);
        }

        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);

        if (profile == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
            profile = UserProfile.builder()
                    .user(user)
                    .birthdate(request.birthdate())
                    .annualIncome(request.annualIncome())
                    .householdSize(request.householdSize())
                    .householdIncomePercent(request.householdIncomePercent())
                    .tenureMonths(request.tenureMonths())
                    .isFirstJob(request.isFirstJob())
                    .isHomeless(request.isHomeless())
                    .isHouseholder(request.isHouseholder())
                    .monthlySavingsGoal(request.monthlySavingsGoal())
                    .mainBanks(request.mainBanks())
                    .neverUsedBanks(request.neverUsedBanks())
                    .maturedSavingBanks(request.maturedSavingBanks())
                    .selectedOptionIds(request.selectedOptionIds())
                    .build();
            userProfileRepository.save(profile);
        } else {
            profile.update(
                    request.birthdate(),
                    request.annualIncome(),
                    request.householdSize(),
                    request.householdIncomePercent(),
                    request.tenureMonths(),
                    request.isFirstJob(),
                    request.isHomeless(),
                    request.isHouseholder(),
                    request.monthlySavingsGoal(),
                    request.mainBanks(),
                    request.neverUsedBanks(),
                    request.maturedSavingBanks(),
                    request.selectedOptionIds()
            );
        }
    }
}
