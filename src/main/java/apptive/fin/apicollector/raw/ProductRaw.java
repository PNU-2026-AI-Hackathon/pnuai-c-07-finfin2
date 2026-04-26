package apptive.fin.apicollector.raw;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "product_raw")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRaw extends BaseTimeEntity {

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

    @Column(name = "raw_json", nullable = false, columnDefinition = "text")
    private String rawJson;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;

    @Column(name = "normalized_at")
    private Instant normalizedAt;

    @Column(name = "normalizer_version")
    private Integer normalizerVersion;

    public boolean hasSameHash(String contentHash) {
        return this.contentHash.equals(contentHash);
    }

    public ProductRaw(
            Source source,
            String externalId,
            String contentHash,
            String rawJson
    ) {
        this.source = source;
        this.externalId = externalId;
        this.contentHash = contentHash;
        this.rawJson = rawJson;
        this.lastSeenAt = Instant.now();
    }

    public void updateRaw(String contentHash, String rawJson) {
        this.contentHash = contentHash;
        this.rawJson = rawJson;
        this.lastSeenAt = Instant.now();

        this.normalizedAt = null;
    }

    public void touchSeen() {
        this.lastSeenAt = Instant.now();
    }

    public void markNormalized(int normalizerVersion) {
        this.normalizedAt = Instant.now();
        this.normalizerVersion = normalizerVersion;
    }

    public boolean needsNormalization(int currentNormalizerVersion) {
        return normalizedAt == null
                || normalizerVersion == null
                || normalizerVersion < currentNormalizerVersion;
    }

}
