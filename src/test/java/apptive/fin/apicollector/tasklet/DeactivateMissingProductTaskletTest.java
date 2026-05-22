package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.product.service.ProductSyncService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class DeactivateMissingProductTaskletTest {

    private final ProductSyncService productSyncService = mock(ProductSyncService.class);

    @Test
    void skipsWhenModeIsNormalizeOnly() {
        DeactivateMissingProductTasklet tasklet = new DeactivateMissingProductTasklet(
                productSyncService,
                properties(Source.ALL, Mode.NORMALIZE_ONLY, 3)
        );

        RepeatStatus result = tasklet.execute(null, null);

        assertThat(result).isEqualTo(RepeatStatus.FINISHED);
        verifyNoInteractions(productSyncService);
    }

    @Test
    void deactivatesOntongYouthOnlyWhenSourceIsOntongYouth() {
        DeactivateMissingProductTasklet tasklet = new DeactivateMissingProductTasklet(
                productSyncService,
                properties(Source.ONTONG, Mode.SYNC, 7)
        );

        Instant before = Instant.now().minusSeconds(1);
        RepeatStatus result = tasklet.execute(null, null);
        Instant after = Instant.now().plusSeconds(1);

        assertThat(result).isEqualTo(RepeatStatus.FINISHED);

        ArgumentCaptor<Instant> thresholdCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(productSyncService).disableAllUnseenProducts(
                org.mockito.ArgumentMatchers.eq(Source.ONTONG),
                thresholdCaptor.capture()
        );
        verify(productSyncService, never()).disableAllUnseenProducts(
                org.mockito.ArgumentMatchers.eq(Source.FSS),
                org.mockito.ArgumentMatchers.any()
        );
        assertThat(thresholdCaptor.getValue())
                .isBetween(before.minusSeconds(7 * 24 * 60 * 60), after.minusSeconds(7 * 24 * 60 * 60));
    }

    @Test
    void deactivatesFssOnlyWhenSourceIsFss() {
        DeactivateMissingProductTasklet tasklet = new DeactivateMissingProductTasklet(
                productSyncService,
                properties(Source.FSS, Mode.SYNC, 7)
        );

        RepeatStatus result = tasklet.execute(null, null);

        assertThat(result).isEqualTo(RepeatStatus.FINISHED);
        verify(productSyncService).disableAllUnseenProducts(
                org.mockito.ArgumentMatchers.eq(Source.FSS),
                org.mockito.ArgumentMatchers.any()
        );
        verify(productSyncService, never()).disableAllUnseenProducts(
                org.mockito.ArgumentMatchers.eq(Source.ONTONG),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void deactivatesAllSourcesWhenSourceIsAll() {
        DeactivateMissingProductTasklet tasklet = new DeactivateMissingProductTasklet(
                productSyncService,
                properties(Source.ALL, Mode.SYNC, 7)
        );

        RepeatStatus result = tasklet.execute(null, null);

        assertThat(result).isEqualTo(RepeatStatus.FINISHED);
        verify(productSyncService).disableAllUnseenProducts(
                org.mockito.ArgumentMatchers.eq(Source.ONTONG),
                org.mockito.ArgumentMatchers.any()
        );
        verify(productSyncService).disableAllUnseenProducts(
                org.mockito.ArgumentMatchers.eq(Source.FSS),
                org.mockito.ArgumentMatchers.any()
        );
    }

    private CollectorProperties properties(Source source, Mode mode, int unseenDisablePeriod) {
        return new CollectorProperties(
                true,
                source,
                mode,
                1,
                100,
                unseenDisablePeriod,
                null,
                null
        );
    }
}
