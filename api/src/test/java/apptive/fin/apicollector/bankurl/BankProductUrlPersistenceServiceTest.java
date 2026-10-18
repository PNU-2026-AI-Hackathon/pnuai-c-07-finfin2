package apptive.fin.apicollector.bankurl;

import apptive.fin.apicollector.product.ProductType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BankProductUrlPersistenceServiceTest {

    @Test
    void updatesOnlyPassedUrls() {
        BankProductUrlRepository repository = mock(BankProductUrlRepository.class);
        BankProductUrlPersistenceService service = new BankProductUrlPersistenceService(repository);
        BankProductUrlTarget target = new BankProductUrlTarget(
                1L, "P1", "테스트적금", ProductType.SAVING, "BANK", "테스트은행"
        );
        ScrapeResult pass = result(target, ScrapeStatus.PASS, "https://bank.example/pass");
        ScrapeResult warn = result(target, ScrapeStatus.WARN, "https://bank.example/warn");
        ScrapeResult fail = result(target, ScrapeStatus.FAIL, "https://bank.example/fail");
        when(repository.updateActiveFssProductUrl(1L, "BANK", pass.productUrl())).thenReturn(2);

        int updated = service.applyPassedResults(List.of(pass, warn, fail));

        assertThat(updated).isEqualTo(2);
        verify(repository).updateActiveFssProductUrl(1L, "BANK", pass.productUrl());
        verifyNoMoreInteractions(repository);
    }

    private ScrapeResult result(BankProductUrlTarget target, ScrapeStatus status, String url) {
        return new ScrapeResult(target, "TestScraper", status, "테스트적금", url, 1.0, "", 1, 1);
    }
}
