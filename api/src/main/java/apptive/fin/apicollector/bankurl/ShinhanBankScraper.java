package apptive.fin.apicollector.bankurl;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.RequestOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ShinhanBankScraper extends AbstractBankProductScraper {

    private static final String SEARCH_URL = "https://bank.shinhan.com/index.jsp#020105010000";
    private static final String SITEMAP_URL = "https://m.shinhan.com/sitemap.xml";

    @Override
    public String providerCode() {
        return "0011625";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("shinhan.com");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        Map<String, String> urlsByProductCode = fetchSitemapUrls(context);
        List<ProductCandidate> candidates = new ArrayList<>();
        try (Page page = context.newPage()) {
            navigate(page, SEARCH_URL);
            tryProductSearch(page, productName);
            settle(page);
            for (PageContent content : pageContents(page)) {
                for (ProductCandidate codeCandidate : extractProductCodes(
                        Jsoup.parse(content.html(), content.url()), content.url()
                )) {
                    String url = urlsByProductCode.getOrDefault(
                            codeCandidate.url(), fallbackMobileUrl(codeCandidate.url(), codeCandidate.name())
                    );
                    candidates.add(new ProductCandidate(codeCandidate.name(), url));
                }
            }
        }
        return dedupe(candidates);
    }

    List<ProductCandidate> extractProductCodes(Document document, String currentUrl) {
        List<ProductCandidate> result = new ArrayList<>();
        for (Element block : document.select(".listTyProducts > li")) {
            String name = bestName(block, List.of(".prdtName a", ".prdtName"));
            String productCode = textByPartialId(block, "상품코드");
            if (looksLikeProductName(name) && !productCode.isBlank()) {
                result.add(new ProductCandidate(name, productCode));
            }
        }
        return result;
    }

    private Map<String, String> fetchSitemapUrls(BrowserContext context) {
        Map<String, String> result = new HashMap<>();
        try {
            APIResponse response = context.request().get(
                    SITEMAP_URL, RequestOptions.create().setTimeout(30_000)
            );
            Document document = Jsoup.parse(response.text(), "", Parser.xmlParser());
            for (Element location : document.select("loc")) {
                String url = cleanText(location.text());
                String productCode = queryValue(url, "pid");
                if (productCode.isBlank()) {
                    continue;
                }
                if (!result.containsKey(productCode) || "now".equals(queryValue(url, "type"))) {
                    result.put(productCode, url);
                }
            }
        } catch (RuntimeException ignored) {
            // Fallback URLs are generated from product codes when the sitemap is unavailable.
        }
        return result;
    }

    private void tryProductSearch(Page page, String productName) {
        try {
            Locator input = page.locator("#tbx_상품검색어").first();
            if (input.count() == 0) {
                return;
            }
            input.fill(productName);
            Locator button = page.locator("#btn_검색").first();
            if (button.count() > 0) {
                button.click();
            } else {
                input.press("Enter");
            }
        } catch (PlaywrightException ignored) {
            // Empty result is handled by the shared service.
        }
    }

    private String textByPartialId(Element block, String partialId) {
        for (Element element : block.select("[id]")) {
            if (element.id().contains(partialId)) {
                String text = cleanText(element.text());
                if (!text.isBlank()) {
                    return text;
                }
            }
        }
        return "";
    }

    private String fallbackMobileUrl(String productCode, String name) {
        String pageCode = name.contains("적금") ? "PR0301S0100F01" : "PR0401S0000F01";
        return "https://m.shinhan.com/mw/fin/pg/" + pageCode
                + "?pid=" + productCode + "&type=now&hwno=";
    }

    private String queryValue(String url, String key) {
        Matcher matcher = Pattern.compile("[?&]" + Pattern.quote(key) + "=([^&]+)").matcher(url);
        return matcher.find() ? matcher.group(1) : "";
    }

    @Override
    protected double settleMillis() {
        return 3_000;
    }
}
