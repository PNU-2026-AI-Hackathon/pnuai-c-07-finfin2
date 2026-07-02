package apptive.fin.apicollector.llm;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "llm_enrichment_cache",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_llm_enrichment_cache_key",
                        columnNames = {
                                "source",
                                "external_id",
                                "content_hash",
                                "provider",
                                "model",
                                "prompt_version",
                                "schema_version"
                        }
                )
        }
)
public class LlmEnrichmentCache extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Source source;

    @Column(name = "external_id", nullable = false, length = 150)
    private String externalId;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(nullable = false, length = 30)
    private String provider;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "prompt_version", nullable = false)
    private int promptVersion;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LlmEnrichmentCacheStatus status;

    @Column(name = "response_json", columnDefinition = "TEXT")
    private String responseJson;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "failure_count", nullable = false)
    private int failureCount;

    @Column(name = "last_failed_at")
    private Instant lastFailedAt;

    private LlmEnrichmentCache(
            Source source,
            String externalId,
            String contentHash,
            String provider,
            String model,
            int promptVersion,
            int schemaVersion,
            String requestHash
    ) {
        this.source = source;
        this.externalId = externalId;
        this.contentHash = contentHash;
        this.provider = provider;
        this.model = model;
        this.promptVersion = promptVersion;
        this.schemaVersion = schemaVersion;
        this.requestHash = requestHash;
    }

    public static LlmEnrichmentCache create(
            Source source,
            String externalId,
            String contentHash,
            String provider,
            String model,
            int promptVersion,
            int schemaVersion,
            String requestHash
    ) {
        return new LlmEnrichmentCache(
                source,
                externalId,
                contentHash,
                provider,
                model,
                promptVersion,
                schemaVersion,
                requestHash
        );
    }

    public void markSuccess(String requestHash, String responseJson) {
        this.requestHash = requestHash;
        this.status = LlmEnrichmentCacheStatus.SUCCESS;
        this.responseJson = responseJson;
        this.errorMessage = null;
        this.failureCount = 0;
        this.lastFailedAt = null;
    }

    public void markFailed(String requestHash, String errorMessage) {
        this.requestHash = requestHash;
        this.status = LlmEnrichmentCacheStatus.FAILED;
        this.responseJson = null;
        this.errorMessage = errorMessage;
        this.failureCount++;
        this.lastFailedAt = Instant.now();
    }

    public boolean isFailedRetryBlocked(Instant now, Duration cooldown) {
        if (status != LlmEnrichmentCacheStatus.FAILED || lastFailedAt == null) {
            return false;
        }
        return lastFailedAt.plus(cooldown).isAfter(now);
    }
}
