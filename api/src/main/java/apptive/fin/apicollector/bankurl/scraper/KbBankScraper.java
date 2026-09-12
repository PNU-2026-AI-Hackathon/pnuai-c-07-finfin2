package apptive.fin.apicollector.bankurl.scraper;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class KbBankScraper extends AbstractBankProductScraper {

    private static final String SEARCH_URL = "https://obank.kbstar.com/quics?page=C016528";
    private static final String RESULT_SELECTOR = "div.area1 a.title";
    private static final Pattern PRODUCT_CODE_PATTERN = Pattern.compile(
            "productDtlSear\\(\\s*['\"]([^'\"]+)"
    );

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
                Locator input = page.locator("#searchWord:visible").first();
                if (input.count() == 0) {
                    continue;
                }
                List<String> previousResults = page.locator(RESULT_SELECTOR).allTextContents();
                input.fill(query);
                Locator searchButton = page.locator(".btn-icon1.ic3:visible").first();
                if (searchButton.count() > 0) {
                    searchButton.click();
                } else {
                    input.press("Enter");
                }
                try {
                    page.waitForFunction("""
                                    previous => {
                                      const current = Array.from(document.querySelectorAll('div.area1 a.title'))
                                        .map(anchor => anchor.textContent.trim());
                                      return current.length > 0
                                        && JSON.stringify(current) !== JSON.stringify(previous);
                                    }
                                    """,
                            previousResults,
                            new Page.WaitForFunctionOptions().setTimeout(10_000));
                } catch (PlaywrightException ignored) {
                    // Extract any results that were rendered before the timeout.
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
        for (Element row : document.select("#procList li,.procList li,div.area1")) {
            String name = bestName(row, List.of("a.title", "strong > a", "strong", ".tit a", ".tit", "a"));
            if (!looksLikeProductName(name)) {
                continue;
            }
            for (Element anchor : row.select("a")) {
                String url = kbProductUrl(anchor, currentUrl);
                if (!url.isBlank()) {
                    candidates.add(new ProductCandidate(name, url));
                    break;
                }
            }
        }
        return dedupe(candidates);
    }

    private String kbProductUrl(Element anchor, String currentUrl) {
        Matcher matcher = PRODUCT_CODE_PATTERN.matcher(anchor.attr("onclick"));
        if (matcher.find()) {
            return absoluteUrl("/quics?page=C016613&prcode=" + matcher.group(1), currentUrl);
        }
        return urlFromAnchor(anchor, currentUrl);
    }
}
