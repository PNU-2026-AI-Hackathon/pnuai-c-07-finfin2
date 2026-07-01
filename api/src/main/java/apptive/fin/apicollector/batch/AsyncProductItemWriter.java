package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class AsyncProductItemWriter implements ItemWriter<CompletableFuture<ProductDraft>> {

    private final ItemWriter<ProductDraft> delegate;

    @Override
    public void write(Chunk<? extends CompletableFuture<ProductDraft>> chunk) throws Exception {
        List<ProductDraft> drafts = new ArrayList<>(chunk.size());
        for (CompletableFuture<ProductDraft> future : chunk) {
            drafts.add(await(future));
        }
        delegate.write(new Chunk<>(drafts));
    }

    private ProductDraft await(CompletableFuture<ProductDraft> future) throws Exception {
        try {
            return future.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
        catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException("Async product processing failed", cause);
        }
    }
}
