package apptive.fin.user.entity;

import apptive.fin.global.converter.LongListJsonConverter;
import apptive.fin.global.converter.StringListJsonConverter;
import apptive.fin.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.List;

/**
 * 정보 입력(Y3-1) 저장값 스냅샷. User와 1:1. 마이페이지(Y5-2) 조회·프리필의 원천.
 * 리스트/선택값은 JSON(TEXT) 컬럼으로 저장(개별 원소 질의 없음).
 */
@Entity
@Table(name = "user_profiles", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_profiles_user", columnNames = {"user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "annual_income")
    private Long annualIncome;

    @Column(name = "household_size")
    private Integer householdSize;

    @Column(name = "household_income_percent")
    private Integer householdIncomePercent;

    @Column(name = "tenure_months")
    private Integer tenureMonths;

    @Column(name = "is_first_job")
    private Boolean isFirstJob;

    @Column(name = "is_homeless")
    private Boolean isHomeless;

    @Column(name = "is_householder")
    private Boolean isHouseholder;

    @Column(name = "monthly_savings_goal")
    private Long monthlySavingsGoal;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "main_banks", columnDefinition = "TEXT")
    private List<String> mainBanks;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "never_used_banks", columnDefinition = "TEXT")
    private List<String> neverUsedBanks;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "matured_saving_banks", columnDefinition = "TEXT")
    private List<String> maturedSavingBanks;

    @Convert(converter = LongListJsonConverter.class)
    @Column(name = "selected_option_ids", columnDefinition = "TEXT")
    private List<Long> selectedOptionIds;

    @Builder
    public UserProfile(User user, LocalDate birthdate, Long annualIncome, Integer householdSize,
                       Integer householdIncomePercent, Integer tenureMonths, Boolean isFirstJob,
                       Boolean isHomeless, Boolean isHouseholder, Long monthlySavingsGoal,
                       List<String> mainBanks, List<String> neverUsedBanks, List<String> maturedSavingBanks,
                       List<Long> selectedOptionIds) {
        this.user = user;
        this.birthdate = birthdate;
        this.annualIncome = annualIncome;
        this.householdSize = householdSize;
        this.householdIncomePercent = householdIncomePercent;
        this.tenureMonths = tenureMonths;
        this.isFirstJob = isFirstJob;
        this.isHomeless = isHomeless;
        this.isHouseholder = isHouseholder;
        this.monthlySavingsGoal = monthlySavingsGoal;
        this.mainBanks = mainBanks;
        this.neverUsedBanks = neverUsedBanks;
        this.maturedSavingBanks = maturedSavingBanks;
        this.selectedOptionIds = selectedOptionIds;
    }

    /** upsert 시 기존 행 전체 갱신(정보 입력 화면 저장은 전량 덮어쓰기). */
    public void update(LocalDate birthdate, Long annualIncome, Integer householdSize,
                       Integer householdIncomePercent, Integer tenureMonths, Boolean isFirstJob,
                       Boolean isHomeless, Boolean isHouseholder, Long monthlySavingsGoal,
                       List<String> mainBanks, List<String> neverUsedBanks, List<String> maturedSavingBanks,
                       List<Long> selectedOptionIds) {
        this.birthdate = birthdate;
        this.annualIncome = annualIncome;
        this.householdSize = householdSize;
        this.householdIncomePercent = householdIncomePercent;
        this.tenureMonths = tenureMonths;
        this.isFirstJob = isFirstJob;
        this.isHomeless = isHomeless;
        this.isHouseholder = isHouseholder;
        this.monthlySavingsGoal = monthlySavingsGoal;
        this.mainBanks = mainBanks;
        this.neverUsedBanks = neverUsedBanks;
        this.maturedSavingBanks = maturedSavingBanks;
        this.selectedOptionIds = selectedOptionIds;
    }
}
