package apptive.fin.global.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

/**
 * 만 나이 계산 유틸. 기준일 = 조회일(Asia/Seoul).
 * (기존 EligibilityFilterService / RateCalculatorService의 Period.between 관용구를 공용화)
 */
public final class AgeUtil {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private AgeUtil() {
    }

    public static Integer koreanAge(LocalDate birthdate) {
        if (birthdate == null) {
            return null;
        }
        return Period.between(birthdate, LocalDate.now(KST)).getYears();
    }
}
