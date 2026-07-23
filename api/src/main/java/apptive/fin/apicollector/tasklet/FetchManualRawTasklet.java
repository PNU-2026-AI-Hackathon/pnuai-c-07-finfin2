package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.RawProductSaveService;
import apptive.fin.apicollector.raw.SaveResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

/**
 * 수동 큐레이션 정부 정책상품을 클래스패스 JSON({@code /manual-products.json})에서 읽어
 * {@link RawProductSaveService}를 통해 {@code product_raw}에 upsert 한다.
 *
 * <p>온통청년 API 폐기에 따라 ONTONG 소스는 이제 수동 JSON을 원천으로 사용한다. externalId 를
 * 상품코드(productCode)로 저장해야 {@code DeactivateMissingProductTasklet}의 retire-on-removal
 * (lastSeen 기반 미수집분 비활성화) 조인이 성립한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FetchManualRawTasklet implements Tasklet {

    private static final String MANUAL_PRODUCTS_RESOURCE = "/manual-products.json";

    private final RawProductSaveService rawProductSaveService;
    private final CollectorProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public RepeatStatus execute(
            StepContribution contribution,
            ChunkContext chunkContext
    ) {
        if (properties.mode().isNormalizeOnly()) {
            log.info("FetchManualRawTasklet skipped. mode={}", properties.mode());
            return RepeatStatus.FINISHED;
        }

        JsonNode products = load();

        int inserted = 0;
        int updated = 0;
        int unchanged = 0;
        int skipped = 0;

        for (JsonNode item : products) {
            String externalId = item.path("productCode").asString(null);

            if (externalId == null || externalId.isBlank()) {
                skipped++;
                continue;
            }

            SaveResult result = rawProductSaveService.saveOrUpdate(
                    Source.ONTONG,
                    externalId.trim(),
                    item,
                    productType(item, externalId)
            );

            switch (result) {
                case INSERTED -> inserted++;
                case UPDATED -> updated++;
                case UNCHANGED -> unchanged++;
            }
        }

        log.info("FetchManualRawTasklet finished. inserted={}, updated={}, unchanged={}, skipped={}",
                inserted, updated, unchanged, skipped);

        return RepeatStatus.FINISHED;
    }

    ProductType productType(JsonNode item, String externalId) {
        String value = item.path("type").asString(null);
        if (value == null || value.isBlank()) {
            return ProductType.POLICY;
        }

        ProductType type;
        try {
            type = ProductType.valueOf(value.trim());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported manual product type. productCode=%s, type=%s".formatted(externalId, value),
                    e
            );
        }

        if (type != ProductType.POLICY && type != ProductType.SUBSCRIPTION) {
            throw new IllegalArgumentException(
                    "Manual product type must be POLICY or SUBSCRIPTION. productCode=%s, type=%s"
                            .formatted(externalId, type)
            );
        }
        return type;
    }

    private JsonNode load() {
        try (InputStream in = getClass().getResourceAsStream(MANUAL_PRODUCTS_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Manual products resource not found: " + MANUAL_PRODUCTS_RESOURCE);
            }
            return objectMapper.readTree(in);
        }
        catch (IllegalStateException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to load manual products from " + MANUAL_PRODUCTS_RESOURCE, e);
        }
    }
}
