package apptive.fin.apicollector.config;

import apptive.fin.apicollector.normalize.ProductDraft;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FinancialProductSyncJobConfig {

    @Bean
    public Job financialProductSyncJob(
            JobRepository jobRepository,
            Step fetchOntongYouthRawStep,
            Step fetchFssRawStep,
            Step normalizeRawProductStep,
            Step deactivateMissingProductStep
    ) {
        return new JobBuilder("financialProductSyncJob", jobRepository)
                .start(fetchOntongYouthRawStep)
//                .next(fetchFssRawStep)
//                .next(normalizeRawProductStep)
//                .next(deactivateMissingProductStep)
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

}
