package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsyncProductItemWriterTest {

    @Test
    void resolvesFuturesInChunkOrder() throws Exception {
        ProductDraft first = draft("first");
        ProductDraft second = draft("second");
        List<ProductDraft> written = new ArrayList<>();
        ItemWriter<ProductDraft> delegate = chunk -> written.addAll(chunk.getItems());
        AsyncProductItemWriter writer = new AsyncProductItemWriter(delegate);

        writer.write(new Chunk<>(List.of(
                CompletableFuture.completedFuture(first),
                CompletableFuture.completedFuture(second)
        )));

        assertThat(written).containsExactly(first, second);
    }

    @Test
    void propagatesFutureFailure() {
        CompletableFuture<ProductDraft> failed = new CompletableFuture<>();
        failed.completeExceptionally(new IllegalStateException("failed"));
        AsyncProductItemWriter writer = new AsyncProductItemWriter(chunk -> {});

        assertThatThrownBy(() -> writer.write(new Chunk<>(List.of(failed))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("failed");
    }

    private ProductDraft draft(String productName) {
        return ProductDraft.builder()
                .sourceCode("FSS")
                .normalizerVersion(1)
                .productName(productName)
                .build();
    }
}
