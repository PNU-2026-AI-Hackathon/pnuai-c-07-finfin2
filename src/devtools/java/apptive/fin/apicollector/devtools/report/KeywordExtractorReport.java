package apptive.fin.apicollector.devtools.report;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.devtools.support.DevtoolPaths;
import apptive.fin.apicollector.normalize.classifier.OntongYouthPolicyClassifier;
import apptive.fin.apicollector.normalize.normalizer.OntongYouthProductNormalizer;
import apptive.fin.apicollector.normalize.ProductClassification;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.normalize.extractor.MonthlyLimitExtractor;
import apptive.fin.apicollector.normalize.extractor.keywords.BankKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.BenefitKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.InterestKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.RegionKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.StatusKeywordRecognizer;
import apptive.fin.apicollector.normalize.extractor.keywords.TermKeywordRecognizer;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeywordExtractorReport {

    private static final Path REPORT_PATH = DevtoolPaths.REPORTS_DIR.resolve(
            "keyword-extractor-report.jsonl"
    );
    private static final Set<KeywordValueEnum> REGION_KEYWORDS = EnumSet.of(
            KeywordValueEnum.REGION_SEOUL,
            KeywordValueEnum.REGION_BUSAN,
            KeywordValueEnum.REGION_DAEGU,
            KeywordValueEnum.REGION_INCHEON,
            KeywordValueEnum.REGION_GWANGJU,
            KeywordValueEnum.REGION_DAEJEON,
            KeywordValueEnum.REGION_ULSAN,
            KeywordValueEnum.REGION_SEJONG,
            KeywordValueEnum.REGION_GYEONGGI,
            KeywordValueEnum.REGION_GANGWON,
            KeywordValueEnum.REGION_CHUNGBUK,
            KeywordValueEnum.REGION_CHUNGNAM,
            KeywordValueEnum.REGION_JEONBUK,
            KeywordValueEnum.REGION_JEONNAM,
            KeywordValueEnum.REGION_GYEONGBUK,
            KeywordValueEnum.REGION_GYEONGNAM,
            KeywordValueEnum.REGION_JEJU
    );

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OntongYouthProductNormalizer normalizer = new OntongYouthProductNormalizer(
            objectMapper,
            properties(),
            new OntongYouthPolicyClassifier(),
            new MonthlyLimitExtractor(),
            keywordExtractor()
    );

    public static void main(String[] args) throws IOException {
        KeywordExtractorReport report = new KeywordExtractorReport();
        ReportResult result = report.analyze();
        report.write(result);
        System.out.printf(
                "Wrote KeywordExtractor report to %s. saved=%d, skipped=%d, failed=%d%n",
                REPORT_PATH,
                result.summary.savedProducts(),
                result.summary.skippedProducts(),
                result.summary.failedProducts()
        );
    }

    private ReportResult analyze() throws IOException {
        List<LoadedRawProduct> rawProducts = loadOntongYouthRawProducts();
        List<Object> lines = new ArrayList<>();
        Map<KeywordValueEnum, Integer> keywordCounts = new EnumMap<>(KeywordValueEnum.class);

        int savedProducts = 0;
        int skippedProducts = 0;
        int failedProducts = 0;
        int propertiesWithAllRegionFallback = 0;
        int propertiesWithoutNonRegionKeywords = 0;

        for (LoadedRawProduct loaded : rawProducts) {
            try {
                ProductDraft draft = normalizer.normalize(loaded.productRaw());
                if (!draft.shouldSaveProduct()) {
                    skippedProducts++;
                    lines.add(skippedLine(loaded, draft.classification()));
                    continue;
                }

                savedProducts++;
                for (int propertyIndex = 0; propertyIndex < draft.properties().size(); propertyIndex++) {
                    ProductPropertyDraft property = draft.properties().get(propertyIndex);
                    List<KeywordValueEnum> keywords = sortedKeywords(property.keywords());
                    List<KeywordValueEnum> nonRegionKeywords = keywords.stream()
                            .filter(keyword -> !REGION_KEYWORDS.contains(keyword))
                            .toList();
                    boolean allRegionFallback = keywords.containsAll(REGION_KEYWORDS);
                    boolean noNonRegionKeywords = nonRegionKeywords.isEmpty();

                    if (allRegionFallback) {
                        propertiesWithAllRegionFallback++;
                    }
                    if (noNonRegionKeywords) {
                        propertiesWithoutNonRegionKeywords++;
                    }
                    for (KeywordValueEnum keyword : keywords) {
                        keywordCounts.merge(keyword, 1, Integer::sum);
                    }

                    lines.add(productLine(
                            loaded,
                            draft,
                            property,
                            propertyIndex,
                            allRegionFallback,
                            noNonRegionKeywords,
                            keywords,
                            nonRegionKeywords
                    ));
                }
            }
            catch (Exception e) {
                failedProducts++;
                lines.add(new ErrorLine(
                        "error",
                        loaded.rawId(),
                        loaded.productRaw().getExternalId(),
                        rawText(loaded.raw(), "plcyNm"),
                        e.getClass().getSimpleName(),
                        e.getMessage()
                ));
            }
        }

        SummaryLine summary = new SummaryLine(
                "summary",
                rawProducts.size(),
                savedProducts,
                skippedProducts,
                failedProducts,
                propertiesWithAllRegionFallback,
                propertiesWithoutNonRegionKeywords,
                keywordCountNames(keywordCounts)
        );
        return new ReportResult(summary, lines);
    }

    private ProductLine productLine(
            LoadedRawProduct loaded,
            ProductDraft draft,
            ProductPropertyDraft property,
            int propertyIndex,
            boolean allRegionFallback,
            boolean noNonRegionKeywords,
            List<KeywordValueEnum> keywords,
            List<KeywordValueEnum> nonRegionKeywords
    ) {
        JsonNode raw = loaded.raw();
        return new ProductLine(
                "product",
                loaded.rawId(),
                loaded.productRaw().getExternalId(),
                draft.productCode(),
                draft.productName(),
                property.providerName(),
                propertyIndex,
                rawText(raw, "plcyKywdNm"),
                rawText(raw, "lclsfNm"),
                rawText(raw, "mclsfNm"),
                rawText(raw, "zipCd"),
                allRegionFallback,
                noNonRegionKeywords,
                keywordNames(keywords),
                keywordNames(nonRegionKeywords)
        );
    }

    private SkippedLine skippedLine(LoadedRawProduct loaded, ProductClassification classification) {
        return new SkippedLine(
                "skipped",
                loaded.rawId(),
                loaded.productRaw().getExternalId(),
                rawText(loaded.raw(), "plcyNm"),
                classification
        );
    }

    private void write(ReportResult result) throws IOException {
        Files.createDirectories(REPORT_PATH.getParent());
        Files.writeString(REPORT_PATH, toJsonLines(result));
    }

    private String toJsonLines(ReportResult result) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(objectMapper.writeValueAsString(result.summary())).append('\n');
        for (Object line : result.lines()) {
            builder.append(objectMapper.writeValueAsString(line)).append('\n');
        }
        return builder.toString();
    }

    private List<LoadedRawProduct> loadOntongYouthRawProducts() throws IOException {
        JsonNode rows;
        try (InputStream inputStream = getClass().getResourceAsStream("/product_raw.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("product_raw.json not found on classpath");
            }
            rows = objectMapper.readTree(inputStream);
        }

        List<LoadedRawProduct> products = new ArrayList<>();
        for (JsonNode row : rows) {
            if (!isOntongYouthSource(row.path("source").asString())) {
                continue;
            }

            String rawJson = row.path("raw_json").asString();
            JsonNode raw = objectMapper.readTree(rawJson);
            ProductRaw productRaw = new ProductRaw(
                    Source.ONTONG,
                    row.path("external_id").asString(),
                    row.path("content_hash").asString(),
                    rawJson,
                    ProductType.POLICY
            );
            products.add(new LoadedRawProduct(row.path("id").asString(), productRaw, raw));
        }
        return products;
    }

    private boolean isOntongYouthSource(String source) {
        return Source.ONTONG.name().equals(source) || "ONTONG_YOUTH".equals(source);
    }

    private static KeywordExtractor keywordExtractor() {
        return new KeywordExtractor(List.of(
                new BenefitKeywordRecognizer(),
                new BankKeywordRecognizer(),
                new InterestKeywordRecognizer(),
                new RegionKeywordRecognizer(),
                new StatusKeywordRecognizer(),
                new TermKeywordRecognizer()
        ));
    }

    private CollectorProperties properties() {
        return new CollectorProperties(
                true,
                Source.ALL,
                Mode.NORMALIZE_ONLY,
                3,
                500,
                7,
                new CollectorProperties.OntongYouth("http://localhost", "key", 100),
                new CollectorProperties.Fss("http://localhost", "key", 100)
        );
    }

    private String rawText(JsonNode raw, String fieldName) {
        String value = raw.path(fieldName).asString(null);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private List<KeywordValueEnum> sortedKeywords(List<KeywordValueEnum> keywords) {
        return keywords.stream()
                .distinct()
                .sorted(Comparator.comparing(KeywordValueEnum::name))
                .toList();
    }

    private List<String> keywordNames(List<KeywordValueEnum> keywords) {
        return keywords.stream()
                .map(KeywordValueEnum::name)
                .toList();
    }

    private Map<String, Integer> keywordCountNames(Map<KeywordValueEnum, Integer> keywordCounts) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (KeywordValueEnum keyword : KeywordValueEnum.values()) {
            Integer count = keywordCounts.get(keyword);
            if (count != null) {
                counts.put(keyword.name(), count);
            }
        }
        return counts;
    }

    private record ReportResult(
            SummaryLine summary,
            List<Object> lines
    ) {}

    private record LoadedRawProduct(
            String rawId,
            ProductRaw productRaw,
            JsonNode raw
    ) {}

    private record SummaryLine(
            String type,
            int ontongYouthRawProducts,
            int savedProducts,
            int skippedProducts,
            int failedProducts,
            int propertiesWithAllRegionFallback,
            int propertiesWithoutNonRegionKeywords,
            Map<String, Integer> keywordCounts
    ) {}

    private record ProductLine(
            String type,
            String rawId,
            String externalId,
            String productCode,
            String productName,
            String providerName,
            int propertyIndex,
            String rawPolicyKeywords,
            String rawLargeCategory,
            String rawMiddleCategory,
            String rawZipCodes,
            boolean allRegionFallback,
            boolean noNonRegionKeywords,
            List<String> keywords,
            List<String> nonRegionKeywords
    ) {}

    private record SkippedLine(
            String type,
            String rawId,
            String externalId,
            String rawProductName,
            ProductClassification classification
    ) {}

    private record ErrorLine(
            String type,
            String rawId,
            String externalId,
            String rawProductName,
            String errorType,
            String errorMessage
    ) {}
}
