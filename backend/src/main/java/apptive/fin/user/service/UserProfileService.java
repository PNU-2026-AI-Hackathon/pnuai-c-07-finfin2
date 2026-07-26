package apptive.fin.user.service;

import apptive.fin.category.entity.CategoryOption;
import apptive.fin.category.repository.CategoryOptionRepository;
import apptive.fin.global.error.BusinessException;
import apptive.fin.global.util.AgeUtil;
import apptive.fin.provider.entity.Provider;
import apptive.fin.provider.repository.ProviderRepository;
import apptive.fin.search.dto.MedianIncomesDto;
import apptive.fin.search.service.MedianIncomeService;
import apptive.fin.user.UserErrorCode;
import apptive.fin.user.dto.UserProfileRequestDto;
import apptive.fin.user.dto.UserProfileResponseDto;
import apptive.fin.user.entity.User;
import apptive.fin.user.entity.UserProfile;
import apptive.fin.user.repository.UserProfileRepository;
import apptive.fin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private static final String REGION_CODE_PREFIX = "REGION_";
    private static final String PREFERENTIAL_OPTION_CODE_PREFIX = "BANK_";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final CategoryOptionRepository categoryOptionRepository;
    private final ProviderRepository providerRepository;
    private final MedianIncomeService medianIncomeService;

    /**
     * 정보 입력값 저장(upsert). 저장에 대한 법적 근거(개인정보 수집·이용 동의)는 회원가입 필수 약관으로
     * 확보되며, 엔드포인트 접근 권한은 컨트롤러 권한 설정(추후 RECOMMENDATION 역할)에서 통제한다.
     */
    @Transactional
    public void upsert(Long userId, UserProfileRequestDto request) {
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
        if (profile == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
            profile = new UserProfile(user);
        }

        profile.apply(request);          // 생성/수정 공통 매핑 단일 지점
        userProfileRepository.save(profile);
    }

    /** 저장된 프로필 삭제(사용자 주도). 저장된 프로필이 없으면 no-op. */
    @Transactional
    public void deleteProfile(Long userId) {
        userProfileRepository.deleteByUserId(userId);
    }

    /**
     * 프로필 조회. 저장값(프리필용) + 서버 계산 표시값(만 나이·가구소득 가이드·라벨/은행명 해소)을 반환.
     * 저장된 프로필이 없으면 hasProfile=false 응답.
     */
    public UserProfileResponseDto getProfile(Long userId) {
        UserProfile p = userProfileRepository.findByUserId(userId).orElse(null);
        if (p == null) {
            return UserProfileResponseDto.notFound();
        }

        List<CategoryOption> options = p.getSelectedOptionIds() == null
                ? List.of()
                : categoryOptionRepository.findAllById(p.getSelectedOptionIds());

        String region = options.stream()
                .filter(o -> o.getCode() != null && o.getCode().startsWith(REGION_CODE_PREFIX))
                .map(CategoryOption::getValue)
                .findFirst()
                .orElse(null);

        List<String> preferentialConditions = options.stream()
                .filter(o -> o.getCode() != null && o.getCode().startsWith(PREFERENTIAL_OPTION_CODE_PREFIX))
                .map(CategoryOption::getValue)
                .toList();

        // 주거래 은행(mainBanks)은 정책상 표시 제외 — 첫거래/재예치만 해소.
        Map<String, String> bankNameByCode = resolveBankNames(
                p.getNeverUsedBanks(), p.getMaturedSavingBanks());

        UserProfileResponseDto.Display display = new UserProfileResponseDto.Display(
                AgeUtil.age(p.getBirthdate()),
                householdIncomeGuide(p.getHouseholdSize(), p.getHouseholdIncomePercent()),
                region,
                preferentialConditions,
                new UserProfileResponseDto.TransactionHistory(
                        toBankNames(p.getNeverUsedBanks(), bankNameByCode),
                        toBankNames(p.getMaturedSavingBanks(), bankNameByCode)
                )
        );

        return new UserProfileResponseDto(
                true,
                p.getBirthdate(),
                p.getAnnualIncome(),
                p.getHouseholdSize(),
                p.getHouseholdIncomePercent(),
                p.getTenureMonths(),
                p.getIsFirstJob(),
                p.getIsHomeless(),
                p.getIsHouseholder(),
                p.getMonthlySavingsGoal(),
                p.getMainBanks(),
                p.getNeverUsedBanks(),
                p.getMaturedSavingBanks(),
                p.getSelectedOptionIds(),
                display
        );
    }

    /** 가구원 수 × 소득 구간(%) → 해당 중위소득 금액(만원). 조회 시점 연도(Asia/Seoul) 기준. */
    private Integer householdIncomeGuide(Integer householdSize, Integer percent) {
        if (householdSize == null || percent == null) {
            return null;
        }
        int currentYear = Year.now(KST).getValue();
        MedianIncomesDto median = medianIncomeService.getMedianIncomesDto(currentYear, householdSize);
        if (median.isEmpty()) {
            return null;   // 해당 연도·가구원 수 중위소득 데이터 없음 → "0만원" 오표기 대신 미산출(null)
        }
        return switch (percent) {
            case 60 -> median.p60();
            case 80 -> median.p80();
            case 100 -> median.p100();
            case 120 -> median.p120();
            case 150 -> median.p150();
            case 180 -> median.p180();
            default -> null;
        };
    }

    /** 표시 대상 은행 코드 리스트(첫거래·재예치)를 한 번에 이름으로 해소하기 위한 code→name 맵. */
    private Map<String, String> resolveBankNames(List<String> neverUsed, List<String> matured) {
        List<String> allCodes = Stream.of(neverUsed, matured)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .toList();
        if (allCodes.isEmpty()) {
            return Map.of();
        }
        return providerRepository.findByCodeIn(allCodes).stream()
                .collect(Collectors.toMap(Provider::getCode, Provider::getName, (a, b) -> a));
    }

    /** 은행 코드 리스트를 이름 리스트로 변환. 매칭 실패 코드는 코드 그대로 유지(무단 누락 방지). */
    private List<String> toBankNames(List<String> codes, Map<String, String> nameByCode) {
        if (codes == null) {
            return null;
        }
        return codes.stream()
                .map(code -> nameByCode.getOrDefault(code, code))
                .toList();
    }
}
