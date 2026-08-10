package apptive.fin.apicollector.bankurl;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class HanaBankScraper extends AbstractBankProductScraper {

    private static final String SEARCH_URL =
            "https://www.kebhana.com/cont/search/search_total.jsp?query={q}&collection=ALL";

    @Override
    public String providerCode() {
        return "0013909";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("kebhana.com");
    }

    @Override
    protected List<String> titleSelectors() {
        return List.of(".product-title", ".productView h3", ".prd-title", "h1", "h2.tit", "h2");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (String query : hanaQueryVariants(productName)) {
            candidates.addAll(searchPages(
                    context, query, List.of(SEARCH_URL), this::extractSearchResults, false
            ));
            if (candidates.stream().anyMatch(candidate -> similarity.score(productName, candidate.name()) >= 0.80)) {
                break;
            }
        }
        return dedupe(candidates);
    }

    List<ProductCandidate> extractSearchResults(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (Element block : document.select(".resultDiv .productInfo")) {
            Element anchor = block.selectFirst("h5 a[href]");
            if (anchor == null) {
                continue;
            }
            String name = cleanText(anchor.text());
            String url = absoluteUrl(anchor.attr("href"), currentUrl);
            if (looksLikeProductName(name) && !url.isBlank()) {
                candidates.add(new ProductCandidate(name, url));
            }
        }
        return dedupe(candidates);
    }

    List<String> hanaQueryVariants(String productName) {
        List<String> result = new ArrayList<>(queryVariants(productName));
        String spaced = productName
                .replace("의정기", "의 정기")
                .replace("의적금", "의 적금")
                .replace("하나정기", "하나 정기");
        if (!spaced.equals(productName)) {
            result.add(1, cleanText(spaced));
        }
        return result.stream().distinct().toList();
    }
}
