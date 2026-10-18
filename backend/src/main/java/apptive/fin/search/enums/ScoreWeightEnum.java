package apptive.fin.search.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ScoreWeightEnum {

    // === V2 가중치 (PRD 개정: 4축 - 고특성 30점, 균등 20점) ===
    // 정부: 핵심혜택30 + 현재신분30 + 납입한도20 + 저축기간20 = 100
    // 은행: 은행거래30 + 핵심혜택30 + 납입한도20 + 저축기간20 = 100

    // 정부 상품 배점 (V2)
    GOV_BENEFITS_V2 ("benefits", 30.0, true, 2),   // 고특성: #정부기여금, #비과세
    GOV_IDENTITY_V2 ("identity", 30.0, true, 2),   // 고특성: 현재 신분 (특화/포함)
    GOV_DEPOSIT_V2  ("deposit",  20.0, true, 2),   // 균등: 납입 한도
    GOV_PERIOD_V2   ("period",   20.0, true, 2),   // 균등: 저축 기간
    GOV_BANK_COND_V2("bankCond",  0.0, true, 2),   // 정부상품에 은행거래 미적용

    // 시중은행 상품 배점 (V2)
    BANK_BANK_COND_V2("bankCond", 30.0, false, 2), // 고특성: 은행 거래 우대조건
    BANK_BENEFITS_V2 ("benefits", 30.0, false, 2), // 고특성: #최고이율_중심, #우대조건_간편
    BANK_DEPOSIT_V2  ("deposit",  20.0, false, 2), // 균등: 납입 한도
    BANK_PERIOD_V2   ("period",   20.0, false, 2), // 균등: 저축 기간
    BANK_IDENTITY_V2 ("identity",  0.0, false, 2), // 은행상품에 신분특화 미적용

    // === TOP3 균등 배점 (PRD: 3축 균등 33/33/34) ===
    // 정부/은행 공통: 핵심혜택33 + 납입한도33 + 저축기간34 = 100
    // 신분특화, 은행거래 축 제외
    TOP3_BENEFITS("benefits", 33.0, true, 3),
    TOP3_DEPOSIT ("deposit",  33.0, true, 3),
    TOP3_PERIOD  ("period",   34.0, true, 3);

    private final String key;
    private final double weight;
    private final boolean isGov;
    private final int version;

    // 버전별 기본 배점 Map 반환
    private static Map<String, Double> baseWeights(boolean isGov, int version) {
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

    // TOP3 균등 가중치 (정부/은행 공통)
    public static Map<String, Double> top3Weights() {
        return Arrays.stream(values())
                .filter(e -> e.version == 3)
                .collect(Collectors.toMap(
                        ScoreWeightEnum::getKey,
                        ScoreWeightEnum::getWeight
                ));
    }

}
