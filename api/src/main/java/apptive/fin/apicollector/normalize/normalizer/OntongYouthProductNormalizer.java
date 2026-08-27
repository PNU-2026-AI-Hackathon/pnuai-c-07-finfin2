package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.global.util.JsonNodes;
import apptive.fin.apicollector.normalize.classifier.OntongYouthPolicyClassifier;
import apptive.fin.apicollector.normalize.ProductClassification;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.MonthlyLimitExtractor;
import apptive.fin.apicollector.raw.ProductRaw;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * 온통청년 API 폐기로 더 이상 Spring 빈으로 등록하지 않는다(ONTONG 소스는 {@link ManualProductNormalizer}가 담당).
 * {@code source()==ONTONG} 노멀라이저 빈이 둘이면 {@code RawProductItemProcessor}의 EnumMap에서 덮어써지므로
 * {@code @Component}를 제거했다. 클래스 자체는 기존 테스트가 직접 생성해 쓰므로 남겨둔다.
 */
public class OntongYouthProductNormalizer implements ProductNormalizer {

    private final CollectorProperties properties;
    private final OntongYouthPolicyClassifier classifier;
    private final MonthlyLimitExtractor monthlyLimitExtractor;
    private final KeywordExtractor keywordExtractor;
    private final RawJsonReader rawJsonReader;

    public OntongYouthProductNormalizer(
            ObjectMapper objectMapper,
            CollectorProperties properties,
            OntongYouthPolicyClassifier classifier,
            MonthlyLimitExtractor monthlyLimitExtractor,
            KeywordExtractor keywordExtractor
    ) {
        this.properties = properties;
        this.classifier = classifier;
        this.monthlyLimitExtractor = monthlyLimitExtractor;
        this.keywordExtractor = keywordExtractor;
        this.rawJsonReader = new RawJsonReader(objectMapper, "Ontong Youth");
    }

    @Override
    public Source source() {
        return Source.ONTONG;
    }

    @Override
    public ProductDraft normalize(ProductRaw rawProduct) {
        JsonNode raw = rawJsonReader.read(rawProduct);
        ProductClassification classification = classifier.classify(raw);
        if (classification != ProductClassification.FINANCIAL_PRODUCT) {
            return skippedDraft(rawProduct, classification);
        }

        String providerName = JsonNodes.firstText(raw, "rgtrInstCdNm", "sprvsnInstCdNm" , "rgtrUpInstCdNm");
        String providerCode = JsonNodes.firstText(raw, "sprvsnInstCd", "rgtrInstCd", "rgtrUpInstCd", "sprvsnInstCdNm", "rgtrInstCdNm");
        String productName = JsonNodes.firstText(raw, "plcyNm");
        String supportContent = JsonNodes.text(raw, "plcySprtCn");
        String content = JsonNodes.joinContent(
                raw,
                "plcyExplnCn",
                "plcySprtCn",
                "addAplyQlfcCndCn",
                "ptcpPrpTrgtCn",
                "plcyAplyMthdCn",
                "sbmsnDcmntCn",
                "earnEtcCn",
                "etcMttrCn"
        );
        String joinMethod = JsonNodes.text(raw, "plcyAplyMthdCn");
        String eligibilityText = JsonNodes.joinContent(raw, "addAplyQlfcCndCn", "ptcpPrpTrgtCn");
        String cautionText = JsonNodes.text(raw, "etcMttrCn");
        String recruitmentPeriod = JsonNodes.text(raw, "aplyYmd");

        var draft = ProductDraft.builder()
                .rawId(rawProduct.getId())
                .rawSource(rawProduct.getSource())
                .normalizerVersion(properties.normalizerVersion())
                .classification(classification)
                .saveProduct(true)
                .sourceCode(Source.ONTONG.name())
                .type(rawProduct.getType())
                .productCode(JsonNodes.firstText(raw, "plcyNo") != null ? JsonNodes.firstText(raw, "plcyNo") : rawProduct.getExternalId())
                .productName(rawJsonReader.required(productName, "productName", rawProduct))
                .content(content)
                .joinMethod(joinMethod)
                .eligibilityText(eligibilityText)
                .cautionText(cautionText)
                .recruitmentPeriod(recruitmentPeriod)
                .properties(java.util.List.of(ProductPropertyDraft.builder()
                        .providerCode(rawJsonReader.required(providerCode, "providerCode", rawProduct))
                        .providerName(rawJsonReader.required(providerName, "providerName", rawProduct))
                        .minAge(JsonNodes.integer(raw, "sprtTrgtMinAge"))
                        .maxAge(JsonNodes.integer(raw, "sprtTrgtMaxAge"))
                        .earnMaxAmt(JsonNodes.longValueOrNullIfZero(raw, "earnMaxAmt"))
                        .maxMonthlyLimit(monthlyLimitExtractor.extract(productName, supportContent))
                        .requiresHomeless(containsAny(content, "무주택"))
                        .requiresHouseholder(containsAny(content, "세대주"))
                        .applyUrl(JsonNodes.firstText(raw, "aplyUrlAddr", "refUrlAddr1", "refUrlAddr2"))
//                        .keywords(keywordsFromText(
//                                JsonNodes.text(raw, "plcyKywdNm"),
//                                JsonNodes.text(raw, "lclsfNm"),
//                                JsonNodes.text(raw, "mclsfNm"),
//                                JsonNodes.text(raw, "zipCd"),
//                                providerName,
//                                productName,
//                                content
//                        ))
                        .build()))
                .build();

        return keywordExtractor.attachTo(draft);
    }

    private ProductDraft skippedDraft(ProductRaw rawProduct, ProductClassification classification) {
        return ProductDraft.builder()
                .rawId(rawProduct.getId())
                .rawSource(rawProduct.getSource())
                .normalizerVersion(properties.normalizerVersion())
                .classification(classification)
                .saveProduct(false)
                .sourceCode(Source.ONTONG.name())
                .build();
    }

    private boolean containsAny(String value, String token) {
        return value != null && value.contains(token);
    }
}
