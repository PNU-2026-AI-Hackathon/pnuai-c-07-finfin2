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
    void workerThatKeepsFailingStopsEvenWhenBrowserReportsAlive() {
        // Playwright 의 Browser.isConnected() 는 드라이버가 보낸 close 이벤트에서만 false 가 된다.
        // 드라이버 프로세스가 죽으면 이벤트가 안 와 true 로 남으므로, 연속 실패로도 워커를 빼야 한다.
        Queue<Long> scrapedByHealthyWorker = new ConcurrentLinkedQueue<>();
        AtomicInteger created = new AtomicInteger();
        AtomicInteger deadWorkerAttempts = new AtomicInteger();
        BankScrapeWorkerFactory workerFactory = () -> {
            if (created.incrementAndGet() == 1) {
                return new BankScrapeWorker() {
                    @Override
                    public ScrapedProduct scrape(BankProductScraper ignored, BankProductUrlTarget target) {
                        deadWorkerAttempts.incrementAndGet();
                        throw new IllegalStateException("Playwright connection closed");
                    }

                    @Override
                    public boolean isAlive() {
                        return true;   // 드라이버가 죽어도 브라우저는 살아있다고 보고한다
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

        List<ScrapeResult> results = service.scrape(LongStream.rangeClosed(1, 30)
                .mapToObj(productId -> target(productId, "TEST"))
                .toList());

        assertThat(results).hasSize(30);
        // 30건 전부를 죽은 워커가 삼키지 않고, 연속 실패 임계에서 빠져야 한다.
        assertThat(deadWorkerAttempts.get()).isLessThan(10);
        assertThat(scrapedByHealthyWorker.size()).isGreaterThan(20);
    }

    @Test
    void targetHeldByDyingWorkerIsRetriedByAHealthyWorker() {
        // 죽은 브라우저로 헛시도한 결과를 그대로 FAIL 로 확정하면 그 상품은 갱신 기회를 잃는다.
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

        assertThat(results).hasSize(10)
                .allSatisfy(result -> assertThat(result.status()).isEqualTo(ScrapeStatus.PASS));
        assertThat(scrapedByHealthyWorker).hasSize(10);
    }

    @Test
    void requeueBudgetStopsInsteadOfLoopingWhenEveryWorkerDies() {
        // 모든 워커가 죽으면 되돌리기가 무한히 반복될 수 있다. 예산으로 끊고 FAIL 로 끝나야 한다.
        BankScrapeWorkerFactory workerFactory = () -> new BankScrapeWorker() {
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
        BankProductUrlScrapeService service = new BankProductUrlScrapeService(
                List.of(new FakeScraper()),
                new BankProductUrlProperties(true, 3, 90, 0),
                workerFactory
        );

        List<ScrapeResult> results = service.scrape(LongStream.rangeClosed(1, 5)
                .mapToObj(productId -> target(productId, "TEST"))
                .toList());

        assertThat(results).hasSize(5)
                .allSatisfy(result -> assertThat(result.status()).isEqualTo(ScrapeStatus.FAIL));
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
