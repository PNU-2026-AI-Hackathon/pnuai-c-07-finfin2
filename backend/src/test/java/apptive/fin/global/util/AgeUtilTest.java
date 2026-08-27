package apptive.fin.global.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class AgeUtilTest {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Test
    void 생년월일이_null이면_null을_반환한다() {
        assertThat(AgeUtil.age(null)).isNull();
    }

    @Test
    void 생일이_지났으면_만_나이가_정확히_계산된다() {
        LocalDate today = LocalDate.now(KST);
        assertThat(AgeUtil.age(today.minusYears(30))).isEqualTo(30);
    }

    @Test
    void 올해_생일이_아직_안_지났으면_한_살_적게_계산된다() {
        LocalDate today = LocalDate.now(KST);
        // 30년 전 + 1일 = 생일이 내일 → 아직 만 29세
        assertThat(AgeUtil.age(today.minusYears(30).plusDays(1))).isEqualTo(29);
    }

    @Test
    void calculatesAgeAtExplicitDate() {
        LocalDate birthdate = LocalDate.of(2000, 7, 12);

        assertThat(AgeUtil.age(birthdate, LocalDate.of(2025, 7, 11))).isEqualTo(24);
        assertThat(AgeUtil.age(birthdate, LocalDate.of(2025, 7, 12))).isEqualTo(25);
    }

    @Test
    void calculatesLeapDayBirthdayAtExplicitDate() {
        LocalDate birthdate = LocalDate.of(2000, 2, 29);

        assertThat(AgeUtil.age(birthdate, LocalDate.of(2020, 2, 28))).isEqualTo(19);
        assertThat(AgeUtil.age(birthdate, LocalDate.of(2020, 2, 29))).isEqualTo(20);
        assertThat(AgeUtil.age(birthdate, LocalDate.of(2021, 3, 1))).isEqualTo(21);
    }
}
