package apptive.fin.user.entity;

import apptive.fin.global.converter.LongListJsonConverter;
import apptive.fin.global.converter.StringListJsonConverter;
import apptive.fin.global.entity.BaseTimeEntity;
import apptive.fin.user.dto.UserProfileRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    @Column(name = "never_used_banks", columnDefinition = "TEXT")
    private List<String> neverUsedBanks;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "matured_saving_banks", columnDefinition = "TEXT")
    private List<String> maturedSavingBanks;

    @Convert(converter = LongListJsonConverter.class)
    @Column(name = "selected_option_ids", columnDefinition = "TEXT")
    private List<Long> selectedOptionIds;

    public UserProfile(User user) {
        this.user = user;
    }

    /**
     * 정보 입력값 전량 덮어쓰기(생성/수정 공통). 필드 매핑 단일 지점 — 필드 추가 시 여기 한 곳만 갱신.
     */
    public void apply(UserProfileRequestDto request) {
        this.birthdate = request.birthdate();
        this.annualIncome = request.annualIncome();
        this.householdSize = request.householdSize();
        this.householdIncomePercent = request.householdIncomePercent();
        this.tenureMonths = request.tenureMonths();
        this.isFirstJob = request.isFirstJob();
        this.isHomeless = request.isHomeless();
        this.isHouseholder = request.isHouseholder();
        this.monthlySavingsGoal = request.monthlySavingsGoal();
        this.neverUsedBanks = request.neverUsedBanks();
        this.maturedSavingBanks = request.maturedSavingBanks();
        this.selectedOptionIds = request.selectedOptionIds();
    }
}
