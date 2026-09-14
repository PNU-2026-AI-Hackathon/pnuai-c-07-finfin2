package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class WooriBankScraper extends AbstractBankProductScraper {

    private static final String SEARCH_URL = "https://spib.wooribank.com/pib/Dream?withyou=CMCOM0007"
            + "&collection=ALL&query={q}&realQuery={q}&searchField=ALL&sort=RANK&startCount=0";

    @Override
    public String providerCode() {
        return "0010001";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("wooribank.com");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        return searchPages(context, productName, List.of(SEARCH_URL), this::extractSearchResults, false);
    }

    List<ProductCandidate> extractSearchResults(Document document, String currentUrl) {
        return extractProductsWithSelectors(
                document,
                currentUrl,
                List.of(".searchResult li", ".resultList li", ".list li", "li"),
                List.of(".tit a", ".tit", "strong a", "strong", "a"),
                true
        );
    }
}
