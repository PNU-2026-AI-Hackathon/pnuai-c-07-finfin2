package apptive.fin.apicollector.bankurl;

import apptive.fin.apicollector.bankurl.runner.BankProductUrlScrapeService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("bank-url-parity")
@SpringBootTest(properties = {
        "spring.batch.job.enabled=false",
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=none"
})
@ActiveProfiles("dev")
class BankProductUrlParityTest {

    private static final String RESULT_FILE = "fss_url_scrape_results.json";

    @Autowired
    private BankProductUrlRepository repository;

    @Autowired
    private BankProductUrlScrapeService scrapeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void pythonAndJavaProduceIdenticalUrlsAndStatuses() throws Exception {
        Path pythonScraperDir = Path.of(requiredSystemProperty("pythonScraperDir"));
        Path reportDir = Path.of(requiredSystemProperty("parityReportDir"));
        Path pythonFullDir = reportDir.resolve("python/full");
        Path javaFullDir = reportDir.resolve("java/full");
        Files.createDirectories(pythonFullDir);
        Files.createDirectories(javaFullDir);

        runPython(pythonScraperDir, pythonFullDir, null);
        List<ParityResult> pythonResults = readPythonResults(pythonFullDir.resolve(RESULT_FILE));
        List<BankProductUrlTarget> targets = repository.findActiveFssTargets();
        List<ParityResult> javaResults = toParityResults(scrapeService.scrape(targets));
        writeResults(javaFullDir.resolve(RESULT_FILE), javaResults);

        Comparison firstRun = compare(pythonResults, javaResults);
        // 재시도 전에 쓴다. 재시도가 예외로 끝나도 첫 실행 증거는 남아야 한다.
        // 재시도로 통과하면 diff.json 에서는 불일치가 사라지므로, 스케줄링에 따른
        // 비결정적 URL 선택을 추적하려면 이 파일이 유일한 근거다.
        writeDiff(reportDir.resolve("first-run-diff.json"), firstRun);

        Comparison comparison = firstRun;
        if (!firstRun.mismatchedKeys().isEmpty()) {
            comparison = retryMismatches(
                    pythonScraperDir, reportDir, targets, pythonResults, javaResults, firstRun.mismatchedKeys()
            );
        }

        writeDiff(reportDir.resolve("diff.json"), comparison);
        assertThat(comparison.targetSetMatches())
                .as("Python and Java target sets; see %s", reportDir.resolve("diff.json"))
                .isTrue();
        assertThat(comparison.mismatches())
                .as("URL/status differences; see %s", reportDir.resolve("diff.json"))
                .isEmpty();
        assertThat(comparison.pythonResults())
                .as("Python final results must not contain FAIL")
                .noneMatch(result -> "fail".equals(result.status()));
        assertThat(comparison.javaResults())
                .as("Java final results must not contain FAIL")
                .noneMatch(result -> "fail".equals(result.status()));
    }

    private Comparison retryMismatches(
            Path pythonScraperDir,
            Path reportDir,
            List<BankProductUrlTarget> targets,
            List<ParityResult> pythonResults,
            List<ParityResult> javaResults,
            Set<ResultKey> mismatchedKeys
    ) throws Exception {
        List<BankProductUrlTarget> retryTargets = targets.stream()
                .filter(target -> mismatchedKeys.contains(ResultKey.from(target)))
                .toList();
        Path retryCsv = reportDir.resolve("retry-targets.csv");
        writeRetryCsv(retryCsv, retryTargets);

        Path pythonRetryDir = reportDir.resolve("python/retry");
        Path javaRetryDir = reportDir.resolve("java/retry");
        Files.createDirectories(pythonRetryDir);
        Files.createDirectories(javaRetryDir);
        runPython(pythonScraperDir, pythonRetryDir, retryCsv);
        List<ParityResult> pythonRetry = readPythonResults(pythonRetryDir.resolve(RESULT_FILE));
        List<ParityResult> javaRetry = toParityResults(scrapeService.scrape(retryTargets));
        writeResults(javaRetryDir.resolve(RESULT_FILE), javaRetry);

        return compare(
                mergeResults(pythonResults, pythonRetry),
                mergeResults(javaResults, javaRetry)
        );
    }

    private void runPython(Path scraperDir, Path outputDir, Path inputCsv) throws Exception {
        List<String> command = new ArrayList<>(List.of(
                "uv", "run", "python", "main.py"
        ));
        if (inputCsv == null) {
            command.addAll(List.of("--mode", "db", "--dry-run"));
        } else {
            command.addAll(List.of("--mode", "csv", "--input", inputCsv.toAbsolutePath().toString()));
        }
        command.addAll(List.of(
                "--output-dir", outputDir.toAbsolutePath().toString(),
                "--concurrency", "4",
                "--per-provider-concurrency", "1"
        ));
        Process process = new ProcessBuilder(command)
                .directory(scraperDir.toFile())
                .inheritIO()
                .start();
        int exitCode = process.waitFor();
        if (exitCode != 0 && exitCode != 1) {
            throw new IllegalStateException("Python scraper failed to start. exitCode=" + exitCode);
        }
    }

    private List<ParityResult> readPythonResults(Path path) throws IOException {
        JsonNode root = objectMapper.readTree(Files.readString(path, StandardCharsets.UTF_8));
        List<ParityResult> results = new ArrayList<>();
        for (JsonNode item : root) {
            results.add(new ParityResult(
                    item.path("product_id").asString(),
                    item.path("product_code").asString(),
                    item.path("product_name").asString(),
                    item.path("product_type").asString(),
                    item.path("provider_code").asString(),
                    item.path("provider_name").asString(),
                    item.path("scraper").asString(),
                    item.path("status").asString().toLowerCase(Locale.ROOT),
                    item.path("title").asString(),
                    item.path("product_url").asString(),
                    item.path("similarity").asDouble(),
                    item.path("error").asString(),
                    item.path("elapsed_ms").asLong(),
                    item.path("attempts").asInt()
            ));
        }
        return sorted(results);
    }

    private List<ParityResult> toParityResults(List<ScrapeResult> results) {
        return sorted(results.stream().map(result -> new ParityResult(
                result.target().productId().toString(),
                result.target().productCode(),
                result.target().productName(),
                result.target().productType().name(),
                result.target().providerCode(),
                result.target().providerName(),
                result.scraper(),
                result.status().name().toLowerCase(Locale.ROOT),
                result.title(),
                result.productUrl(),
                Math.round(result.similarity() * 10_000.0) / 10_000.0,
                result.error(),
                result.elapsedMillis(),
                result.attempts()
        )).toList());
    }

    private Comparison compare(List<ParityResult> pythonResults, List<ParityResult> javaResults) {
        Map<ResultKey, ParityResult> pythonByKey = index(pythonResults);
        Map<ResultKey, ParityResult> javaByKey = index(javaResults);
        Set<ResultKey> allKeys = new LinkedHashSet<>(pythonByKey.keySet());
        allKeys.addAll(javaByKey.keySet());
        List<ResultDiff> mismatches = allKeys.stream()
                .filter(key -> !samePersistedResult(pythonByKey.get(key), javaByKey.get(key)))
                .map(key -> new ResultDiff(key, pythonByKey.get(key), javaByKey.get(key)))
                .toList();
        return new Comparison(
                pythonByKey.keySet().equals(javaByKey.keySet()),
                pythonResults,
                javaResults,
                mismatches
        );
    }

    private boolean samePersistedResult(ParityResult python, ParityResult java) {
        return python != null && java != null
                && python.status().equals(java.status())
                && python.productUrl().equals(java.productUrl());
    }

    private List<ParityResult> mergeResults(List<ParityResult> original, List<ParityResult> retry) {
        Map<ResultKey, ParityResult> merged = new LinkedHashMap<>(index(original));
        retry.forEach(result -> merged.put(result.key(), result));
        return sorted(new ArrayList<>(merged.values()));
    }

    private Map<ResultKey, ParityResult> index(List<ParityResult> results) {
        return results.stream().collect(Collectors.toMap(
                ParityResult::key,
                Function.identity(),
                (first, duplicate) -> first,
                LinkedHashMap::new
        ));
    }

    private void writeRetryCsv(Path path, List<BankProductUrlTarget> targets) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("product_id,product_code,product_name,product_type,source_code,provider_code,provider_name");
            writer.newLine();
            for (BankProductUrlTarget target : targets) {
                writer.write(String.join(",",
                        csv(target.productId().toString()),
                        csv(target.productCode()),
                        csv(target.productName()),
                        csv(target.productType().name()),
                        "FSS",
                        csv(target.providerCode()),
                        csv(target.providerName())
                ));
                writer.newLine();
            }
        }
    }

    private String csv(String value) {
        return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\"";
    }

    private void writeResults(Path path, List<ParityResult> results) throws IOException {
        Files.createDirectories(path.getParent());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), results);
    }

    private void writeDiff(Path path, Comparison comparison) throws IOException {
        Files.createDirectories(path.getParent());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), comparison);
    }

    private List<ParityResult> sorted(List<ParityResult> results) {
        return results.stream().sorted(Comparator
                .comparing(ParityResult::providerName)
                .thenComparing(ParityResult::productName)
                .thenComparing(ParityResult::productId))
                .toList();
    }

    private String requiredSystemProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing system property: " + name);
        }
        return value;
    }

    private record ParityResult(
            String productId,
            String productCode,
            String productName,
            String productType,
            String providerCode,
            String providerName,
            String scraper,
            String status,
            String title,
            String productUrl,
            double similarity,
            String error,
            long elapsedMs,
            int attempts
    ) {
        ResultKey key() {
            return new ResultKey(productId, providerCode, productName);
        }
    }

    private record ResultKey(String productId, String providerCode, String productName) {
        static ResultKey from(BankProductUrlTarget target) {
            return new ResultKey(target.productId().toString(), target.providerCode(), target.productName());
        }
    }

    private record ResultDiff(ResultKey key, ParityResult python, ParityResult java) {
    }

    private record Comparison(
            boolean targetSetMatches,
            List<ParityResult> pythonResults,
            List<ParityResult> javaResults,
            List<ResultDiff> mismatches
    ) {
        Set<ResultKey> mismatchedKeys() {
            return mismatches.stream().map(ResultDiff::key).collect(Collectors.toSet());
        }
    }
}
