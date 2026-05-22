package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.product.service.HighInterestKeywordService;
import apptive.fin.apicollector.product.service.HighInterestKeywordService.HighInterestKeywordUpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddHighInterest implements Tasklet {

    private final HighInterestKeywordService highInterestKeywordService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        HighInterestKeywordUpdateResult result = highInterestKeywordService.refreshHighInterestKeywords();
        log.info(
                "AddHighInterest finished. median={}, rateCount={}, added={}, removed={}",
                result.median(),
                result.rateCount(),
                result.addedCount(),
                result.removedCount()
        );
        return RepeatStatus.FINISHED;
    }
}
