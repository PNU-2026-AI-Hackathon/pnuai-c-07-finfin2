package apptive.fin.apicollector.bankurl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankProductUrlPersistenceService {

    private final BankProductUrlRepository repository;

    @Transactional
    public int applyPassedResults(List<ScrapeResult> results) {
        int updated = 0;
        for (ScrapeResult result : results) {
            if (result.status() != ScrapeStatus.PASS) {
                continue;
            }
            updated += repository.updateActiveFssProductUrl(
                    result.target().productId(),
                    result.target().providerCode(),
                    result.productUrl()
            );
        }
        return updated;
    }
}
