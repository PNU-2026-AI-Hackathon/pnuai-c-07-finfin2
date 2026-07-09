package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.ProductClassification;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.FssPreferentialRateExtractor;
import apptive.fin.apicollector.normalize.extractor.FssRequiredKeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FssProductNormalizer extends AbstractProductNormalizer implements ProductNormalizer {

    private final ObjectMapper objectMapper;
    private final CollectorProperties properties;
    private final KeywordExtractor keywordExtractor;
    private final FssPreferentialRateExtractor preferentialRateExtractor;
    private final FssRequiredKeywordExtractor requiredKeywordExtractor;
    private final FssBankNameNormalizer bankNameNormalizer;
    private final FssBankUrlNormalizer bankUrlNormalizer;

    @Override
    public Source source() {
        return Source.FSS;
    }

    @Override
    public ProductDraft normalize(ProductRaw rawProduct) {
        JsonNode raw = read(rawProduct);
        JsonNode base = raw.path("base");
        String content = joinContent(base, "join_way", "mtrt_int", "spcl_cnd", "join_member", "etc_note");
        String joinMethod = text(base, "join_way");
        String eligibilityText = text(base, "join_member");
        String cautionText = text(base, "etc_note");
        String productName = firstText(base, "fin_prdt_nm");
        List<ProductPropertyDraft> propertyDrafts = properties(raw, base, productName, content);

        var draft = ProductDraft.builder()
                    .rawId(rawProduct.getId())
                    .rawSource(rawProduct.getSource())
                    .normalizerVersion(properties.normalizerVersion())
                    .classification(ProductClassification.FINANCIAL_PRODUCT)
                    .saveProduct(true)
                    .sourceCode(Source.FSS.name())
                    .type(rawProduct.getType())
                    .productCode(rawProduct.getExternalId())
                    .productName(required(productName, rawProduct))
                    .content(content)
                    .joinMethod(joinMethod)
                    .eligibilityText(eligibilityText)
                    .cautionText(cautionText)
                    .properties(propertyDrafts)
                    .build();

        return extractKeywords(keywordExtractor, draft);
    }

    private JsonNode read(ProductRaw rawProduct) {
        try {
            return objectMapper.readTree(rawProduct.getRawJson());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse FSS raw JSON. rawId=" + rawProduct.getId(), e);
        }
    }

    private List<ProductPropertyDraft> properties(
            JsonNode raw,
            JsonNode base,
            String productName,
            String content
    ) {
        List<apptive.fin.apicollector.product.KeywordValueEnum> keywords = keywordsFromText(
                text(raw, "productType"),
                text(raw, "financialGroupName"),
                productName,
                content
        );
        String providerCode = firstText(base, "fin_co_no", "kor_co_nm");
        String providerName = bankNameNormalizer.normalize(providerCode, firstText(base, "kor_co_nm", "fin_co_no"));
        String providerApplyUrl = bankUrlNormalizer.normalize(providerCode).orElse(null);
        Long maxMonthlyLimit = longValue(base, "max_limit");
        var preferentialRates = preferentialRateExtractor.extract(text(base, "spcl_cnd"));
        var requiredKeywords = requiredKeywordExtractor.extract(text(base, "join_member"), text(base, "etc_note"));
        JsonNode optionsNode = raw.path("options");
        if (optionsNode == null || !optionsNode.isArray() || optionsNode.isEmpty()) {
            return List.of(ProductPropertyDraft.builder()
                    .providerCode(providerCode)
                    .providerName(providerName)
                    .providerApplyUrl(providerApplyUrl)
                    .maxMonthlyLimit(maxMonthlyLimit)
                    .requiresHomeless(false)
                    .requiresHouseholder(false)
                    .keywords(keywords)
                    .requiredKeywords(requiredKeywords)
                    .preferentialRates(preferentialRates)
                    .build());
        }

        List<ProductPropertyDraft> properties = new ArrayList<>();
        for (JsonNode option : optionsNode) {
            properties.add(ProductPropertyDraft.builder()
                    .providerCode(providerCode)
                    .providerName(providerName)
                    .providerApplyUrl(providerApplyUrl)
                    .intrRateType(firstText(option, "intr_rate_type"))
                    .intrRateTypeName(firstText(option, "intr_rate_type_nm"))
                    .reserveType(firstText(option, "rsrv_type"))
                    .saveTerm(integer(option, "save_trm"))
                    .baseRate(decimal(option, "intr_rate"))
                    .maxRate(decimal(option, "intr_rate2"))
                    .maxMonthlyLimit(maxMonthlyLimit)
//                    .minTenureMonths(integer(option, "save_trm"))
                    .requiresHomeless(false)
                    .requiresHouseholder(false)
                    .keywords(keywords)
                    .requiredKeywords(requiredKeywords)
                    .preferentialRates(preferentialRates)
                    .build());
        }
        return properties;
    }

    private String required(String productName, ProductRaw rawProduct) {
        if (productName == null) {
            throw new IllegalArgumentException("FSS productName is required. rawId=" + rawProduct.getId());
        }
        return productName;
    }
}
