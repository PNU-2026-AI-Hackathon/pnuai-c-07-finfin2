package apptive.fin.apicollector.bankurl;

import com.microsoft.playwright.BrowserContext;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Component
public class ImBankScraper extends AbstractBankProductScraper {

    private static final String SEARCH_URL =
            "https://www.imbank.co.kr/dcz_ebz_10010_0010.act?kwd={q}";

    @Override
    public String providerCode() {
        return "0010016";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("imbank.co.kr");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        List<ProductCandidate> candidates = searchPages(
                context, productName, List.of(SEARCH_URL), this::extractProducts, false
        );
        if (candidates.isEmpty()) {
            return candidates;
        }
        String directlyNavigableSearchUrl = SEARCH_URL.replace(
                "{q}", URLEncoder.encode(productName, StandardCharsets.UTF_8)
        );
        return candidates.stream()
                .map(candidate -> new ProductCandidate(candidate.name(), directlyNavigableSearchUrl))
                .toList();
    }

    @Override
    protected double settleMillis() {
        return 5_000;
    }
}
