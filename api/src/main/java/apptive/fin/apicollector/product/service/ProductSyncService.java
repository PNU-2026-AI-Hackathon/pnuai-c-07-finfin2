package apptive.fin.apicollector.product.service;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.normalize.DisplayNameResolver;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductSyncService {

    private final ProductSourceRepository productSourceRepository;
    private final ProviderRepository providerRepository;
    private final ProductRepository productRepository;
    private final ProductRawRepository productRawRepository;
    private final DisplayNameResolver displayNameResolver;

    @Transactional
    public void sync(List<? extends ProductDraft> drafts) {
        for (ProductDraft draft : drafts) {
            sync(draft);
        }
    }

    /**
     * 정규화가 끝난 뒤 활성 상품 전체를 대상으로 디스플레이 이름(product_name)을 확정한다.
     * FSS 상품의 끝 괄호를 떼되, 떼면 다른 상품과 겹치는 경우 원본(괄호)을 유지한다.
     *
     * @return product_name이 실제로 바뀐 상품 수
     */
    @Transactional
    public int resolveDisplayNames() {
        List<Product> products = productRepository.findAllWithJoinableProperty();
        List<DisplayNameResolver.Item> items = products.stream()
                .map(product -> new DisplayNameResolver.Item(
                        product.getId(),
                        Source.FSS.name().equals(product.getSource().getCode()) ? Source.FSS : Source.ONTONG,
                        product.getOriginalName()
                ))
                .toList();

        Map<Long, String> displayById = displayNameResolver.resolve(items);

        int updated = 0;
        for (Product product : products) {
            String display = displayById.get(product.getId());
            if (display != null && !display.equals(product.getProductName())) {
                product.applyDisplayName(display);
                updated++;
            }
        }
        return updated;
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
                    existing.updateApplyUrl(propertyDraft.providerApplyUrl());
                    return existing;
                })
                .orElseGet(() -> providerRepository.save(Provider.create(
                        source,
                        propertyDraft.providerCode(),
                        propertyDraft.providerName(),
                        propertyDraft.providerApplyUrl()
                )));
    }

    private void markNormalized(ProductDraft draft) {
        ProductRaw raw = productRawRepository.findById(draft.rawId())
                .orElseThrow(() -> new IllegalStateException("ProductRaw not found. rawId=" + draft.rawId()));
        raw.markNormalized(draft.normalizerVersion());
    }
}
