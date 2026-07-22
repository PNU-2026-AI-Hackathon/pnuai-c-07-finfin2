package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.util.JsonNodes;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProductNormalizer {

    protected ProductDraft extractKeywords(
            KeywordExtractor extractor,
            ProductDraft draft

    ) {
        List<ProductPropertyDraft> productPropertyDrafts = new ArrayList<>();
        for (ProductPropertyDraft property : draft.properties()) {
            List<KeywordValueEnum> keywords = extractor.extract(draft, property);

            productPropertyDrafts.add(
                    property
                            .toBuilder()
                            .keywords(keywords)
                            .build()
            );
        }
        return draft.toBuilder()
                .properties(productPropertyDrafts)
                .build();
    }

    protected String firstText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            String value = JsonNodes.text(node, fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    protected String joinContent(JsonNode node, String... fieldNames) {
        List<String> parts = new ArrayList<>();
        for (String fieldName : fieldNames) {
            String value = JsonNodes.text(node, fieldName);
            if (value != null) {
                parts.add(value);
            }
        }
        return parts.isEmpty() ? null : String.join("\n\n", parts);
    }

    protected String collapseWhitespace(String value) {
        if (value == null) return null;
        String collapsed = value.replaceAll("\\s+", " ").trim();
        return collapsed.isEmpty() ? null : collapsed;
    }
}
