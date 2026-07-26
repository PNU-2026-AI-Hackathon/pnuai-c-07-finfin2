package apptive.fin.search.dto;

import apptive.fin.search.KeywordValueEnum;

import java.util.List;

public record ResolvedKeywords(
        List<KeywordValueEnum> regions,
        List<KeywordValueEnum> identities,
        KeywordValueEnum savingPeriod,
        List<KeywordValueEnum> coreBenefits,
        List<KeywordValueEnum> bankConditions
){

    public static ResolvedKeywords emptyKeywords() {
        return new ResolvedKeywords(List.of(), List.of(), null, List.of(), List.of());
    }
}