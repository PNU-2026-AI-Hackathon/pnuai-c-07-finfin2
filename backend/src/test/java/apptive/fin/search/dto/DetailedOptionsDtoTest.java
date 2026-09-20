package apptive.fin.search.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DetailedOptionsDtoTest {

    // birthdate만 지정하고 나머지는 null인 상세옵션을 만든다.
    private DetailedOptionsDto withBirthdate(LocalDate birthdate) {
        return new DetailedOptionsDto(
                birthdate, null, null, null, null, null, null, null, null, null
        );
    }

    @Test
    void 생일이_없으면_나이는_null() {
        assertThat(withBirthdate(null).age(LocalDate.of(2026, 7, 12))).isNull();
    }

    @Test
    void 생일_당일이면_만나이는_0() {
        LocalDate birthdate = LocalDate.of(2000, 7, 12);
        assertThat(withBirthdate(birthdate).age(birthdate)).isZero();
    }

    @Test
    void 생일_당일에는_만나이가_증가한다() {
        // 2000-01-01 출생, 2025-01-01(25번째 생일) → 25
        assertThat(withBirthdate(LocalDate.of(2000, 1, 1)).age(LocalDate.of(2025, 1, 1)))
                .isEqualTo(25);
    }

    @Test
    void 생일_전날이면_만나이가_아직_1_적다() {
        // 2000-07-12 출생, 2025-07-11(생일 하루 전) → 24
        assertThat(withBirthdate(LocalDate.of(2000, 7, 12)).age(LocalDate.of(2025, 7, 11)))
                .isEqualTo(24);
    }

    // ===== 윤년(2000-02-29 출생) 경계 =====

    @Test
    void 윤년생일_비윤년_2월28일이면_아직_생일_전으로_계산한다() {
        // 2020-02-28은 2020-02-29 이전 → 19세
        assertThat(withBirthdate(LocalDate.of(2000, 2, 29)).age(LocalDate.of(2020, 2, 28)))
                .isEqualTo(19);
    }

    @Test
    void 윤년생일_윤년_2월29일이면_생일_당일로_계산한다() {
        // 2020은 윤년이라 2월 29일 존재 → 20세
        assertThat(withBirthdate(LocalDate.of(2000, 2, 29)).age(LocalDate.of(2020, 2, 29)))
                .isEqualTo(20);
    }

    @Test
    void 윤년생일_비윤년_3월1일이면_생일이_지난_것으로_계산한다() {
        // 2021은 비윤년, 3월 1일은 생일(2월 말)이 지난 시점 → 21세
        assertThat(withBirthdate(LocalDate.of(2000, 2, 29)).age(LocalDate.of(2021, 3, 1)))
                .isEqualTo(21);
    }
}
