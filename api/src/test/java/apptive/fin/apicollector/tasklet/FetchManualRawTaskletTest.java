package apptive.fin.apicollector.tasklet;

import apptive.fin.apicollector.Mode;
import apptive.fin.apicollector.Source;
import apptive.fin.apicollector.config.CollectorProperties;
import apptive.fin.apicollector.product.ProductType;
import apptive.fin.apicollector.raw.RawProductSaveService;
import apptive.fin.apicollector.raw.SaveResult;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FetchManualRawTaskletTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FetchManualRawTasklet tasklet = new FetchManualRawTasklet(
            null,
            properties(Mode.SYNC),
            objectMapper
    );

    @Test
    void fetchesAllProductsFromActualManualResource() {
        RawProductSaveService saveService = mock(RawProductSaveService.class);
        when(saveService.saveOrUpdate(eq(Source.ONTONG), anyString(), any(), any(ProductType.class)))
                .thenReturn(SaveResult.INSERTED);
        FetchManualRawTasklet syncingTasklet = new FetchManualRawTasklet(
                saveService,
                properties(Mode.SYNC),
                objectMapper
        );

        assertThat(syncingTasklet.execute(null, null)).isEqualTo(RepeatStatus.FINISHED);

        verify(saveService, times(16)).saveOrUpdate(
                eq(Source.ONTONG),
                anyString(),
                any(),
                any(ProductType.class)
        );
    }

    @Test
    void skipsFetchInNormalizeOnlyMode() {
        RawProductSaveService saveService = mock(RawProductSaveService.class);
        FetchManualRawTasklet normalizeOnlyTasklet = new FetchManualRawTasklet(
                saveService,
                properties(Mode.NORMALIZE_ONLY),
                objectMapper
        );

        assertThat(normalizeOnlyTasklet.execute(null, null)).isEqualTo(RepeatStatus.FINISHED);

        verifyNoInteractions(saveService);
    }

    @Test
    void defaultsMissingTypeToPolicy() throws Exception {
        assertThat(tasklet.productType(objectMapper.readTree("{}"), "POLICY001"))
                .isEqualTo(ProductType.POLICY);
    }

    @Test
    void acceptsSubscriptionType() throws Exception {
        assertThat(tasklet.productType(objectMapper.readTree("{\"type\":\"SUBSCRIPTION\"}"), "SUB001"))
                .isEqualTo(ProductType.SUBSCRIPTION);
    }

    @Test
    void rejectsBankAndUnknownTypes() throws Exception {
        assertThatThrownBy(() -> tasklet.productType(
                objectMapper.readTree("{\"type\":\"SAVING\"}"),
                "INVALID001"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be POLICY or SUBSCRIPTION");

        assertThatThrownBy(() -> tasklet.productType(
                objectMapper.readTree("{\"type\":\"UNKNOWN\"}"),
                "INVALID002"
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported manual product type");
    }

    private static CollectorProperties properties(Mode mode) {
        return new CollectorProperties(
                true,
                Source.ONTONG,
                mode,
                19,
                500,
                7,
                new CollectorProperties.OntongYouth("http://localhost", "key", 100),
                new CollectorProperties.Fss("http://localhost", "key", 100),
                new CollectorProperties.Llm(
                        false,
                        "GEMINI",
                        "gemini-test",
                        1,
                        1,
                        10,
                        3,
                        0.1,
                        "http://localhost",
                        ""
                )
        );
    }
}
