package apptive.fin.apicollector.batch;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.product.service.ProductSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component("productDraftItemWriter")
@RequiredArgsConstructor
public class ProductItemWriter implements ItemWriter<ProductDraft> {

    private final ProductSyncService productSyncService;

    @Override
    public void write(Chunk<? extends ProductDraft> chunk) {
        productSyncService.sync(chunk.getItems());
    }
}
