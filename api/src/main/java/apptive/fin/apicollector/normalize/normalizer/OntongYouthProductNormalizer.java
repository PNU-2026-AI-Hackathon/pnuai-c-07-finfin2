package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.classifier.OntongYouthPolicyClassifier;
import apptive.fin.apicollector.normalize.ProductClassification;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.MonthlyLimitExtractor;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * 온통청년 API 폐기로 더 이상 Spring 빈으로 등록하지 않는다(ONTONG 소스는 {@link ManualProductNormalizer}가 담당).
 * {@code source()==ONTONG} 노멀라이저 빈이 둘이면 {@code RawProductItemProcessor}의 EnumMap에서 덮어써지므로
 * {@code @Component}를 제거했다. 클래스 자체는 기존 테스트/devtools 리포트가 직접 생성해 쓰므로 남겨둔다.
 */
@RequiredArgsConstructor
public class OntongYouthProductNormalizer extends AbstractProductNormalizer implements ProductNormalizer {

    private final ObjectMapper objectMapper;
    private final CollectorProperties properties;
    private final OntongYouthPolicyClassifier classifier;
    private final MonthlyLimitExtractor monthlyLimitExtractor;
    private final KeywordExtractor keywordExtractor;

    @Override
    public Source source() {
        return Source.ONTONG;
    }

    @Override
    public ProductDraft normalize(ProductRaw rawProduct) {
        JsonNode raw = read(rawProduct);
        ProductClassification classification = classifier.classify(raw);
        if (classification != ProductClassification.FINANCIAL_PRODUCT) {
            return skippedDraft(rawProduct, classification);
        }

        String providerName = firstText(raw, "rgtrInstCdNm", "sprvsnInstCdNm" , "rgtrUpInstCdNm");
        String providerCode = firstText(raw, "sprvsnInstCd", "rgtrInstCd", "rgtrUpInstCd", "sprvsnInstCdNm", "rgtrInstCdNm");
        String productName = firstText(raw, "plcyNm");
        String supportContent = text(raw, "plcySprtCn");
        String content = joinContent(
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
        String joinMethod = text(raw, "plcyAplyMthdCn");
        String eligibilityText = joinContent(raw, "addAplyQlfcCndCn", "ptcpPrpTrgtCn");
        String cautionText = text(raw, "etcMttrCn");
        String recruitmentPeriod = text(raw, "aplyYmd");

        var draft = ProductDraft.builder()
                .rawId(rawProduct.getId())
                .rawSource(rawProduct.getSource())
                .normalizerVersion(properties.normalizerVersion())
                .classification(classification)
                .saveProduct(true)
                .sourceCode(Source.ONTONG.name())
                .type(rawProduct.getType())
                .productCode(firstText(raw, "plcyNo") != null ? firstText(raw, "plcyNo") : rawProduct.getExternalId())
                .productName(required(productName, "productName", rawProduct))
                .content(content)
                .joinMethod(joinMethod)
                .eligibilityText(eligibilityText)
                .cautionText(cautionText)
                .recruitmentPeriod(recruitmentPeriod)
                .properties(java.util.List.of(ProductPropertyDraft.builder()
                        .providerCode(required(providerCode, "providerCode", rawProduct))
                        .providerName(required(providerName, "providerName", rawProduct))
                        .minAge(integer(raw, "sprtTrgtMinAge"))
                        .maxAge(integer(raw, "sprtTrgtMaxAge"))
                        .earnMaxAmt(longValue(raw, "earnMaxAmt"))
                        .maxMonthlyLimit(monthlyLimitExtractor.extract(productName, supportContent))
                        .requiresHomeless(containsAny(content, "무주택"))
                        .requiresHouseholder(containsAny(content, "세대주"))
                        .applyUrl(firstText(raw, "aplyUrlAddr", "refUrlAddr1", "refUrlAddr2"))
//                        .keywords(keywordsFromText(
//                                text(raw, "plcyKywdNm"),
//                                text(raw, "lclsfNm"),
//                                text(raw, "mclsfNm"),
//                                text(raw, "zipCd"),
//                                providerName,
//                                productName,
//                                content
//                        ))
                        .build()))
                .build();

        return extractKeywords(keywordExtractor, draft);
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

    private JsonNode read(ProductRaw rawProduct) {
        try {
            return objectMapper.readTree(rawProduct.getRawJson());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Ontong Youth raw JSON. rawId=" + rawProduct.getId(), e);
        }
    }

    private boolean containsAny(String value, String token) {
        return value != null && value.contains(token);
    }

    private String required(String value, String fieldName, ProductRaw rawProduct) {
        if (value == null) {
            throw new IllegalArgumentException("Ontong Youth " + fieldName + " is required. rawId=" + rawProduct.getId());
        }
        return value;
    }
}
