package apptive.fin.apicollector.config;

import apptive.fin.apicollector.batch.RawProductItemReader;
import apptive.fin.apicollector.tasklet.BankProductUrlTasklet;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.enrich.FssLlmProductDraftEnricher;
import apptive.fin.apicollector.raw.ProductRaw;
import apptive.fin.apicollector.tasklet.FetchManualRawTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.infrastructure.support.transaction.ResourcelessTransactionManager;

import java.util.concurrent.Future;

@Configuration
@RequiredArgsConstructor
public class FinancialProductSyncJobConfig {

    @Bean
    public Job financialProductSyncJob(
        JobRepository jobRepository,
        JobExecutionDecider sourceDecider,
        Flow fssSyncFlow,
        Flow ontongYouthSyncFlow,
        Flow allSyncFlow
    ) {
        return new JobBuilder("financialProductSyncJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(sourceDecider)
                    .on("FSS").to(fssSyncFlow)
                .from(sourceDecider)
                    .on("ONTONG_YOUTH").to(ontongYouthSyncFlow)
                .from(sourceDecider)
                    .on("ALL").to(allSyncFlow)
                .end()
                .build();
    }

    @Bean
    public Step fetchManualRawStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FetchManualRawTasklet fetchManualRawTasklet
    ) {
        return new StepBuilder("fetchManualRawStep", jobRepository)
                .tasklet(fetchManualRawTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step fetchFssRawStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            Tasklet fetchFssRawTasklet
    ) {
        return new StepBuilder("fetchFssRawStep", jobRepository)
                .tasklet(fetchFssRawTasklet, transactionManager)
                .build();
    }


    @Bean
    public Flow fssSyncFlow(
            Step fetchFssRawStep,
            Step normalizeFssRawProductStep,
            Step deactivateMissingProductStep,
            Step bankProductUrlStep
    ) {
        return new FlowBuilder<Flow>("fssSyncFlow")
                .start(fetchFssRawStep)
                .next(normalizeFssRawProductStep)
                .next(deactivateMissingProductStep)
                .next(bankProductUrlStep)
                .build();
    }

    @Bean
    public Flow ontongYouthSyncFlow(
            Step fetchManualRawStep,
            Step normalizeOntongRawProductStep,
            Step deactivateMissingProductStep
    ) {
        return new FlowBuilder<Flow>("ontongYouthSyncFlow")
                .start(fetchManualRawStep)
                .next(normalizeOntongRawProductStep)
                .next(deactivateMissingProductStep)
                .build();
    }

    @Bean
    public Flow allSyncFlow(
            Step fetchManualRawStep,
            Step fetchFssRawStep,
            Step normalizeOntongRawProductStep,
            Step normalizeFssRawProductStep,
            Step deactivateMissingProductStep,
            Step bankProductUrlStep
    ) {
        return new FlowBuilder<Flow>("allSyncFlow")
                .start(fetchManualRawStep)
                .next(fetchFssRawStep)
                .next(normalizeOntongRawProductStep)
                .next(normalizeFssRawProductStep)
                .next(deactivateMissingProductStep)
                .next(bankProductUrlStep)
                .build();
    }


    @Bean
    public Step normalizeFssRawProductStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            RawProductItemReader fssRawProductItemReader,
            ItemProcessor<ProductRaw, ProductDraft> rawProductItemProcessor,
            ItemWriter<ProductDraft> productDraftItemWriter,
            CollectorProperties properties,
            TaskExecutor fssLlmExecutor,
            FssLlmProductDraftEnricher fssLlmProductDraftEnricher
    ) {
        if (llmEnabled(properties)) {
            AsyncItemProcessor<ProductRaw, ProductDraft> asyncProcessor =
                    new AsyncItemProcessor<>(rawProductItemProcessor);
            asyncProcessor.setTaskExecutor(fssLlmExecutor);

            AsyncItemWriter<ProductDraft> asyncWriter = new AsyncItemWriter<>(productDraftItemWriter);

            return new StepBuilder("normalizeFssRawProductStep", jobRepository)
                    .<ProductRaw, Future<ProductDraft>>chunk(llmChunkSize(properties))
                    .reader(fssRawProductItemReader)
                    .processor(asyncProcessor)
                    .writer(asyncWriter)
                    .transactionManager(transactionManager)
                    .listener(fssLlmProductDraftEnricher)
                    .build();
        }

        return new StepBuilder("normalizeFssRawProductStep", jobRepository)
                .<ProductRaw, ProductDraft>chunk(100)
                .reader(fssRawProductItemReader)
                .processor(rawProductItemProcessor)
                .writer(productDraftItemWriter)
                .transactionManager(transactionManager)
                .listener(fssLlmProductDraftEnricher)
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor fssLlmExecutor(CollectorProperties properties) {
        int concurrency = llmMaxConcurrency(properties);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(concurrency);
        executor.setMaxPoolSize(concurrency);
        executor.setThreadNamePrefix("fss-llm-");
        executor.setDaemon(true);
        return executor;
    }

    @Bean
    public Step normalizeOntongRawProductStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            RawProductItemReader ontongRawProductItemReader,
            ItemProcessor<ProductRaw, ProductDraft> rawProductItemProcessor,
            ItemWriter<ProductDraft> productDraftItemWriter
    ) {
        return new StepBuilder("normalizeOntongYouthRawProductStep", jobRepository)
                .<ProductRaw, ProductDraft>chunk(100)
                .reader(ontongRawProductItemReader)
                .processor(rawProductItemProcessor)
                .writer(productDraftItemWriter)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step deactivateMissingProductStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            Tasklet deactivateMissingProductTasklet
    ) {
        return new StepBuilder("deactivateMissingProductStep", jobRepository)
                .tasklet(deactivateMissingProductTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step bankProductUrlStep(
            JobRepository jobRepository,
            BankProductUrlTasklet bankProductUrlTasklet
    ) {
        return new StepBuilder("bankProductUrlStep", jobRepository)
                .tasklet(bankProductUrlTasklet, new ResourcelessTransactionManager())
                .build();
    }

    private boolean llmEnabled(CollectorProperties properties) {
        return properties.llm() != null && properties.llm().enabled();
    }

    private int llmChunkSize(CollectorProperties properties) {
        return Math.max(1, properties.llm().chunkSize());
    }

    private int llmMaxConcurrency(CollectorProperties properties) {
        if (properties.llm() == null) {
            return 1;
        }
        return Math.max(1, properties.llm().maxConcurrency());
    }

}
