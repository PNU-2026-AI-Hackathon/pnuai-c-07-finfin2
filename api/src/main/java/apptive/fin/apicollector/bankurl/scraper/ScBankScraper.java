package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.RequestOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ScBankScraper extends AbstractBankProductScraper {

    private static final String SEARCH_URL =
            "https://www.standardchartered.co.kr/np/kr/sn/inc/TotalSearch.jsp";

    @Override
    public String providerCode() {
        return "0010002";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("standardchartered.co.kr");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (String query : queryVariants(productName)) {
            String form = "checkArr=total%2Cproduct%2Cbiz%2Cmenu%2Cfaq%2Cnews%2Cdoc%2Cwebpage%2Cbranch%2Cbanking"
                    + "&orderSet=weight&pageSn=1&cate=total&query=" + quoteEucKr(query);
            APIResponse response = context.request().post(SEARCH_URL, RequestOptions.create()
                    .setData(form)
                    .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=EUC-KR")
                    .setHeader("User-Agent", "Mozilla/5.0 api-collector/1.0")
                    .setTimeout(30_000));
            String html = new String(response.body(), StandardCharsets.UTF_8);
            candidates.addAll(extractSearchResults(Jsoup.parse(html, SEARCH_URL), SEARCH_URL));
        }
        return dedupe(candidates);
    }

    List<ProductCandidate> extractSearchResults(Document document, String currentUrl) {
        return extractProductsWithSelectors(
                document,
                currentUrl,
                List.of(
                        ".search-result li", ".result-list li", ".list-con-area", ".grid li",
                        ".product-list li", "dt", "li"
                ),
                List.of(".title a", ".tit a", ".tit", ".name a", ".name", "dt a", "strong a", "strong", "a"),
                true
        );
    }

    String quoteEucKr(String value) {
        byte[] bytes = value.getBytes(Charset.forName("EUC-KR"));
        StringBuilder encoded = new StringBuilder(bytes.length * 3);
        for (byte valueByte : bytes) {
            encoded.append("%%%02X".formatted(valueByte & 0xff));
        }
        return encoded.toString();
    }
}
