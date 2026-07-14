package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public interface KeywordRecognizer {
    Logger log = LoggerFactory.getLogger(KeywordRecognizer.class);
    ConcurrentHashMap<String, Optional<Pattern>> PATTERN_CACHE = new ConcurrentHashMap<>();

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

        return PATTERN_CACHE.computeIfAbsent(token, key -> {
            try {
                return Optional.of(Pattern.compile(key));
            }
            catch (PatternSyntaxException e) {
                log.warn("Invalid keyword regex token: {}", key, e);
                return Optional.empty();
            }
        }).map(pattern -> pattern.matcher(value).find())
                .orElse(false);
    }

}
