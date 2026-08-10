package apptive.fin.apicollector.bankurl;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class KbBankScraper extends AbstractBankProductScraper {

    private static final String SEARCH_URL = "https://obank.kbstar.com/quics?page=C111087";

    @Override
    public String providerCode() {
        return "0010927";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("kbstar.com");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (String query : queryVariants(productName)) {
            try (Page page = context.newPage()) {
                navigate(page, SEARCH_URL);
                Locator input = page.locator("#keyword").first();
                if (input.count() > 0) {
                    input.fill(query);
                    input.press("Enter");
                    settle(page);
                }
                Locator productTab = page.locator("#tb3").first();
                if (productTab.count() > 0) {
                    try {
                        productTab.click();
                        settle(page);
                    } catch (PlaywrightException ignored) {
                        // Results can already be visible without opening the tab.
                    }
                }
                for (PageContent content : pageContents(page)) {
                    candidates.addAll(extractSearchResults(
                            Jsoup.parse(content.html(), content.url()), content.url()
                    ));
                }
            }
            if (candidates.stream().anyMatch(candidate -> similarity.score(candidate.name(), productName) >= 0.85)) {
                break;
            }
        }
        return dedupe(candidates);
    }

    List<ProductCandidate> extractSearchResults(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (Element row : document.select("#procList li,.procList li,li")) {
            String name = bestName(row, List.of("strong > a", "strong", ".tit a", ".tit", "a"));
            if (!looksLikeProductName(name)) {
                continue;
            }
            for (Element anchor : row.select("a")) {
                String url = urlFromAnchor(anchor, currentUrl);
                if (!url.isBlank()) {
                    candidates.add(new ProductCandidate(name, url));
                    break;
                }
            }
        }
        return dedupe(candidates);
    }
}
