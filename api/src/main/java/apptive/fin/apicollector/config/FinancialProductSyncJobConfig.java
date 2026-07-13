package apptive.fin.apicollector.config;

import apptive.fin.apicollector.batch.AsyncProductItemProcessor;
import apptive.fin.apicollector.batch.AsyncProductItemWriter;
import apptive.fin.apicollector.batch.RawProductItemReader;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.raw.ProductRaw;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
    public Step fetchOntongYouthRawStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            Tasklet fetchOntongYouthRawTasklet
    ) {
        return new StepBuilder("fetchOntongYouthRawStep", jobRepository)
                .tasklet(fetchOntongYouthRawTasklet, transactionManager)
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
            Step deactivateMissingProductStep
    ) {
        return new FlowBuilder<Flow>("fssSyncFlow")
                .start(fetchFssRawStep)
                .next(normalizeFssRawProductStep)
                .next(deactivateMissingProductStep)
                .build();
    }

    @Bean
    public Flow ontongYouthSyncFlow(
            Step fetchOntongYouthRawStep,
            Step normalizeOntongRawProductStep,
            Step deactivateMissingProductStep
    ) {
        return new FlowBuilder<Flow>("ontongYouthSyncFlow")
                .start(fetchOntongYouthRawStep)
                .next(normalizeOntongRawProductStep)
                .next(deactivateMissingProductStep)
                .build();
    }

    @Bean
    public Flow allSyncFlow(
            Step fetchOntongYouthRawStep,
            Step fetchFssRawStep,
            Step normalizeOntongRawProductStep,
            Step normalizeFssRawProductStep,
            Step deactivateMissingProductStep
    ) {
        return new FlowBuilder<Flow>("allSyncFlow")
                .start(fetchOntongYouthRawStep)
                .next(fetchFssRawStep)
                .next(normalizeOntongRawProductStep)
                .next(normalizeFssRawProductStep)
                .next(deactivateMissingProductStep)
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
            ExecutorService fssLlmExecutor
    ) {
        if (llmEnabled(properties)) {
            return new StepBuilder("normalizeFssRawProductStep", jobRepository)
                    .<ProductRaw, CompletableFuture<ProductDraft>>chunk(llmChunkSize(properties))
                    .reader(fssRawProductItemReader)
                    .processor(new AsyncProductItemProcessor(rawProductItemProcessor, fssLlmExecutor))
                    .writer(new AsyncProductItemWriter(productDraftItemWriter))
                    .transactionManager(transactionManager)
                    .build();
        }

        return new StepBuilder("normalizeFssRawProductStep", jobRepository)
                .<ProductRaw, ProductDraft>chunk(100)
                .reader(fssRawProductItemReader)
                .processor(rawProductItemProcessor)
                .writer(productDraftItemWriter)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService fssLlmExecutor(CollectorProperties properties) {
        AtomicInteger threadNumber = new AtomicInteger(1);
        return Executors.newFixedThreadPool(llmMaxConcurrency(properties), runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("fss-llm-" + threadNumber.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        });
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
