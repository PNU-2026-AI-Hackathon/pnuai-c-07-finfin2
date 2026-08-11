package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.global.util.JsonNodes;
import apptive.fin.apicollector.normalize.ProductClassification;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.FssPreferentialRateExtractor;
import apptive.fin.apicollector.normalize.extractor.FssRequiredKeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.raw.ProductRaw;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class FssProductNormalizer implements ProductNormalizer {

    private final CollectorProperties properties;
    private final KeywordExtractor keywordExtractor;
    private final FssPreferentialRateExtractor preferentialRateExtractor;
    private final FssRequiredKeywordExtractor requiredKeywordExtractor;
    private final FssBankNameNormalizer bankNameNormalizer;
    private final FssBankUrlNormalizer bankUrlNormalizer;
    private final RawJsonReader rawJsonReader;

    public FssProductNormalizer(
            ObjectMapper objectMapper,
            CollectorProperties properties,
            KeywordExtractor keywordExtractor,
            FssPreferentialRateExtractor preferentialRateExtractor,
            FssRequiredKeywordExtractor requiredKeywordExtractor,
            FssBankNameNormalizer bankNameNormalizer,
            FssBankUrlNormalizer bankUrlNormalizer
    ) {
        this.properties = properties;
        this.keywordExtractor = keywordExtractor;
        this.preferentialRateExtractor = preferentialRateExtractor;
        this.requiredKeywordExtractor = requiredKeywordExtractor;
        this.bankNameNormalizer = bankNameNormalizer;
        this.bankUrlNormalizer = bankUrlNormalizer;
        this.rawJsonReader = new RawJsonReader(objectMapper, "FSS");
    }

    @Override
    public Source source() {
        return Source.FSS;
    }

    @Override
    public ProductDraft normalize(ProductRaw rawProduct) {
        JsonNode raw = rawJsonReader.read(rawProduct);
        JsonNode base = raw.path("base");
        String content = JsonNodes.joinContent(base, "join_way", "mtrt_int", "spcl_cnd", "join_member", "etc_note");
        String joinMethod = JsonNodes.text(base, "join_way");
        String eligibilityText = JsonNodes.text(base, "join_member");
        String cautionText = JsonNodes.text(base, "etc_note");
        // 금감원이 준 이름을 그대로 보존한다. 이름 끝 괄호(적립·지급 방식, 시즌 표기)는 은행 URL 스크래퍼가
        // 상품을 구분하는 유일한 근거라, 여기서 떼면 전북 자유/정액적립식 같은 상품을 갈라낼 수 없다.
        // 표시용 이름은 backend 의 Product.getDisplayProductName() 이 응답을 만들 때 가공한다.
        String productName = collapseWhitespace(JsonNodes.firstText(base, "fin_prdt_nm"));
        List<ProductPropertyDraft> propertyDrafts = properties(raw, base);

        var draft = ProductDraft.builder()
                    .rawId(rawProduct.getId())
                    .rawSource(rawProduct.getSource())
                    .normalizerVersion(properties.normalizerVersion())
                    .classification(ProductClassification.FINANCIAL_PRODUCT)
                    .saveProduct(true)
                    .sourceCode(Source.FSS.name())
                    .type(rawProduct.getType())
                    .productCode(rawProduct.getExternalId())
                    .productName(rawJsonReader.required(productName, "productName", rawProduct))
                    .content(content)
                    .joinMethod(joinMethod)
                    .eligibilityText(eligibilityText)
                    .cautionText(cautionText)
                    .properties(propertyDrafts)
                    .build();

        return keywordExtractor.attachTo(draft);
    }

    private List<ProductPropertyDraft> properties(
            JsonNode raw,
            JsonNode base
    ) {
        String providerCode = JsonNodes.firstText(base, "fin_co_no", "kor_co_nm");
        String providerName = collapseWhitespace(bankNameNormalizer.normalize(providerCode, JsonNodes.firstText(base, "kor_co_nm", "fin_co_no")));
        String providerApplyUrl = bankUrlNormalizer.normalize(providerCode).orElse(null);
        Long maxMonthlyLimit = JsonNodes.longValueOrNullIfZero(base, "max_limit");
        var preferentialRates = preferentialRateExtractor.extract(JsonNodes.text(base, "spcl_cnd"));
        var requiredKeywords = requiredKeywordExtractor.extract(JsonNodes.text(base, "join_member"), JsonNodes.text(base, "etc_note"));
        JsonNode optionsNode = raw.path("options");
        if (optionsNode == null || !optionsNode.isArray() || optionsNode.isEmpty()) {
            return List.of(ProductPropertyDraft.builder()
                    .providerCode(providerCode)
                    .providerName(providerName)
                    .providerApplyUrl(providerApplyUrl)
                    .maxMonthlyLimit(maxMonthlyLimit)
                    .requiresHomeless(false)
                    .requiresHouseholder(false)
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
                    .intrRateType(JsonNodes.firstText(option, "intr_rate_type"))
                    .intrRateTypeName(JsonNodes.firstText(option, "intr_rate_type_nm"))
                    .reserveType(JsonNodes.firstText(option, "rsrv_type"))
                    .saveTerm(JsonNodes.integer(option, "save_trm"))
                    .baseRate(JsonNodes.decimal(option, "intr_rate"))
                    .maxRate(JsonNodes.decimal(option, "intr_rate2"))
                    .maxMonthlyLimit(maxMonthlyLimit)
//                    .minTenureMonths(JsonNodes.integer(option, "save_trm"))
                    .requiresHomeless(false)
                    .requiresHouseholder(false)
                    .requiredKeywords(requiredKeywords)
                    .preferentialRates(preferentialRates)
                    .build());
        }
        return properties;
    }

    private static String collapseWhitespace(String value) {
        if (value == null) {
            return null;
        }
        String collapsed = value.replaceAll("\\s+", " ").trim();
        return collapsed.isEmpty() ? null : collapsed;
    }

}
