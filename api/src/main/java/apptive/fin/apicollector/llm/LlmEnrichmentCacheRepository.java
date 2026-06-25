package apptive.fin.apicollector.llm;

import apptive.fin.apicollector.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LlmEnrichmentCacheRepository extends JpaRepository<LlmEnrichmentCache, Long> {

    Optional<LlmEnrichmentCache> findBySourceAndExternalIdAndContentHashAndProviderAndModelAndPromptVersionAndSchemaVersion(
            Source source,
            String externalId,
            String contentHash,
            String provider,
            String model,
            int promptVersion,
            int schemaVersion
    );
}
