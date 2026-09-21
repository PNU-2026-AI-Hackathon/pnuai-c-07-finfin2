package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class KakaoBankScraper extends AbstractBankProductScraper {

    private static final String LIST_URL = "https://www.kakaobank.com/products/withdrawal";

    @Override
    public String providerCode() {
        return "0015130";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("kakaobank.com");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        return searchPages(context, productName, List.of(LIST_URL), this::extractProductLinks, false);
    }

    List<ProductCandidate> extractProductLinks(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (Element anchor : document.select("a[href*=/products/],a[href*=/p/]")) {
            String name = cleanText(anchor.text());
            String url = urlFromAnchor(anchor, currentUrl);
            if (looksLikeProductName(name) && !url.isBlank()) {
                candidates.add(new ProductCandidate(name, url));
            }
        }
        return dedupe(candidates);
    }
}
