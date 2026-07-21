package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.ProductClassification;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.dto.RequiredKeywordDraft;
import apptive.fin.apicollector.product.ContributionType;
import apptive.fin.apicollector.product.ExtractionConfidence;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.RequiredKeywordEffect;
import apptive.fin.apicollector.raw.ProductRaw;
import apptive.fin.apicollector.util.JsonNodes;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 수동 큐레이션 JSON({@code /manual-products.json})을 {@link ProductDraft}로 변환한다.
 * 온통청년 API 폐기에 따라 ONTONG 소스는 이 노멀라이저가 담당한다({@code source()==ONTONG}).
 *
 * <p>키워드는 {@code AbstractProductNormalizer#extractKeywords} 로 재추출하지 않고 JSON에 명시된
 * 값을 그대로 사용한다(수동 데이터는 이미 정규화된 형태이므로 LLM/추출기 미사용).
 */
@Component
public class ManualProductNormalizer extends AbstractProductNormalizer implements ProductNormalizer {

    private final CollectorProperties properties;
    private final RawJsonReader rawJsonReader;

    public ManualProductNormalizer(ObjectMapper objectMapper, CollectorProperties properties) {
        this.properties = properties;
        this.rawJsonReader = new RawJsonReader(objectMapper, "Manual product");
    }

    @Override
    public Source source() {
        return Source.ONTONG;
    }

    @Override
    public ProductDraft normalize(ProductRaw rawProduct) {
        JsonNode raw = rawJsonReader.read(rawProduct);

        // ONTONG 소스에는 과거 온통청년 API 응답 형식의 product_raw가 남아 있을 수 있다.
        // 해당 형식만 제외 처리하여 normalizer 버전 변경 시
        // 수동 상품 동기화 단계가 계속 실패하는 것을 방지한다.
        if (isLegacyOntongYouthRaw(raw)) {
            return skippedLegacyDraft(rawProduct);
        }

        List<ProductPropertyDraft> propertyDrafts = new ArrayList<>();
        Set<ManualPropertyKey> propertyKeys = new HashSet<>();
        for (JsonNode propertyNode : raw.path("properties")) {
            ProductPropertyDraft propertyDraft = toPropertyDraft(propertyNode, rawProduct);
            ManualPropertyKey propertyKey = new ManualPropertyKey(
                    propertyDraft.providerCode(),
                    propertyDraft.saveTerm(),
                    propertyDraft.variantCode()
            );
            if (!propertyKeys.add(propertyKey)) {
                throw new IllegalArgumentException(
                        "Duplicate manual product property key. rawId=%d, key=%s"
                                .formatted(rawProduct.getId(), propertyKey)
                );
            }
            propertyDrafts.add(propertyDraft);
        }

        return ProductDraft.builder()
                .rawId(rawProduct.getId())
                .rawSource(rawProduct.getSource())
                .normalizerVersion(properties.normalizerVersion())
                .classification(ProductClassification.FINANCIAL_PRODUCT)
                .saveProduct(true)
                .sourceCode(Source.ONTONG.name())
                .type(rawProduct.getType())
                .productCode(rawProduct.getExternalId())
                .productName(rawJsonReader.required(JsonNodes.text(raw, "productName"), "productName", rawProduct))
                .content(JsonNodes.text(raw, "content"))
                .contentSummary(JsonNodes.text(raw, "contentSummary"))
                .joinMethod(JsonNodes.text(raw, "joinMethod"))
                .eligibilityText(JsonNodes.text(raw, "eligibilityText"))
                .cautionText(JsonNodes.text(raw, "cautionText"))
                .recruitmentPeriod(JsonNodes.text(raw, "recruitmentPeriod"))
                .properties(propertyDrafts)
                .build();
    }

    private boolean isLegacyOntongYouthRaw(JsonNode raw) {
        return !raw.has("productName")
                && (raw.hasNonNull("plcyNo") || raw.hasNonNull("plcyNm"));
    }

    private ProductDraft skippedLegacyDraft(ProductRaw rawProduct) {
        return ProductDraft.builder()
                .rawId(rawProduct.getId())
                .rawSource(rawProduct.getSource())
                .normalizerVersion(properties.normalizerVersion())
                .classification(ProductClassification.EXCLUDED)
                .saveProduct(false)
                .sourceCode(Source.ONTONG.name())
                .build();
    }

    private ProductPropertyDraft toPropertyDraft(JsonNode node, ProductRaw rawProduct) {
        return ProductPropertyDraft.builder()
                .variantCode(JsonNodes.text(node, "variantCode"))
                .providerCode(rawJsonReader.required(JsonNodes.text(node, "providerCode"), "providerCode", rawProduct))
                .providerName(rawJsonReader.required(JsonNodes.text(node, "providerName"), "providerName", rawProduct))
                .applyUrl(JsonNodes.text(node, "applyUrl"))
                .providerApplyUrl(JsonNodes.text(node, "providerApplyUrl"))
                .saveTerm(JsonNodes.integer(node, "saveTerm"))
                .govContributionRate(JsonNodes.decimal(node, "govContributionRate"))
                .govContributionType(contributionType(node))
                .govMatchingRatio(JsonNodes.decimal(node, "govMatchingRatio"))
                .govMonthlyFixedContribution(JsonNodes.longValueOrNullIfZero(node, "govMonthlyFixedContribution"))
                .govContributionPeriodMonths(JsonNodes.integer(node, "govContributionPeriodMonths"))
                .excludeFromRateComparison(JsonNodes.bool(node, "excludeFromRateComparison"))
                .minMonthlyLimit(JsonNodes.longValueOrNullIfZero(node, "minMonthlyLimit"))
                .maxMonthlyLimit(JsonNodes.longValueOrNullIfZero(node, "maxMonthlyLimit"))
                .minAge(JsonNodes.integer(node, "minAge"))
                .maxAge(JsonNodes.integer(node, "maxAge"))
                .allowsMilitaryAgeExtension(JsonNodes.bool(node, "allowsMilitaryAgeExtension"))
                .militaryMaxAge(JsonNodes.integer(node, "militaryMaxAge"))
                .earnMaxAmt(JsonNodes.longValueOrNullIfZero(node, "earnMaxAmt"))
                .earnPercent(JsonNodes.integer(node, "earnPercent"))
                .minTenureMonths(JsonNodes.integer(node, "minTenureMonths"))
                .requiresHomeless(JsonNodes.bool(node, "requiresHomeless"))
                .requiresHouseholder(JsonNodes.bool(node, "requiresHouseholder"))
                .keywords(keywords(node))
                .requiredKeywords(requiredKeywords(node, rawProduct))
                .build();
    }

    private ContributionType contributionType(JsonNode node) {
        String value = JsonNodes.text(node, "govContributionType");
        return value == null ? null : ContributionType.valueOf(value);
    }

    private List<KeywordValueEnum> keywords(JsonNode node) {
        List<KeywordValueEnum> keywords = new ArrayList<>();
        for (JsonNode keyword : node.path("keywords")) {
            String code = keyword.asString(null);
            if (code != null && !code.isBlank()) {
                keywords.add(KeywordValueEnum.valueOf(code.trim()));
            }
        }
        return keywords;
    }

    private List<RequiredKeywordDraft> requiredKeywords(JsonNode node, ProductRaw rawProduct) {
        JsonNode requiredKeywordsNode = node.path("requiredKeywords");
        if (requiredKeywordsNode.isMissingNode() || requiredKeywordsNode.isNull()) {
            return List.of();
        }
        if (!requiredKeywordsNode.isArray()) {
            throw new IllegalArgumentException(
                    "Manual product requiredKeywords must be an array. rawId=" + rawProduct.getId()
            );
        }

        List<RequiredKeywordDraft> requiredKeywords = new ArrayList<>();
        for (JsonNode item : requiredKeywordsNode) {
            String keywordValue = rawJsonReader.required(JsonNodes.text(item, "keywordCode"), "requiredKeywords[].keywordCode", rawProduct);
            String effectValue = rawJsonReader.required(JsonNodes.text(item, "effect"), "requiredKeywords[].effect", rawProduct);
            String confidenceValue = rawJsonReader.required(JsonNodes.text(item, "confidence"), "requiredKeywords[].confidence", rawProduct);

            KeywordValueEnum keywordCode;
            RequiredKeywordEffect effect;
            ExtractionConfidence confidence;
            try {
                keywordCode = KeywordValueEnum.valueOf(keywordValue);
                effect = RequiredKeywordEffect.valueOf(effectValue);
                confidence = ExtractionConfidence.valueOf(confidenceValue);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Unsupported manual required keyword. rawId=%d, value=%s"
                                .formatted(rawProduct.getId(), item),
                        e
                );
            }

            if (!keywordCode.name().startsWith("STATUS_")) {
                throw new IllegalArgumentException(
                        "Manual required keyword must use a STATUS_* code. rawId=%d, keywordCode=%s"
                                .formatted(rawProduct.getId(), keywordCode)
                );
            }

            requiredKeywords.add(RequiredKeywordDraft.builder()
                    .keywordCode(keywordCode)
                    .effect(effect)
                    .confidence(confidence)
                    .build());
        }
        return requiredKeywords;
    }

    private record ManualPropertyKey(
            String providerCode,
            Integer saveTerm,
            String variantCode
    ) {
    }
}
