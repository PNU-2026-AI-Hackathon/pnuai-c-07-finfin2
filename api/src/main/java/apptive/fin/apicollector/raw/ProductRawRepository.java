package apptive.fin.apicollector.raw;

import apptive.fin.apicollector.Source;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRawRepository extends JpaRepository<ProductRaw, Long> {
    Optional<ProductRaw> findBySourceAndExternalId(
            Source source, String externalId
    );

    List<ProductRaw> findAllBySourceIn(Collection<Source> sources);

    @Query("""
        SELECT r
            FROM ProductRaw r
            WHERE r.source in :sources
                and r.id > :lastSeenId
                and (
                    r.normalizedAt is null
                    or r.normalizerVersion is null
                    or r.normalizerVersion < :normalizerVersion
                    or (
                        :llmEnrichmentEnabled = true
                        and r.source = apptive.fin.apicollector.Source.FSS
                        and not exists (
                            select c.id
                            from apptive.fin.apicollector.llm.LlmEnrichmentCache c
                            where c.source = r.source
                              and c.externalId = r.externalId
                              and c.contentHash = r.contentHash
                              and c.provider = :llmProvider
                              and c.model = :llmModel
                              and c.promptVersion = :llmPromptVersion
                              and c.schemaVersion = :llmSchemaVersion
                              and c.status = apptive.fin.apicollector.llm.LlmEnrichmentCacheStatus.SUCCESS
                        )
                    )
                )
            order by r.id asc
    """)
    List<ProductRaw> findNextNeedNormalize(
            @Param("sources") Collection<Source> sources,
            @Param("lastSeenId") Long lastSeenId,
            @Param("normalizerVersion") int normalizerVersion,
            @Param("llmEnrichmentEnabled") boolean llmEnrichmentEnabled,
            @Param("llmProvider") String llmProvider,
            @Param("llmModel") String llmModel,
            @Param("llmPromptVersion") int llmPromptVersion,
            @Param("llmSchemaVersion") int llmSchemaVersion,
            Pageable pageable
    );

    List<ProductRaw> findAllBySourceAndLastSeenAtBefore(
            Source source,
            Instant lastSeenAt
    );

}
