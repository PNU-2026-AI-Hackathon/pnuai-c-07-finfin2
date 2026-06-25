package apptive.fin.apicollector.normalize.dto;

import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import lombok.Builder;

@Builder(toBuilder = true)
public record RequiredKeywordDraft(
        KeywordValueEnum keywordCode,
        RequiredKeywordEffect effect,
        ExtractionConfidence confidence
) {
    public RequiredKeywordDraft {
        effect = effect == null ? RequiredKeywordEffect.REQUIRE : effect;
        confidence = confidence == null ? ExtractionConfidence.HIGH : confidence;
    }
}
