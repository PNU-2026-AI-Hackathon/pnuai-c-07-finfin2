package apptive.fin.global.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class AgeUtilTest {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Test
    void 생년월일이_null이면_null을_반환한다() {
        assertThat(AgeUtil.koreanAge(null)).isNull();
    }

    @Test
    void 생일이_지났으면_만_나이가_정확히_계산된다() {
        LocalDate today = LocalDate.now(KST);
        assertThat(AgeUtil.koreanAge(today.minusYears(30))).isEqualTo(30);
    }

    @Test
    void 올해_생일이_아직_안_지났으면_한_살_적게_계산된다() {
        LocalDate today = LocalDate.now(KST);
        // 30년 전 + 1일 = 생일이 내일 → 아직 만 29세
        assertThat(AgeUtil.koreanAge(today.minusYears(30).plusDays(1))).isEqualTo(29);
    }
}
