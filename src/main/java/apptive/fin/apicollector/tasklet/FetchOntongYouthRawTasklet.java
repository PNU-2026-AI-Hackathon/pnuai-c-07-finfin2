package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.client.OntongYouthClient;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.raw.RawProductSaveService;
import apptive.fin.apicollector.raw.SaveResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchOntongYouthRawTasklet implements Tasklet {
    private final OntongYouthClient ontongYouthClient;
    private final RawProductSaveService rawProductSaveService;
    private final CollectorProperties properties;

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext
    ) {
        if (properties.mode().isNormalizeOnly()) {
            log.info("FetchOntongYouthRawTasklet skipped. mode={}", properties.mode());
            return RepeatStatus.FINISHED;
        }

        int inserted = 0;
        int updated = 0;
        int unchanged = 0;
        int skipped = 0;

        for (JsonNode item : ontongYouthClient.fetchAll()) {
            String externalId = item.path("plcyNo").asText(null);

            if (externalId == null || externalId.isBlank()) {
                skipped++;
                continue;
            }

            SaveResult result = rawProductSaveService.saveOrUpdate(
                    Source.ONTONG_YOUTH,
                    externalId,
                    item
            );

            switch (result) {
                case INSERTED -> inserted++;
                case UPDATED -> updated++;
                case UNCHANGED -> unchanged++;
            }
        }

        log.info("FetchOntongYouthRawTasklet finished. inserted={}, updated={}, unchanged={}, skipped={}",
                inserted, updated, unchanged, skipped);

        return RepeatStatus.FINISHED;
    }
}
