package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class AsyncProductItemProcessor implements ItemProcessor<ProductRaw, CompletableFuture<ProductDraft>> {

    private final ItemProcessor<ProductRaw, ProductDraft> delegate;
    private final Executor executor;

    @Override
    public CompletableFuture<ProductDraft> process(ProductRaw item) {
        return CompletableFuture.supplyAsync(() -> processDelegate(item), executor);
    }

    private ProductDraft processDelegate(ProductRaw item) {
        try {
            return delegate.process(item);
        }
        catch (Exception e) {
            throw new CompletionException(e);
        }
    }
}
