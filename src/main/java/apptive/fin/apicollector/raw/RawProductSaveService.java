package apptive.fin.apicollector.raw;

import apptive.fin.apicollector.Source;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class RawProductSaveService {

    private final ProductRawRepository productRawRepository;
    private final ObjectMapper objectMapper;

    public SaveResult saveOrUpdate(Source source, String externalId, JsonNode raw) {
        String rawJson = toJson(raw);
        String hash = sha256(rawJson);

        return productRawRepository.findBySourceAndExternalId(source, externalId)
                .map(existing -> {
                    if (existing.hasSameHash(hash)) {
                        existing.touchSeen();
                        return SaveResult.UNCHANGED;
                    }

                    existing.updateRaw(hash, rawJson);
                    return SaveResult.UPDATED;
                })
                .orElseGet(()->{
                    productRawRepository.save(new ProductRaw(source, externalId, hash, rawJson));
                    return SaveResult.INSERTED;
                });
    }

    private String toJson(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to serialize raw JSON", e);
        }
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to calculate hash", e);
        }
    }

}
