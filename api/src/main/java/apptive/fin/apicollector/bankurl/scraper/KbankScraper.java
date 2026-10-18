package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class KbankScraper extends AbstractBankProductScraper {

    private static final String LIST_URL =
            "https://www.kbanknow.com/web/product/info/list?tab=deposit";

    private final ObjectMapper objectMapper;

    public KbankScraper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
        List<ProductCandidate> candidates = new ArrayList<>(extractStructuredProducts(document));
        for (Element anchor : document.select("a[href*=/web/product/deposit/]")) {
            String url = urlFromAnchor(anchor, currentUrl);
            String name = cleanText(anchor.text());
            if (!url.isBlank() && isProductLinkName(name)) {
                candidates.add(new ProductCandidate(name, url));
            }
        }
        return dedupe(candidates);
    }

    private boolean isProductLinkName(String name) {
        return !name.isBlank()
                && name.length() <= 90
                && !Set.of("상세보기", "자세히 보기").contains(name);
    }

    private List<ProductCandidate> extractStructuredProducts(Document document) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (Element script : document.select("script[type=application/ld+json]")) {
            try {
                collectFinancialProducts(objectMapper.readTree(script.data()), candidates);
            } catch (RuntimeException ignored) {
                // 유효한 JSON-LD가 없으면 기존 HTML 링크 추출 방식으로 대체한다.
            }
        }
        return candidates;
    }

    private void collectFinancialProducts(JsonNode node, List<ProductCandidate> candidates) {
        if ("FinancialProduct".equals(node.path("@type").asString(""))) {
            String name = cleanText(node.path("name").asString(""));
            String url = cleanText(node.path("url").asString(""));
            if (!name.isBlank() && !url.isBlank()) {
                candidates.add(new ProductCandidate(name, url));
            }
        }
        for (JsonNode child : node) {
            collectFinancialProducts(child, candidates);
        }
    }

    @Override
    protected double settleMillis() {
        return 3_000;
    }
}
