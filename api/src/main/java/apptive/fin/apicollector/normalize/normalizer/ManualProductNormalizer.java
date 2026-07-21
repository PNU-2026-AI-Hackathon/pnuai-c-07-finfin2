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
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ManualProductNormalizer extends AbstractProductNormalizer implements ProductNormalizer {

    private final ObjectMapper objectMapper;
    private final CollectorProperties properties;

    @Override
    public Source source() {
        return Source.ONTONG;
    }

    @Override
    public ProductDraft normalize(ProductRaw rawProduct) {
        JsonNode raw = read(rawProduct);

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
                .productName(required(text(raw, "productName"), "productName", rawProduct))
                .content(text(raw, "content"))
                .contentSummary(text(raw, "contentSummary"))
                .joinMethod(text(raw, "joinMethod"))
                .eligibilityText(text(raw, "eligibilityText"))
                .cautionText(text(raw, "cautionText"))
                .recruitmentPeriod(text(raw, "recruitmentPeriod"))
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
                .variantCode(text(node, "variantCode"))
                .providerCode(required(text(node, "providerCode"), "providerCode", rawProduct))
                .providerName(required(text(node, "providerName"), "providerName", rawProduct))
                .applyUrl(text(node, "applyUrl"))
                .providerApplyUrl(text(node, "providerApplyUrl"))
                .saveTerm(integer(node, "saveTerm"))
                .govContributionRate(decimal(node, "govContributionRate"))
                .govContributionType(contributionType(node))
                .govMatchingRatio(decimal(node, "govMatchingRatio"))
                .govMonthlyFixedContribution(longValue(node, "govMonthlyFixedContribution"))
                .govContributionPeriodMonths(integer(node, "govContributionPeriodMonths"))
                .excludeFromRateComparison(bool(node, "excludeFromRateComparison"))
                .minMonthlyLimit(longValue(node, "minMonthlyLimit"))
                .maxMonthlyLimit(longValue(node, "maxMonthlyLimit"))
                .minAge(integer(node, "minAge"))
                .maxAge(integer(node, "maxAge"))
                .allowsMilitaryAgeExtension(bool(node, "allowsMilitaryAgeExtension"))
                .militaryMaxAge(integer(node, "militaryMaxAge"))
                .earnMaxAmt(longValue(node, "earnMaxAmt"))
                .earnPercent(integer(node, "earnPercent"))
                .minTenureMonths(integer(node, "minTenureMonths"))
                .requiresHomeless(bool(node, "requiresHomeless"))
                .requiresHouseholder(bool(node, "requiresHouseholder"))
                .keywords(keywords(node))
                .requiredKeywords(requiredKeywords(node, rawProduct))
                .build();
    }

    private ContributionType contributionType(JsonNode node) {
        String value = text(node, "govContributionType");
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
            String keywordValue = required(text(item, "keywordCode"), "requiredKeywords[].keywordCode", rawProduct);
            String effectValue = required(text(item, "effect"), "requiredKeywords[].effect", rawProduct);
            String confidenceValue = required(text(item, "confidence"), "requiredKeywords[].confidence", rawProduct);

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

    private boolean bool(JsonNode node, String fieldName) {
        String value = text(node, fieldName);
        return value != null && Boolean.parseBoolean(value);
    }

    private JsonNode read(ProductRaw rawProduct) {
        try {
            return objectMapper.readTree(rawProduct.getRawJson());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse manual product raw JSON. rawId=" + rawProduct.getId(), e);
        }
    }

    private String required(String value, String fieldName, ProductRaw rawProduct) {
        if (value == null) {
            throw new IllegalArgumentException("Manual product " + fieldName + " is required. rawId=" + rawProduct.getId());
        }
        return value;
    }

    private record ManualPropertyKey(
            String providerCode,
            Integer saveTerm,
            String variantCode
    ) {
    }
}
