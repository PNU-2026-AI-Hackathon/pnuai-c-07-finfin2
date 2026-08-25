package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.bankurl.BankProductUrlPersistenceService;
import apptive.fin.apicollector.bankurl.BankProductUrlProperties;
import apptive.fin.apicollector.bankurl.BankProductUrlRepository;
import apptive.fin.apicollector.bankurl.BankProductUrlTarget;
import apptive.fin.apicollector.bankurl.ScrapeResult;
import apptive.fin.apicollector.bankurl.ScrapeStatus;
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
                        "Bank product URL {}. provider={}, product={}, title={}, url={}, similarity={}, error={}",
                        result.status(),
                        result.target().providerName(),
                        result.target().productName(),
                        result.title(),
                        result.productUrl(),
                        result.similarity(),
                        result.error()
                ));
        // 어떤 제목과 비교해서 그 판정이 나왔는지가 없으면 원인 분석이 매번 막힌다.
        // PASS 까지 포함해 전부 남기되, 평소 로그를 덮지 않도록 DEBUG 로 둔다.
        if (log.isDebugEnabled()) {
            results.forEach(result -> log.debug(
                    "Bank product URL detail. status={}, provider={}, product={}, title={}, similarity={}",
                    result.status(),
                    result.target().providerName(),
                    result.target().productName(),
                    result.title(),
                    result.similarity()
            ));
        }
        return RepeatStatus.FINISHED;
    }

    private long count(List<ScrapeResult> results, ScrapeStatus status) {
        return results.stream().filter(result -> result.status() == status).count();
    }
}
