package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.llm.LlmEnrichmentCache;
import apptive.fin.apicollector.llm.LlmEnrichmentCacheRepository;
import apptive.fin.apicollector.llm.LlmProductEnrichment;
import apptive.fin.apicollector.raw.ProductRaw;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * LLM enrichment 캐시의 조회/생성/저장과 응답 (역)직렬화를 담당한다.
 * 캐시 키는 (source, externalId, contentHash, provider, model, prompt/schema version)로 구성된다.
 */
@Component
@RequiredArgsConstructor
public class LlmEnrichmentCacheStore {

    private final LlmEnrichmentCacheRepository cacheRepository;
    private final CollectorProperties properties;
    private final ObjectMapper objectMapper;

    public LlmEnrichmentCache findOrCreate(ProductRaw rawProduct, String requestHash) {
        return cacheRepository
                .findBySourceAndExternalIdAndContentHashAndProviderAndModelAndPromptVersionAndSchemaVersion(
                        rawProduct.getSource(),
                        rawProduct.getExternalId(),
                        rawProduct.getContentHash(),
                        properties.llm().provider(),
                        properties.llm().model(),
                        properties.llm().promptVersion(),
                        properties.llm().schemaVersion()
                )
                .orElseGet(() -> LlmEnrichmentCache.create(
                        rawProduct.getSource(),
                        rawProduct.getExternalId(),
                        rawProduct.getContentHash(),
                        properties.llm().provider(),
                        properties.llm().model(),
                        properties.llm().promptVersion(),
                        properties.llm().schemaVersion(),
                        requestHash
                ));
    }

    public void saveSuccess(LlmEnrichmentCache cache, String requestHash, LlmProductEnrichment enrichment) {
        cache.markSuccess(requestHash, objectMapper.writeValueAsString(enrichment));
        cacheRepository.save(cache);
    }

    public void saveFailed(LlmEnrichmentCache cache, String requestHash, String error) {
        cache.markFailed(requestHash, error);
        cacheRepository.save(cache);
    }

    public LlmProductEnrichment readEnrichment(LlmEnrichmentCache cache) {
        return objectMapper.readValue(cache.getResponseJson(), LlmProductEnrichment.class);
    }
}
