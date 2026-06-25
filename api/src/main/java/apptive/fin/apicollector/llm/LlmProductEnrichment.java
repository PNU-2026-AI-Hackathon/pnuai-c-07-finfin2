package apptive.fin.apicollector.llm;

import java.math.BigDecimal;
import java.util.List;

public record LlmProductEnrichment(
        String summaryContent,
        List<String> keywords,
        Long minMonthlyLimit,
        Long maxMonthlyLimit,
        Integer minAge,
        Integer maxAge,
        Long earnMaxAmt,
        Integer earnPercent,
        Boolean requiresHomeless,
        Boolean requiresHouseholder,
        BigDecimal govContributionRate
) {
    public LlmProductEnrichment {
        keywords = keywords == null ? List.of() : List.copyOf(keywords);
    }
}
