package apptive.fin.apicollector.bankurl.runner;

import apptive.fin.apicollector.bankurl.BankProductUrlProperties;
import apptive.fin.apicollector.bankurl.BankProductUrlTarget;
import apptive.fin.apicollector.bankurl.ScrapeResult;
import apptive.fin.apicollector.bankurl.ScrapeStatus;
import apptive.fin.apicollector.bankurl.scraper.BankProductScraper;
import apptive.fin.apicollector.bankurl.scraper.ScrapedProduct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
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
        // 워커가 공유 큐에서 직접 꺼내 간다. 은행을 미리 나눠주면 상품 수가 많은 은행을 맡은 워커만
        // 오래 돌고 나머지는 놀기 때문에, 먼저 끝난 워커가 다음 타깃을 집어가도록 한다.
        Queue<BankProductUrlTarget> pending = new ConcurrentLinkedQueue<>(interleaveByProvider(targets));
        Collection<ScrapeResult> results = new ConcurrentLinkedQueue<>();
        Queue<RuntimeException> workerFailures = new ConcurrentLinkedQueue<>();

        int workerCount = Math.min(properties.effectiveConcurrency(), targets.size());
        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        try {
            List<Future<?>> futures = new ArrayList<>(workerCount);
            for (int worker = 0; worker < workerCount; worker++) {
                futures.add(executor.submit(() -> drain(pending, results, workerFailures)));
            }
            for (Future<?> future : futures) {
                future.get();
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bank product URL scraping was interrupted", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Bank product URL worker failed", exception.getCause());
        } finally {
            executor.shutdownNow();
        }

        // 모든 워커가 브라우저를 띄우지 못하면 큐가 남는다. 조용히 사라지지 않도록 실패로 남긴다.
        RuntimeException workerFailure = workerFailures.peek();
        BankProductUrlTarget unprocessed;
        while ((unprocessed = pending.poll()) != null) {
            results.add(failedResult(unprocessed, workerFailure == null
                    ? new IllegalStateException("no scrape worker was available")
                    : workerFailure, 0, 0));
        }

        return results.stream().sorted(Comparator
                .comparing((ScrapeResult result) -> result.target().providerName())
                .thenComparing(result -> result.target().productName())
                .thenComparing(result -> result.target().productId()))
                .toList();
    }

    /**
     * 같은 은행이 큐에 연달아 놓이지 않도록 은행별로 하나씩 번갈아 배치한다.
     * 은행 수가 워커 수보다 많은 동안에는 워커들이 서로 다른 은행을 집게 되어 한 사이트에 요청이 몰리지 않는다.
     */
    static List<BankProductUrlTarget> interleaveByProvider(List<BankProductUrlTarget> targets) {
        Map<String, List<BankProductUrlTarget>> byProvider = targets.stream().collect(Collectors.groupingBy(
                BankProductUrlTarget::providerCode,
                LinkedHashMap::new,
                Collectors.toList()
        ));
        int rounds = byProvider.values().stream().mapToInt(List::size).max().orElse(0);
        List<BankProductUrlTarget> interleaved = new ArrayList<>(targets.size());
        for (int round = 0; round < rounds; round++) {
            for (List<BankProductUrlTarget> providerTargets : byProvider.values()) {
                if (round < providerTargets.size()) {
                    interleaved.add(providerTargets.get(round));
                }
            }
        }
        return interleaved;
    }

    private void drain(
            Queue<BankProductUrlTarget> pending,
            Collection<ScrapeResult> results,
            Queue<RuntimeException> workerFailures
    ) {
        try (BankScrapeWorker worker = workerFactory.create()) {
            BankProductUrlTarget target;
            while ((target = pending.poll()) != null) {
                // 결과를 즉시 공유 컬렉션에 넣어, 이 워커가 뒤에서 죽어도 처리분이 유실되지 않게 한다.
                results.add(scrapeOne(worker, target));
            }
        } catch (RuntimeException exception) {
            // 이 워커만 빠진다. 남은 타깃은 살아 있는 다른 워커가 계속 가져간다.
            workerFailures.add(exception);
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
