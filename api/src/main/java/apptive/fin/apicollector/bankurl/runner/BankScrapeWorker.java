package apptive.fin.apicollector.bankurl.runner;

import apptive.fin.apicollector.bankurl.BankProductUrlTarget;
import apptive.fin.apicollector.bankurl.scraper.BankProductScraper;
import apptive.fin.apicollector.bankurl.scraper.ScrapedProduct;

interface BankScrapeWorker extends AutoCloseable {

    ScrapedProduct scrape(BankProductScraper scraper, BankProductUrlTarget target);

    /**
     * 브라우저가 아직 쓸 수 있는 상태인지. false 면 이 워커는 남은 타깃을 가져가면 안 된다
     * (죽은 브라우저는 타깃마다 즉시 실패해 정상 워커보다 빠르게 큐를 비워버린다).
     */
    boolean isAlive();

    /**
     * 이번 실패가 상품 단위 오류가 아니라 이 워커를 더 사용할 수 없다는 신호인지.
     */
    default boolean isUnusable(RuntimeException failure) {
        return !isAlive();
    }

    @Override
    void close();
}
