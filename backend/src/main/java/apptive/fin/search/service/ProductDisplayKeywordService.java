package apptive.fin.search.service;

import apptive.fin.search.entity.Product;
import apptive.fin.search.entity.ProductProperty;
import apptive.fin.search.enums.KeywordValueEnum;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductDisplayKeywordService {

    public List<KeywordValueEnum> resolve(
            Product product,
            List<ProductProperty> properties,
            Double bankMaxInterestThreshold
    ) {
        Set<KeywordValueEnum> keywords = new LinkedHashSet<>();
        for (ProductProperty property : properties) {
            for (KeywordValueEnum code : property.keywordCodes()) {
                if (code != null && code != KeywordValueEnum.BENEFIT_MAX_INTEREST) {
                    keywords.add(code);
                }
            }
        }

        if (product.isBank()
                && BankMaxInterestPolicy.anyJoinableQualifies(
                        properties,
                        bankMaxInterestThreshold
                )) {
            keywords.add(KeywordValueEnum.BENEFIT_MAX_INTEREST);
        }
        return List.copyOf(keywords);
    }
}
