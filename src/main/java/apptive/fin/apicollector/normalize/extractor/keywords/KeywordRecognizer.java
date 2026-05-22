package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public interface KeywordRecognizer {
    List<KeywordValueEnum> recognize(ProductDraft productDraft, ProductPropertyDraft propertyDraft);
    default void addIfContains(
            Set<KeywordValueEnum> keywords,
            String value,
            KeywordValueEnum keyword,
            String... tokens
    ) {
        if (value == null) {
            return;
        }

        for (String token : tokens) {
            if (matchesToken(value, token)) {
                keywords.add(keyword);
                return;
            }
        }
    }
    default boolean matchesToken(String value, String token) {
        if (value.contains(token)) {
            return true;
        }

        try {
            return Pattern.compile(token).matcher(value).find();
        }
        catch (PatternSyntaxException e) {
            return false;
        }
    }

}
