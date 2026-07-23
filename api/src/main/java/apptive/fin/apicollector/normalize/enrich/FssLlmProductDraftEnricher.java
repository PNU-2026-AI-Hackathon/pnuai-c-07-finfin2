package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.global.util.Sha256;
import apptive.fin.apicollector.llm.*;
import apptive.fin.apicollector.llm.cache.*;
import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FSS 상품 draft를 LLM으로 보강하는 오케스트레이터.
 * 프롬프트 생성/검증/병합/캐시는 각 협력자에 위임하고, 여기서는 흐름 제어와 배치 통계만 담당한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FssLlmProductDraftEnricher implements ProductDraftEnricher, StepExecutionListener {

    private static final Duration FAILED_RETRY_COOLDOWN = Duration.ofHours(6);

    private final CollectorProperties properties;
    private final List<LlmProviderClient> providerClients;
    private final FssEnrichmentPromptBuilder promptBuilder;
    private final LlmEnrichmentValidator validator;
    private final FssEnrichmentMerger merger;
    private final LlmEnrichmentCacheStore cacheStore;

    private final AtomicInteger cacheHits = new AtomicInteger();
    private final AtomicInteger llmCalls = new AtomicInteger();
    private final AtomicInteger llmFailures = new AtomicInteger();
    private final AtomicInteger cooldownSkips = new AtomicInteger();
    private final AtomicInteger invalidCacheEntries = new AtomicInteger();

    @Override
    public void beforeStep(StepExecution stepExecution) {
        cacheHits.set(0);
        llmCalls.set(0);
        llmFailures.set(0);
        cooldownSkips.set(0);
        invalidCacheEntries.set(0);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info(
                "FSS LLM enrichment summary. cacheHits={}, llmCalls={}, llmFailures={}, cooldownSkips={}, invalidCache={}",
                cacheHits.get(),
                llmCalls.get(),
                llmFailures.get(),
                cooldownSkips.get(),
                invalidCacheEntries.get()
        );
        return null;
    }

    @Override
    public boolean supports(Source source) {
        return source == Source.FSS;
    }

    @Override
    public ProductDraft enrich(ProductRaw rawProduct, ProductDraft draft) {
        if (!enabled() || !draft.shouldSaveProduct()) {
            return draft;
        }

        LlmProviderClient providerClient = providerClient();
        String prompt = promptBuilder.build(rawProduct, draft);
        String requestHash = Sha256.hex(prompt);
        LlmEnrichmentCache cache = cacheStore.findOrCreate(rawProduct, requestHash);

        if (cache.getStatus() == LlmEnrichmentCacheStatus.SUCCESS
                && cache.getResponseJson() != null
                && requestHash.equals(cache.getRequestHash())) {
            cacheHits.incrementAndGet();
            return fromCache(cache, rawProduct, draft);
        }
        if (cache.isFailedRetryBlocked(Instant.now(), FAILED_RETRY_COOLDOWN)) {
            cooldownSkips.incrementAndGet();
            log.debug(
                    "Skipping FSS LLM enrichment during failed retry cooldown. rawId={}, externalId={}, failureCount={}",
                    rawProduct.getId(),
                    rawProduct.getExternalId(),
                    cache.getFailureCount()
            );
            return draft;
        }

        try {
            llmCalls.incrementAndGet();
            LlmProductEnrichment enrichment = providerClient.enrich(new LlmProductEnrichmentRequest(
                    properties.llm().model(),
                    prompt,
                    properties.llm().schemaVersion()
            ));
            validator.validate(enrichment);

            cacheStore.saveSuccess(cache, requestHash, enrichment);
            return merger.merge(rawProduct, draft, enrichment);
        }
        catch (Exception e) {
            llmFailures.incrementAndGet();
            log.warn("FSS LLM enrichment failed. rawId={}, externalId={}", rawProduct.getId(), rawProduct.getExternalId(), e);
            cacheStore.saveFailed(cache, requestHash, truncate(e.getMessage()));
            return draft;
        }
    }

    private ProductDraft fromCache(LlmEnrichmentCache cache, ProductRaw rawProduct, ProductDraft draft) {
        try {
            LlmProductEnrichment enrichment = cacheStore.readEnrichment(cache);
            validator.validate(enrichment);
            return merger.merge(rawProduct, draft, enrichment);
        }
        catch (Exception e) {
            invalidCacheEntries.incrementAndGet();
            log.warn("FSS LLM enrichment cache is invalid. cacheId={}", cache.getId(), e);
            return draft;
        }
    }

    private boolean enabled() {
        return properties.llm() != null
                && properties.llm().enabled()
                && properties.llm().apiKey() != null
                && !properties.llm().apiKey().isBlank();
    }

    private LlmProviderClient providerClient() {
        return providerClients.stream()
                .filter(client -> client.supports(properties.llm().provider()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unsupported LLM provider: " + properties.llm().provider()));
    }

    private String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() <= 500 ? value : value.substring(0, 500);
    }
}
