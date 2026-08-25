package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.bankurl.BankProductUrlPersistenceService;
import apptive.fin.apicollector.bankurl.BankProductUrlProperties;
import apptive.fin.apicollector.bankurl.BankProductUrlRepository;
import apptive.fin.apicollector.bankurl.BankProductUrlTarget;
import apptive.fin.apicollector.bankurl.ScrapeResult;
import apptive.fin.apicollector.bankurl.ScrapeStatus;
import apptive.fin.apicollector.bankurl.runner.BankProductUrlScrapeService;
import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BankProductUrlTaskletTest {

    private final BankProductUrlRepository repository = mock(BankProductUrlRepository.class);
    private final BankProductUrlScrapeService scrapeService = mock(BankProductUrlScrapeService.class);
    private final BankProductUrlPersistenceService persistenceService = mock(BankProductUrlPersistenceService.class);

    @Test
    void disabledCollectorSkipsAllWork() {
        BankProductUrlTasklet tasklet = new BankProductUrlTasklet(
                new BankProductUrlProperties(false, 4, 90, 1),
                repository,
                scrapeService,
                persistenceService
        );

        RepeatStatus status = tasklet.execute(null, null);

        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verifyNoInteractions(repository, scrapeService, persistenceService);
    }

    @Test
    void enabledCollectorScrapesTargetsAndPersistsPassedResults() {
        BankProductUrlTarget target = new BankProductUrlTarget(
                1L, "P1", "테스트정기예금", ProductType.DEPOSIT, "TEST", "테스트은행"
        );
        ScrapeResult result = new ScrapeResult(
                target, "FakeScraper", ScrapeStatus.PASS, target.productName(),
                "https://example.com/product", 1.0, "", 10, 1
        );
        when(repository.findActiveFssTargets()).thenReturn(List.of(target));
        when(scrapeService.scrape(List.of(target))).thenReturn(List.of(result));
        when(persistenceService.applyPassedResults(List.of(result))).thenReturn(2);
        BankProductUrlTasklet tasklet = new BankProductUrlTasklet(
                new BankProductUrlProperties(true, 4, 90, 1),
                repository,
                scrapeService,
                persistenceService
        );

        RepeatStatus status = tasklet.execute(null, null);

        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(persistenceService).applyPassedResults(List.of(result));
    }
}
