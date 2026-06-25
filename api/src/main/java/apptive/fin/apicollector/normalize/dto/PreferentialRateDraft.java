package apptive.fin.apicollector.normalize.dto;

import apptive.fin.apicollector.product.KeywordValueEnum;
import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record PreferentialRateDraft(
        KeywordValueEnum keywordCode,
        BigDecimal rate,
        String description,
        Integer minAge,
        Integer maxAge
) {
}
