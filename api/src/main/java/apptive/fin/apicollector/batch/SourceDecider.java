package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.config.CollectorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SourceDecider implements JobExecutionDecider {
    private final CollectorProperties properties;

    @Override
    public FlowExecutionStatus decide(
            JobExecution jobExecution,
            StepExecution stepExecution
    ) {
        return switch (properties.source()) {
            case ALL -> new FlowExecutionStatus("ALL");
            case FSS -> new FlowExecutionStatus("FSS");
            case ONTONG ->  new FlowExecutionStatus("ONTONG_YOUTH");
        };
    }
}
