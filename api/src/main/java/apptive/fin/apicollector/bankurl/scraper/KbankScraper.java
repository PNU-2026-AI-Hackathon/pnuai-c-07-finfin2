package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class KbankScraper extends AbstractBankProductScraper {

    private static final String LIST_URL =
            "https://www.kbanknow.com/web/product/info/list?tab=deposit";
    private static final Map<String, String> NAMES_BY_PATH = new LinkedHashMap<>();

    static {
        NAMES_BY_PATH.put("/web/product/deposit/rolling-farm", "데굴데굴농장");
        NAMES_BY_PATH.put("/web/product/deposit/mykids-saving", "마이키즈 적금");
        NAMES_BY_PATH.put("/web/product/deposit/codek-fixed", "코드K 정기예금");
        NAMES_BY_PATH.put("/web/product/deposit/curious-saving", "궁금한 적금");
        NAMES_BY_PATH.put("/web/product/deposit/primary-saving", "주거래우대 자유적금");
        NAMES_BY_PATH.put("/web/product/deposit/codek-saving", "코드K 자유적금");
    }

    @Override
    public String providerCode() {
        return "0014674";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("kbanknow.com");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        return searchPages(context, productName, List.of(LIST_URL), this::extractProductLinks, false);
    }

    List<ProductCandidate> extractProductLinks(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (Element anchor : document.select("a[href*=/web/product/]")) {
            String url = urlFromAnchor(anchor, currentUrl);
            String name = nameFromUrl(url);
            if (name.isBlank()) {
                name = cleanText(anchor.text());
            }
            if (!url.isBlank() && (looksLikeProductName(name) || NAMES_BY_PATH.containsValue(name))) {
                candidates.add(new ProductCandidate(name, url));
            }
        }
        return dedupe(candidates);
    }

    private String nameFromUrl(String url) {
        return NAMES_BY_PATH.entrySet().stream()
                .filter(entry -> url.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("");
    }

    @Override
    protected double settleMillis() {
        return 3_000;
    }
}
