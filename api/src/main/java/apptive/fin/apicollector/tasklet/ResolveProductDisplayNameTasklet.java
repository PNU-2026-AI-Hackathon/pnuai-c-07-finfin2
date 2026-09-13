package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.product.service.ProductSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

/**
 * 정규화 이후 활성 상품 전체를 훑어 디스플레이 이름(괄호 제거 + 중복 시 유지)을 확정한다.
 * 집합 단위 판정이라 청크·비동기로 흐르는 normalize 단계 안에서는 할 수 없어 별도 단계로 둔다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResolveProductDisplayNameTasklet implements Tasklet {

    private final ProductSyncService productSyncService;

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext
    ) {
        int updated = productSyncService.resolveDisplayNames();
        log.info("ResolveProductDisplayNameTasklet: updated={}", updated);
        return RepeatStatus.FINISHED;
    }
}
