package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.product.service.ProductSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeactivateMissingProductTasklet implements Tasklet {

    private final ProductSyncService productSyncService;
    private final CollectorProperties properties;

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext
    ) {

        if (properties.mode().isNormalizeOnly()) {
            log.info(
                    "DeactivateMissingProductTasklet skipped. source={}, mode={}",
                    properties.source(),
                    properties.mode()
            );
            return RepeatStatus.FINISHED;
        }


        Instant threshold = Instant.now().minus(properties.unseenDisablePeriod(), ChronoUnit.DAYS);

        if (properties.source() == Source.ALL || properties.source() == Source.ONTONG) {
            int ontongDeactivated = productSyncService.disableAllUnseenProducts(Source.ONTONG, threshold);
            log.info(
                    "DeactivateMissingProductTasklet: ontong={}",
                    ontongDeactivated
            );
        }

        if (properties.source() == Source.ALL || properties.source() == Source.FSS) {
            int fssDeactivated = productSyncService.disableAllUnseenProducts(Source.FSS, threshold);
            log.info(
                    "DeactivateMissingProductTasklet: fss={}",
                    fssDeactivated
            );

        }

        return RepeatStatus.FINISHED;
    }
}
