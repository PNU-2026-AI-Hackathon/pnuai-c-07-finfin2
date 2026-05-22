package apptive.fin.apicollector.product.service;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.entity.Product;
import apptive.fin.apicollector.product.entity.ProductSource;
import apptive.fin.apicollector.product.entity.Provider;
import apptive.fin.apicollector.product.repository.ProductRepository;
import apptive.fin.apicollector.product.repository.ProductSourceRepository;
import apptive.fin.apicollector.product.repository.ProviderRepository;
import apptive.fin.apicollector.raw.ProductRaw;
import apptive.fin.apicollector.raw.ProductRawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

    @Transactional
    public int disableAllUnseenProducts(Source source, Instant lastSeen) {
        ProductSource productSource = productSourceRepository.findByCode(source.name())
                .orElseThrow(()->new IllegalArgumentException("invalid source"));
        return productRepository.disableBySourceAndLastSeenBefore(productSource, source, lastSeen);
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

        Product product = productRepository.findBySourceAndProductCode(source, draft.productCode())
                .orElseGet(() -> productRepository.save(Product.create(
                        source,
                        draft.type(),
                        draft.productCode(),
                        draft.productName()
                )));

        product.updateFrom(draft);
        product.replaceProperties(draft.properties(), propertyDraft -> resolveProvider(source, propertyDraft));

        markNormalized(draft);
    }

    private Provider resolveProvider(ProductSource source, ProductPropertyDraft propertyDraft) {
        return providerRepository.findBySourceAndCode(source, propertyDraft.providerCode())
                .map(existing -> {
                    existing.updateName(propertyDraft.providerName());
                    return existing;
                })
                .orElseGet(() -> providerRepository.save(Provider.create(
                        source,
                        propertyDraft.providerCode(),
                        propertyDraft.providerName()
                )));
    }

    private void markNormalized(ProductDraft draft) {
        ProductRaw raw = productRawRepository.findById(draft.rawId())
                .orElseThrow(() -> new IllegalStateException("ProductRaw not found. rawId=" + draft.rawId()));
        raw.markNormalized(draft.normalizerVersion());
    }
}
