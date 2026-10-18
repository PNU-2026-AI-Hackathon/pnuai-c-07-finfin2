package apptive.fin.apicollector.normalize.extractor;

import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FssRequiredKeywordExtractor {

    public List<RequiredKeywordDraft> extract(String joinMember, String etcNote) {
        String text = ((joinMember == null ? "" : joinMember) + " " + (etcNote == null ? "" : etcNote)).trim();
        if (text.isBlank()) {
            return List.of();
        }

        List<RequiredKeywordDraft> result = new ArrayList<>();
        if (containsAny(text, "중소기업", "중소기업근로자", "중기근로자", "중소기업 재직")) {
            result.add(require(KeywordValueEnum.STATUS_SME_WORKER));
        }
        if (containsAny(text, "군인", "장병", "군 복무", "군복무", "병역")) {
            result.add(require(KeywordValueEnum.STATUS_MILITARY));
        }
        if (containsAny(text, "무직", "미취업", "구직자")) {
            result.add(require(KeywordValueEnum.STATUS_UNEMPLOYED));
        }
        return List.copyOf(result);
    }

    private RequiredKeywordDraft require(KeywordValueEnum keyword) {
        return RequiredKeywordDraft.builder()
                .keywordCode(keyword)
                .effect(RequiredKeywordEffect.REQUIRE)
                .confidence(ExtractionConfidence.HIGH)
                .build();
    }

    private boolean containsAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
