package apptive.fin.apicollector.bankurl.runner;

import com.microsoft.playwright.Browser;
import apptive.fin.apicollector.bankurl.BankProductUrlProperties;
import apptive.fin.apicollector.bankurl.BankProductUrlTarget;
import apptive.fin.apicollector.bankurl.ScrapeResult;
import apptive.fin.apicollector.bankurl.ScrapeStatus;
import apptive.fin.apicollector.bankurl.scraper.BankProductScraper;
import apptive.fin.apicollector.bankurl.scraper.ScrapedProduct;
import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

class BankProductUrlScrapeServiceTest {

    @Test
    void retriesTemporaryFailureAndReturnsValidatedResult() {
        AtomicInteger attempts = new AtomicInteger();
        BankProductScraper scraper = new FakeScraper();
        BankScrapeWorkerFactory workerFactory = () -> new BankScrapeWorker() {
            @Override
            public ScrapedProduct scrape(BankProductScraper ignored, BankProductUrlTarget target) {
                if (attempts.incrementAndGet() == 1) {
                    throw new IllegalStateException("temporary");
                }
                return new ScrapedProduct(target.productName(), "https://example.com/product");
            }

            @Override
            public boolean isAlive() {
                return true;
            }

            @Override
            public void close() {
            }
        };
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(scraper),
                new BankProductUrlProperties(true, 1, 90, 1),
                workerFactory
        );

        List<ScrapeResult> results = service.scrape(List.of(target("TEST")));

        assertThat(results).singleElement().satisfies(result -> {
            assertThat(result.status()).isEqualTo(ScrapeStatus.PASS);
            assertThat(result.productUrl()).isEqualTo("https://example.com/product");
            assertThat(result.attempts()).isEqualTo(2);
        });
    }

    @Test
    void unsupportedProviderIsReportedWithoutScrapeAttempt() {
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(new FakeScraper()),
                new BankProductUrlProperties(true, 1, 90, 0),
                () -> new BankScrapeWorker() {
                    @Override
                    public ScrapedProduct scrape(BankProductScraper scraper, BankProductUrlTarget target) {
                        throw new AssertionError("must not scrape unsupported provider");
                    }

                    @Override
                    public boolean isAlive() {
                        return true;
                    }

                    @Override
                    public void close() {
                    }
                }
        );

        ScrapeResult result = service.scrape(List.of(target("UNKNOWN"))).getFirst();

        assertThat(result.status()).isEqualTo(ScrapeStatus.FAIL);
        assertThat(result.attempts()).isZero();
        assertThat(result.error()).contains("no scraper mapped for provider_code");
    }

    @Test
    void sameProviderTargetsAreSpreadAcrossWorkers() {
        // 같은 은행 상품 4개를 워커 2개가 나눠 가져야 한다.
        // 은행 단위로 고정 배정하면 워커 하나가 4개를 순차 처리하므로 barrier 가 열리지 않는다.
        CyclicBarrier bothWorkersBusy = new CyclicBarrier(2);
        Set<Integer> workersThatScraped = ConcurrentHashMap.newKeySet();
        AtomicInteger workerSeq = new AtomicInteger();
        BankScrapeWorkerFactory workerFactory = () -> {
            int workerId = workerSeq.incrementAndGet();
            return new BankScrapeWorker() {
                @Override
                public ScrapedProduct scrape(BankProductScraper ignored, BankProductUrlTarget target) {
                    workersThatScraped.add(workerId);
                    try {
                        bothWorkersBusy.await(3, TimeUnit.SECONDS);
                    } catch (Exception exception) {
                        throw new IllegalStateException("worker " + workerId + " scraped alone", exception);
                    }
                    return new ScrapedProduct(target.productName(), "https://example.com/product");
                }

                @Override
                public boolean isAlive() {
                    return true;
                }

                @Override
                public void close() {
                }
            };
        };
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(new FakeScraper()),
                new BankProductUrlProperties(true, 2, 90, 0),
                workerFactory
        );

        List<ScrapeResult> results = service.scrape(List.of(
                target(1L, "TEST"), target(2L, "TEST"), target(3L, "TEST"), target(4L, "TEST")
        ));

        assertThat(results).hasSize(4)
                .allSatisfy(result -> assertThat(result.status()).isEqualTo(ScrapeStatus.PASS));
        assertThat(workersThatScraped).hasSize(2);
    }

    @Test
    void interleaveByProviderAlternatesBanksAndKeepsEveryTarget() {
        List<BankProductUrlTarget> targets = List.of(
                target(1L, "A"), target(2L, "A"), target(3L, "A"),
                target(4L, "B"),
                target(5L, "C"), target(6L, "C")
        );

        List<BankProductUrlTarget> interleaved = BankProductUrlScrapeService.interleaveByProvider(targets);

        assertThat(interleaved).containsExactlyInAnyOrderElementsOf(targets);
        assertThat(interleaved).extracting(BankProductUrlTarget::providerCode)
                .containsExactly("A", "B", "C", "A", "C", "A");
    }

    @Test
    void everyTargetIsScrapedExactlyOnce() {
        Queue<Long> scrapedProductIds = new ConcurrentLinkedQueue<>();
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(new FakeScraper()),
                new BankProductUrlProperties(true, 4, 90, 0),
                () -> passingWorker(scrapedProductIds)
        );
        List<BankProductUrlTarget> targets = LongStream.rangeClosed(1, 20)
                .mapToObj(productId -> target(productId, "TEST"))
                .toList();

        List<ScrapeResult> results = service.scrape(targets);

        assertThat(results).hasSize(20)
                .allSatisfy(result -> assertThat(result.status()).isEqualTo(ScrapeStatus.PASS));
        assertThat(scrapedProductIds).containsExactlyInAnyOrderElementsOf(
                targets.stream().map(BankProductUrlTarget::productId).toList()
        );
    }

    @Test
    void survivingWorkersFinishTheQueueWhenOneWorkerCannotStart() {
        AtomicInteger created = new AtomicInteger();
        BankScrapeWorkerFactory workerFactory = () -> {
            if (created.incrementAndGet() == 1) {
                throw new IllegalStateException("chromium is not installed");
            }
            return passingWorker(new ConcurrentLinkedQueue<>());
        };
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(new FakeScraper()),
                new BankProductUrlProperties(true, 3, 90, 0),
                workerFactory
        );

        List<ScrapeResult> results = service.scrape(LongStream.rangeClosed(1, 6)
                .mapToObj(productId -> target(productId, "TEST"))
                .toList());

        assertThat(results).hasSize(6)
                .allSatisfy(result -> assertThat(result.status()).isEqualTo(ScrapeStatus.PASS));
    }

    @Test
    void allTargetsFailWhenNoWorkerCanStart() {
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(new FakeScraper()),
                new BankProductUrlProperties(true, 2, 90, 0),
                () -> {
                    throw new IllegalStateException("chromium is not installed");
                }
        );

        List<ScrapeResult> results = service.scrape(LongStream.rangeClosed(1, 3)
                .mapToObj(productId -> target(productId, "TEST"))
                .toList());

        assertThat(results).hasSize(3).allSatisfy(result -> {
            assertThat(result.status()).isEqualTo(ScrapeStatus.FAIL);
            assertThat(result.error()).contains("chromium is not installed");
        });
    }

    @Test
    void deadBrowserWorkerStopsInsteadOfBurningThroughTheQueue() {
        // 브라우저가 죽은 워커는 타깃마다 즉시 실패하므로, 멈추지 않으면 정상 워커보다 먼저 큐를 비운다.
        // 워커 1은 첫 타깃 뒤 죽고, 워커 2가 나머지를 전부 정상 처리해야 한다.
        Queue<Long> scrapedByHealthyWorker = new ConcurrentLinkedQueue<>();
        AtomicInteger created = new AtomicInteger();
        BankScrapeWorkerFactory workerFactory = () -> {
            if (created.incrementAndGet() == 1) {
                return new BankScrapeWorker() {
                    private boolean alive = true;

                    @Override
                    public ScrapedProduct scrape(BankProductScraper ignored, BankProductUrlTarget target) {
                        alive = false;
                        throw new IllegalStateException("Browser has been closed");
                    }

                    @Override
                    public boolean isAlive() {
                        return alive;
                    }

                    @Override
                    public void close() {
                    }
                };
            }
            return passingWorker(scrapedByHealthyWorker);
        };
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(new FakeScraper()),
                new BankProductUrlProperties(true, 2, 90, 0),
                workerFactory
        );

        List<ScrapeResult> results = service.scrape(LongStream.rangeClosed(1, 10)
                .mapToObj(productId -> target(productId, "TEST"))
                .toList());

        assertThat(results).hasSize(10);
        // 죽은 워커가 삼킨 것은 자기가 집은 1건뿐이고 나머지 9건은 살아 있는 워커가 처리한다.
        assertThat(results).filteredOn(result -> result.status() == ScrapeStatus.FAIL).hasSize(1);
        assertThat(scrapedByHealthyWorker).hasSize(9);
    }

    private BankScrapeWorker passingWorker(Queue<Long> scrapedProductIds) {
        return new BankScrapeWorker() {
            @Override
            public ScrapedProduct scrape(BankProductScraper ignored, BankProductUrlTarget target) {
                scrapedProductIds.add(target.productId());
                return new ScrapedProduct(target.productName(), "https://example.com/product");
            }

            @Override
            public boolean isAlive() {
                return true;
            }

            @Override
            public void close() {
            }
        };
    }

    private BankProductUrlTarget target(String providerCode) {
        return target(1L, providerCode);
    }

    private BankProductUrlTarget target(long productId, String providerCode) {
        return new BankProductUrlTarget(
                productId, "P" + productId, "테스트정기예금", ProductType.DEPOSIT, providerCode, "테스트은행"
        );
    }

    private static class FakeScraper implements BankProductScraper {

        @Override
        public String providerCode() {
            return "TEST";
        }

        @Override
        public Set<String> allowedDomains() {
            return Set.of("example.com");
        }

        @Override
        public ScrapedProduct scrape(Browser browser, String productName, int timeoutMillis) {
            throw new AssertionError("worker boundary should invoke the fake worker");
        }
    }
}
