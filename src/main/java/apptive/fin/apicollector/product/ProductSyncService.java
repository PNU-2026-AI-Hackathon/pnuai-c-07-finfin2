package apptive.fin.apicollector.product;

import apptive.fin.apicollector.normalize.ProductDraft;
import apptive.fin.apicollector.product.entity.Product;
import apptive.fin.apicollector.product.entity.ProductSource;
import apptive.fin.apicollector.product.entity.Provider;
import apptive.fin.apicollector.raw.ProductRaw;
import apptive.fin.apicollector.raw.ProductRawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSyncService {

    private final ProductSourceRepository productSourceRepository;
    private final ProviderRepository providerRepository;
    private final ProductRepository productRepository;
    private final ProductRawRepository productRawRepository;

    @Transactional
    public void sync(List<? extends ProductDraft> drafts) {
        for (ProductDraft draft : drafts) {
            sync(draft);
        }
    }

    private void sync(ProductDraft draft) {
        if (!draft.shouldSaveProduct()) {
            markNormalized(draft);
            return;
        }

        ProductSource source = productSourceRepository.findByCode(draft.sourceCode())
                .orElseGet(() -> productSourceRepository.save(ProductSource.create(
                        draft.sourceCode(),
                        draft.sourceCode()
                )));

        Provider provider = providerRepository.findBySourceAndCode(source, draft.providerCode())
                .map(existing -> {
                    existing.updateName(draft.providerName());
                    return existing;
                })
                .orElseGet(() -> providerRepository.save(Provider.create(
                        source,
                        draft.providerCode(),
                        draft.providerName()
                )));

        Product product = productRepository.findBySourceAndProductCode(source, draft.productCode())
                .orElseGet(() -> productRepository.save(Product.create(
                        source,
                        provider,
                        draft.type(),
                        draft.productCode(),
                        draft.productName()
                )));

        product.updateFrom(draft, provider);
        product.replaceOptions(draft.options());
        product.replaceKeywords(draft.keywords());

        markNormalized(draft);
    }

    private void markNormalized(ProductDraft draft) {
        ProductRaw raw = productRawRepository.findById(draft.rawId())
                .orElseThrow(() -> new IllegalStateException("ProductRaw not found. rawId=" + draft.rawId()));
        raw.markNormalized(draft.normalizerVersion());
    }
}
