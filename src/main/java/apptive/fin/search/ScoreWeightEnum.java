package apptive.fin.search;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ScoreWeightEnum {

    // 정부 상품 배점
    GOV_BENEFITS ("benefits", 40.0,true),
    GOV_PERIOD ("period", 20.0, true),
    GOV_IDENTITY("identity",20.0, true),
    GOV_DEPOSIT ("deposit",12.0, true),
    GOV_BANK_COND("bankCond", 8.0, true),

    // 시중은행 상품 배점
    BANK_BANK_COND ("bankCond", 40.0, false),
    BANK_BENEFITS ("benefits", 20.0, false),
    BANK_PERIOD   ("period",   20.0, false),
    BANK_DEPOSIT  ("deposit",  15.0, false),
    BANK_IDENTITY ("identity",  5.0, false);

    private final String key;
    private final double weight;
    private final boolean isGov;

    // 정부 or 은행 기본 배점 Map 반환
    public static Map<String, Double> baseWeights(boolean isGov) {
        return Arrays.stream(values())
                .filter(e -> e.isGov == isGov)
                .collect(Collectors.toMap(
                        ScoreWeightEnum::getKey,
                        ScoreWeightEnum::getWeight
                ));
    }

}
