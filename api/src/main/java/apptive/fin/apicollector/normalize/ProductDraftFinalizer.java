package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * 정규화와 보강이 끝난 draft에 키워드 소유권과 파생 태그를 최종 적용한다.
 */
@Component
public class ProductDraftFinalizer {

    private static final int EASY_CONDITION_MAX_COUNT = 3;

    public ProductDraft apply(ProductDraft draft) {
        List<ProductPropertyDraft> properties = new ArrayList<>();
        for (ProductPropertyDraft property : draft.properties()) {
            properties.add(finalizeProperty(property));
        }
        return draft.toBuilder()
                .properties(properties)
                .build();
    }

    private ProductPropertyDraft finalizeProperty(ProductPropertyDraft property) {
        Set<KeywordValueEnum> keywords = EnumSet.noneOf(KeywordValueEnum.class);
        property.keywords().stream()
                .filter(keyword -> !keyword.isPreferentialRate())
                .filter(keyword -> !keyword.isRequired())
                .filter(keyword -> keyword != KeywordValueEnum.BENEFIT_EASY_CONDITION)
                .forEach(keywords::add);

        int preferentialConditionCount = property.preferentialRates().size();
        if (preferentialConditionCount >= 1
                && preferentialConditionCount <= EASY_CONDITION_MAX_COUNT) {
            keywords.add(KeywordValueEnum.BENEFIT_EASY_CONDITION);
        }

        return property.toBuilder()
                .keywords(List.copyOf(keywords))
                .build();
    }
}
