package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class KdbBankScraper extends AbstractBankProductScraper {

    private static final List<String> URLS = List.of(
            "https://www.kdb.co.kr/CBADTS38N01.act?query={q}",
            "https://banking.kdb.co.kr/bp/BMDEWP01N10.act#__init__"
    );
    private static final Map<String, String> KNOWN_PRODUCTS = new LinkedHashMap<>();

    static {
        KNOWN_PRODUCTS.put("KDB 정기예금", "100237000101");
        KNOWN_PRODUCTS.put("KDB 자유적금", "100238000101");
        KNOWN_PRODUCTS.put("KDB 기업정기적금", "100239000101");
    }

    @Override
    public String providerCode() {
        return "0010030";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("kdb.co.kr");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        return searchPages(context, productName, URLS, this::extractProducts, false);
    }

    @Override
    protected List<ProductCandidate> extractProducts(Document document, String currentUrl) {
        String html = document.html();
        List<ProductCandidate> candidates = new ArrayList<>();
        KNOWN_PRODUCTS.forEach((name, code) -> {
            if (html.contains(code) || html.contains(name)) {
                candidates.add(new ProductCandidate(name, detailUrl(code)));
            }
        });
        Matcher matcher = Pattern.compile(
                "PROD_C\\s*=\\s*['\"](\\d+)['\"];.*?PROD_NM\\s*=\\s*['\"]([^'\"]+)['\"]",
                Pattern.DOTALL
        ).matcher(html);
        while (matcher.find()) {
            String name = cleanText(matcher.group(2));
            if (looksLikeProductName(name)) {
                candidates.add(new ProductCandidate(name, detailUrl(matcher.group(1))));
            }
        }
        return dedupe(candidates);
    }

    private String detailUrl(String code) {
        return "https://banking.kdb.co.kr/bp/BMDEWP01N10.act?PRD_C=" + code + "#prd=" + code;
    }
}
