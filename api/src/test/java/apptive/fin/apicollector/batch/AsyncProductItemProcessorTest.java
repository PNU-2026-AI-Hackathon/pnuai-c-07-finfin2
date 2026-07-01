package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.ProductRaw;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncProductItemProcessorTest {

    @Test
    void processesItemOnExecutor() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            ProductDraft draft = ProductDraft.builder()
                    .sourceCode("FSS")
                    .normalizerVersion(1)
                    .productName("draft")
                    .build();
            ItemProcessor<ProductRaw, ProductDraft> delegate = item -> draft;
            AsyncProductItemProcessor processor = new AsyncProductItemProcessor(delegate, executor);

            CompletableFuture<ProductDraft> result = processor.process(
                    new ProductRaw(Source.FSS, "external", "hash", "{}", ProductType.SAVING)
            );

            assertThat(result.get()).isSameAs(draft);
        }
        finally {
            executor.shutdownNow();
        }
    }
}
