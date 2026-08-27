package apptive.fin.apicollector.raw;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.global.util.Sha256;
import apptive.fin.apicollector.product.ProductType;

import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.cfg.JsonNodeFeature;

@Service
public class RawProductSaveService {

    private final ProductRawRepository productRawRepository;

    /**
     * 해시/저장용 canonical 직렬화 writer.
     * WRITE_PROPERTIES_SORTED 로 ObjectNode 키를 (재귀적으로) 정렬해서,
     * 외부 API가 동일 내용을 키 순서만 바꿔 내려줘도 content_hash 가 흔들리지 않게 한다.
     */
    private final ObjectWriter canonicalWriter;

    public RawProductSaveService(ProductRawRepository productRawRepository, ObjectMapper objectMapper) {
        this.productRawRepository = productRawRepository;
        this.canonicalWriter = objectMapper.writer().with(JsonNodeFeature.WRITE_PROPERTIES_SORTED);
    }

    public SaveResult saveOrUpdate(Source source, String externalId, JsonNode raw, ProductType productType) {
        String rawJson = toJson(raw);
        String hash = Sha256.hex(rawJson);

        return productRawRepository.findBySourceAndExternalId(source, externalId)
                .map(existing -> {
                    if (existing.hasSameHash(hash) && existing.hasSameType(productType)) {
                        existing.touchSeen();
                        return SaveResult.UNCHANGED;
                    }

                    existing.updateRaw(hash, rawJson, productType);
                    return SaveResult.UPDATED;
                })
                .orElseGet(()->{
                    productRawRepository.save(new ProductRaw(source, externalId, hash, rawJson, productType));
                    return SaveResult.INSERTED;
                });
    }

    private String toJson(JsonNode node) {
        try {
            return canonicalWriter.writeValueAsString(node);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to serialize raw JSON", e);
        }
    }

}
