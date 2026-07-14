package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.normalize.ProductClassification;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.ContributionType;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

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

        List<ProductPropertyDraft> propertyDrafts = new ArrayList<>();
        for (JsonNode propertyNode : raw.path("properties")) {
            propertyDrafts.add(toPropertyDraft(propertyNode, rawProduct));
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

    private ProductPropertyDraft toPropertyDraft(JsonNode node, ProductRaw rawProduct) {
        return ProductPropertyDraft.builder()
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
}
