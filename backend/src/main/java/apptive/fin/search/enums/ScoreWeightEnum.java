package apptive.fin.search.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ScoreWeightEnum {

    // === V2 가중치 (PRD 개정: 3축 통합) ===
    // 정부/은행 공통: 핵심혜택50, 저축기간30, 납입한도20
    // 신분특화, 은행조건 축 제거

    // 정부 상품 배점 (V2)
    GOV_BENEFITS_V2 ("benefits", 50.0, true, 2),
    GOV_PERIOD_V2   ("period",   30.0, true, 2),
    GOV_DEPOSIT_V2  ("deposit",  20.0, true, 2),
    GOV_IDENTITY_V2 ("identity",  0.0, true, 2),  // 제거됨
    GOV_BANK_COND_V2("bankCond",  0.0, true, 2),  // 제거됨

    // 시중은행 상품 배점 (V2)
    BANK_BENEFITS_V2 ("benefits", 50.0, false, 2),
    BANK_PERIOD_V2   ("period",   30.0, false, 2),
    BANK_DEPOSIT_V2  ("deposit",  20.0, false, 2),
    BANK_IDENTITY_V2 ("identity",  0.0, false, 2),  // 제거됨
    BANK_BANK_COND_V2("bankCond",  0.0, false, 2),  // 제거됨

    // === V1 가중치 (레거시 호환) ===
    // 정부 상품 배점
    GOV_BENEFITS ("benefits", 40.0, true, 1),
    GOV_PERIOD   ("period",   22.0, true, 1),
    GOV_IDENTITY ("identity", 20.0, true, 1),
    GOV_DEPOSIT  ("deposit",  18.0, true, 1),
    GOV_BANK_COND("bankCond",  0.0, true, 1),

    // 시중은행 상품 배점
    BANK_BANK_COND ("bankCond", 40.0, false, 1),
    BANK_BENEFITS  ("benefits", 20.0, false, 1),
    BANK_PERIOD    ("period",   20.0, false, 1),
    BANK_DEPOSIT   ("deposit",  15.0, false, 1),
    BANK_IDENTITY  ("identity",  5.0, false, 1);

    private final String key;
    private final double weight;
    private final boolean isGov;
    private final int version;

    // 레거시 호환용 생성자
    ScoreWeightEnum(String key, double weight, boolean isGov) {
        this(key, weight, isGov, 1);
    }

    // 정부 or 은행 기본 배점 Map 반환 (V1 레거시)
    public static Map<String, Double> baseWeights(boolean isGov) {
        return baseWeights(isGov, 1);
    }

    // 버전별 기본 배점 Map 반환
    public static Map<String, Double> baseWeights(boolean isGov, int version) {
        return Arrays.stream(values())
                .filter(e -> e.isGov == isGov && e.version == version)
                .collect(Collectors.toMap(
                        ScoreWeightEnum::getKey,
                        ScoreWeightEnum::getWeight
                ));
    }

    // V2 가중치 (PRD 개정)
    public static Map<String, Double> baseWeightsV2(boolean isGov) {
        return baseWeights(isGov, 2);
    }

}
