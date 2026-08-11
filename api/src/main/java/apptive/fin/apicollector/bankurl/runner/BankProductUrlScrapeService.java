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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BankProductUrlScrapeService {

    // 연속으로 이만큼 실패하면 워커 쪽 문제로 본다. 정상 실행에서는 FAIL 이 0건이고
    // 큐가 은행별로 인터리브돼 있어 연속 3건은 서로 다른 은행이다.
    private static final int UNHEALTHY_CONSECUTIVE_FAILURES = 3;
    // 죽은 워커가 되돌린 타깃을 처리할 기회를 한 번 더 준다.
    private static final int MAX_ROUNDS = 2;

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
        AtomicInteger requeueBudget = new AtomicInteger(properties.effectiveConcurrency());

        // 워커가 죽어 되돌려진 타깃이 남으면 한 번 더 돌린다. 되돌리기 예산이 있어 무한 반복되지 않는다.
        for (int round = 0; round < MAX_ROUNDS && !pending.isEmpty(); round++) {
            runWorkers(pending, results, workerFailures, requeueBudget);
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

    private void runWorkers(
            Queue<BankProductUrlTarget> pending,
            Collection<ScrapeResult> results,
            Queue<RuntimeException> workerFailures,
            AtomicInteger requeueBudget
    ) {
        int workerCount = Math.min(properties.effectiveConcurrency(), pending.size());
        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        try {
            List<Future<?>> futures = new ArrayList<>(workerCount);
            for (int worker = 0; worker < workerCount; worker++) {
                futures.add(executor.submit(() -> drain(pending, results, workerFailures, requeueBudget)));
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
    }

    private void drain(
            Queue<BankProductUrlTarget> pending,
            Collection<ScrapeResult> results,
            Queue<RuntimeException> workerFailures,
            AtomicInteger requeueBudget
    ) {
        try (BankScrapeWorker worker = workerFactory.create()) {
            int consecutiveFailures = 0;
            BankProductUrlTarget target;
            while ((target = pending.poll()) != null) {
                ScrapeResult result = scrapeOne(worker, target);
                consecutiveFailures = result.status() == ScrapeStatus.FAIL ? consecutiveFailures + 1 : 0;
                // Playwright 의 Browser.isConnected() 는 드라이버가 보낸 close 이벤트에서만 false 가 되므로
                // 드라이버 프로세스 사망은 잡지 못한다. 연속 실패를 보조 신호로 함께 본다.
                // 큐가 은행별로 인터리브돼 있어 연속 3건은 서로 다른 은행이다 — 워커 쪽 문제로 보는 게 맞다.
                if (!worker.isAlive() || consecutiveFailures >= UNHEALTHY_CONSECUTIVE_FAILURES) {
                    if (requeueBudget.getAndDecrement() > 0) {
                        // 못 쓰는 브라우저로 헛시도한 결과다. 확정하지 않고 살아 있는 워커에 다시 준다.
                        pending.offer(target);
                    } else {
                        results.add(result);
                    }
                    workerFailures.add(new IllegalStateException("scrape worker became unusable"));
                    return;
                }
                // 결과를 즉시 공유 컬렉션에 넣어, 이 워커가 뒤에서 죽어도 처리분이 유실되지 않게 한다.
                results.add(result);
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
