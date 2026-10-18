package apptive.fin.apicollector.raw;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.JsonNodeFeature;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RawProductSaveServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductRawRepository repository = mock(ProductRawRepository.class);
    private final RawProductSaveService service = new RawProductSaveService(repository, objectMapper);

    @Test
    void updatesTypeAndRequestsRenormalizationWhenOnlyTypeChanges() throws Exception {
        JsonNode rawJson = objectMapper.readTree("{\"productCode\":\"SUB001\"}");
        String serialized = objectMapper.writeValueAsString(rawJson);
        String hash = HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(serialized.getBytes(StandardCharsets.UTF_8))
        );
        ProductRaw existing = new ProductRaw(Source.ONTONG, "SUB001", hash, serialized, ProductType.POLICY);
        existing.markNormalized(18);
        when(repository.findBySourceAndExternalId(Source.ONTONG, "SUB001"))
                .thenReturn(Optional.of(existing));

        SaveResult result = service.saveOrUpdate(
                Source.ONTONG,
                "SUB001",
                rawJson,
                ProductType.SUBSCRIPTION
        );

        assertThat(result).isEqualTo(SaveResult.UPDATED);
        assertThat(existing.getType()).isEqualTo(ProductType.SUBSCRIPTION);
        assertThat(existing.getNormalizedAt()).isNull();
        assertThat(existing.needsNormalization(19)).isTrue();
    }

    @Test
    void treatsKeyReorderedRawAsUnchanged() throws Exception {
        // 저장된 raw: 키 순서 A
        JsonNode stored = objectMapper.readTree("{\"base\":{\"x\":1,\"y\":2},\"z\":3}");
        ProductRaw existing = new ProductRaw(
                Source.FSS, "FSS:1", canonicalHash(stored), canonicalJson(stored), ProductType.DEPOSIT);
        existing.markNormalized(1);
        when(repository.findBySourceAndExternalId(Source.FSS, "FSS:1"))
                .thenReturn(Optional.of(existing));

        // 동일 내용이되 키 순서만 다른 raw (중첩 객체 포함)
        JsonNode reordered = objectMapper.readTree("{\"z\":3,\"base\":{\"y\":2,\"x\":1}}");
        SaveResult result = service.saveOrUpdate(Source.FSS, "FSS:1", reordered, ProductType.DEPOSIT);

        assertThat(result).isEqualTo(SaveResult.UNCHANGED);
        assertThat(existing.getNormalizedAt()).isNotNull();
    }

    private String canonicalJson(JsonNode node) {
        return objectMapper.writer().with(JsonNodeFeature.WRITE_PROPERTIES_SORTED).writeValueAsString(node);
    }

    private String canonicalHash(JsonNode node) throws Exception {
        return HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256")
                        .digest(canonicalJson(node).getBytes(StandardCharsets.UTF_8))
        );
    }
}
