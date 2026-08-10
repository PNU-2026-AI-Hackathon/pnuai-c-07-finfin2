package apptive.fin.apicollector.bankurl.runner;

import apptive.fin.apicollector.bankurl.BankProductUrlProperties;
import apptive.fin.apicollector.bankurl.BankProductUrlTarget;
import apptive.fin.apicollector.bankurl.ScrapeResult;
import apptive.fin.apicollector.bankurl.ScrapeStatus;
import apptive.fin.apicollector.bankurl.scraper.BankProductScraper;
import apptive.fin.apicollector.bankurl.scraper.ScrapedProduct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BankProductUrlScrapeService {

    private final Map<String, BankProductScraper> scrapersByProviderCode;
    private final BankProductUrlProperties properties;
    private final BankScrapeWorkerFactory workerFactory;
    private final ProductUrlValidator validator = new ProductUrlValidator();

    public BankProductUrlScrapeService(
            List<BankProductScraper> scrapers,
            BankProductUrlProperties properties,
            BankScrapeWorkerFactory workerFactory
    ) {
        this.scrapersByProviderCode = scrapers.stream().collect(Collectors.toUnmodifiableMap(
                BankProductScraper::providerCode,
                Function.identity()
        ));
        this.properties = properties;
        this.workerFactory = workerFactory;
    }

    public List<ScrapeResult> scrape(List<BankProductUrlTarget> targets) {
        if (targets.isEmpty()) {
            return List.of();
        }
        List<List<BankProductUrlTarget>> partitions = partitionByProvider(targets);
        ExecutorService executor = Executors.newFixedThreadPool(partitions.size());
        try {
            List<Future<List<ScrapeResult>>> futures = partitions.stream()
                    .map(partition -> executor.submit(() -> scrapePartition(partition)))
                    .toList();
            List<ScrapeResult> results = new ArrayList<>();
            for (Future<List<ScrapeResult>> future : futures) {
                results.addAll(future.get());
            }
            return results.stream().sorted(Comparator
                    .comparing((ScrapeResult result) -> result.target().providerName())
                    .thenComparing(result -> result.target().productName())
                    .thenComparing(result -> result.target().productId()))
                    .toList();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bank product URL scraping was interrupted", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Bank product URL worker failed", exception.getCause());
        } finally {
            executor.shutdownNow();
        }
    }

    private List<List<BankProductUrlTarget>> partitionByProvider(List<BankProductUrlTarget> targets) {
        Map<String, List<BankProductUrlTarget>> byProvider = targets.stream().collect(Collectors.groupingBy(
                BankProductUrlTarget::providerCode,
                LinkedHashMap::new,
                Collectors.toList()
        ));
        int count = Math.min(properties.effectiveConcurrency(), byProvider.size());
        List<List<BankProductUrlTarget>> partitions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            partitions.add(new ArrayList<>());
        }
        int index = 0;
        for (List<BankProductUrlTarget> providerTargets : byProvider.values()) {
            partitions.get(index++ % count).addAll(providerTargets);
        }
        return partitions;
    }

    private List<ScrapeResult> scrapePartition(List<BankProductUrlTarget> targets) {
        try (BankScrapeWorker worker = workerFactory.create()) {
            return targets.stream().map(target -> scrapeOne(worker, target)).toList();
        } catch (RuntimeException exception) {
            return targets.stream().map(target -> failedResult(target, exception, 0, 0)).toList();
        }
    }

    private ScrapeResult scrapeOne(BankScrapeWorker worker, BankProductUrlTarget target) {
        long started = System.nanoTime();
        BankProductScraper scraper = scrapersByProviderCode.get(target.providerCode());
        if (scraper == null) {
            return failedResult(target, new IllegalArgumentException("no scraper mapped for provider_code"), 0, started);
        }
        for (int attempt = 1; attempt <= properties.effectiveRetries() + 1; attempt++) {
            try {
                ScrapedProduct product = worker.scrape(scraper, target);
                ValidationOutcome outcome = validator.validate(
                        target.productName(), product.title(), product.productUrl(), scraper.allowedDomains()
                );
                return new ScrapeResult(
                        target,
                        scraper.getClass().getSimpleName(),
                        outcome.status(),
                        product.title(),
                        product.productUrl(),
                        outcome.similarity(),
                        outcome.error(),
                        elapsedMillis(started),
                        attempt
                );
            } catch (RuntimeException exception) {
                if (attempt > properties.effectiveRetries()) {
                    return failedResult(target, exception, attempt, started);
                }
            }
        }
        throw new IllegalStateException("unreachable");
    }

    private ScrapeResult failedResult(
            BankProductUrlTarget target,
            RuntimeException exception,
            int attempts,
            long started
    ) {
        return new ScrapeResult(
                target,
                scrapersByProviderCode.containsKey(target.providerCode())
                        ? scrapersByProviderCode.get(target.providerCode()).getClass().getSimpleName()
                        : "",
                ScrapeStatus.FAIL,
                "",
                "",
                0.0,
                exception.getClass().getSimpleName() + ": " + exception.getMessage(),
                started == 0 ? 0 : elapsedMillis(started),
                attempts
        );
    }

    private long elapsedMillis(long started) {
        return (System.nanoTime() - started) / 1_000_000;
    }
}
