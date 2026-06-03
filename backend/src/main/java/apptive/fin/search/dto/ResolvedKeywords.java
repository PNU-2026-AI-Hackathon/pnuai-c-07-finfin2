package apptive.fin.search.dto;

import apptive.fin.search.KeywordValueEnum;

import java.util.List;

public record ResolvedKeywords(
        List<KeywordValueEnum> regions,
        List<KeywordValueEnum> identities,
        KeywordValueEnum savingPeriod,
        List<KeywordValueEnum> coreBenefits,
        List<KeywordValueEnum> bankConditions
){}