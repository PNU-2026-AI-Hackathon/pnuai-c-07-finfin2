package apptive.fin.apicollector.bankurl;

import apptive.fin.apicollector.bankurl.runner.BankProductUrlScrapeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankProductUrlTasklet implements Tasklet {

    private final BankProductUrlProperties properties;
    private final BankProductUrlRepository repository;
    private final BankProductUrlScrapeService scrapeService;
    private final BankProductUrlPersistenceService persistenceService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        if (!properties.enabled()) {
            log.info("BankProductUrlTasklet skipped. enabled=false");
            return RepeatStatus.FINISHED;
        }

        long started = System.nanoTime();
        List<BankProductUrlTarget> targets = repository.findActiveFssTargets();
        List<ScrapeResult> results = scrapeService.scrape(targets);
        int updatedRows = persistenceService.applyPassedResults(results);

        long pass = count(results, ScrapeStatus.PASS);
        long warn = count(results, ScrapeStatus.WARN);
        long fail = count(results, ScrapeStatus.FAIL);
        log.info(
                "BankProductUrlTasklet finished. total={}, pass={}, warn={}, fail={}, updatedRows={}, elapsedMs={}",
                results.size(), pass, warn, fail, updatedRows, (System.nanoTime() - started) / 1_000_000
        );
        results.stream()
                .filter(result -> result.status() != ScrapeStatus.PASS)
                .forEach(result -> log.warn(
                        "Bank product URL {}. provider={}, product={}, url={}, similarity={}, error={}",
                        result.status(),
                        result.target().providerName(),
                        result.target().productName(),
                        result.productUrl(),
                        result.similarity(),
                        result.error()
                ));
        return RepeatStatus.FINISHED;
    }

    private long count(List<ScrapeResult> results, ScrapeStatus status) {
        return results.stream().filter(result -> result.status() == status).count();
    }
}
