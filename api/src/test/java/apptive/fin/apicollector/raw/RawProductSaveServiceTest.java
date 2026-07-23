package apptive.fin.apicollector.raw;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
}
