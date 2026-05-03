package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.client.fss.FssClient;
import apptive.fin.apicollector.client.fss.FssProductType;
import apptive.fin.apicollector.client.fss.FssRawProduct;
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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchFssRawTasklet implements Tasklet {

    private final FssClient fssClient;
    private final RawProductSaveService rawProductSaveService;
    private final CollectorProperties properties;

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext
    ) {
        if (properties.mode().isNormalizeOnly()) {
            log.info("FetchFssRawTasklet skipped. mode={}", properties.mode());
            return RepeatStatus.FINISHED;
        }

        List<FssRawProduct> products = fssClient.fetchAll();

        int inserted = 0;
        int updated = 0;
        int unchanged = 0;
        int skipped = 0;

        Map<FssProductType, Integer> fetchedByType = new EnumMap<>(FssProductType.class);
        Map<FssProductType, Integer> savedByType = new EnumMap<>(FssProductType.class);

        for (FssRawProduct product : products) {
            fetchedByType.merge(product.productType(), 1, Integer::sum);

            if (isInvalid(product)) {
                skipped++;
                log.warn(
                        "Skip invalid FSS product. type={}, group={}, externalId={}",
                        product.productType(),
                        product.financialGroup(),
                        product.externalId()
                );
                continue;
            }

            SaveResult result = rawProductSaveService.saveOrUpdate(
                    Source.FSS,
                    product.externalId(),
                    product.raw()
            );

            switch (result) {
                case INSERTED -> inserted++;
                case UPDATED -> updated++;
                case UNCHANGED -> unchanged++;
            }

            savedByType.merge(product.productType(), 1, Integer::sum);
        }

        log.info(
                "FetchFssRawTasklet finished. fetched={}, inserted={}, updated={}, unchanged={}, skipped={}, fetchedByType={}, savedByType={}",
                products.size(),
                inserted,
                updated,
                unchanged,
                skipped,
                fetchedByType,
                savedByType
        );

        return RepeatStatus.FINISHED;
    }

    private boolean isInvalid(FssRawProduct product) {
        return product == null
                || product.productType() == null
                || product.financialGroup() == null
                || product.externalId() == null
                || product.externalId().isBlank()
                || product.raw() == null
                || product.raw().isMissingNode()
                || product.raw().isNull();
    }
}
