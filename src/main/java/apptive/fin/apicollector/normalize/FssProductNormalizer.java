package apptive.fin.apicollector.normalize;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FssProductNormalizer extends AbstractProductNormalizer implements ProductNormalizer {

    private final ObjectMapper objectMapper;
    private final CollectorProperties properties;

    @Override
    public Source source() {
        return Source.FSS;
    }

    @Override
    public ProductDraft normalize(ProductRaw rawProduct) {
        JsonNode raw = read(rawProduct);
        JsonNode base = raw.path("base");
        List<ProductOptionDraft> options = options(raw.path("options"));
        String content = joinContent(base, "join_way", "mtrt_int", "spcl_cnd", "join_member", "etc_note");
        String productName = firstText(base, "fin_prdt_nm");

        return ProductDraft.builder()
                .rawId(rawProduct.getId())
                .rawSource(rawProduct.getSource())
                .normalizerVersion(properties.normalizerVersion())
                .classification(ProductClassification.FINANCIAL_PRODUCT)
                .saveProduct(true)
                .sourceCode(Source.FSS.name())
                .providerCode(firstText(base, "fin_co_no", "kor_co_nm"))
                .providerName(firstText(base, "kor_co_nm", "fin_co_no"))
                .type(ProductType.BANK)
                .productCode(rawProduct.getExternalId())
                .productName(required(productName, rawProduct))
                .content(content)
                .baseRate(max(options, ProductOptionDraft::intrRate))
                .maxRate(max(options, ProductOptionDraft::intrRate2))
                .maxMonthlyLimit(longValue(base, "max_limit"))
                .minTenureMonths(maxSaveTerm(options))
                .requiresHomeless(false)
                .requiresHouseholder(false)
                .options(options)
                .keywords(keywordsFromText(
                        text(raw, "productType"),
                        text(raw, "financialGroupName"),
                        productName,
                        content
                ))
                .build();
    }

    private JsonNode read(ProductRaw rawProduct) {
        try {
            return objectMapper.readTree(rawProduct.getRawJson());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse FSS raw JSON. rawId=" + rawProduct.getId(), e);
        }
    }

    private List<ProductOptionDraft> options(JsonNode optionsNode) {
        if (optionsNode == null || !optionsNode.isArray()) {
            return List.of();
        }

        List<ProductOptionDraft> options = new ArrayList<>();
        for (JsonNode option : optionsNode) {
            options.add(new ProductOptionDraft(
                    firstText(option, "intr_rate_type"),
                    firstText(option, "intr_rate_type_nm"),
                    integer(option, "save_trm"),
                    decimal(option, "intr_rate"),
                    decimal(option, "intr_rate2")
            ));
        }
        return options;
    }

    private BigDecimal max(
            List<ProductOptionDraft> options,
            java.util.function.Function<ProductOptionDraft, BigDecimal> getter
    ) {
        return options.stream()
                .map(getter)
                .filter(value -> value != null)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private Integer maxSaveTerm(List<ProductOptionDraft> options) {
        return options.stream()
                .map(ProductOptionDraft::saveTerm)
                .filter(value -> value != null)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private String required(String productName, ProductRaw rawProduct) {
        if (productName == null) {
            throw new IllegalArgumentException("FSS productName is required. rawId=" + rawProduct.getId());
        }
        return productName;
    }
}
