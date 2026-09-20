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

@Component
public class BusanBankScraper extends AbstractBankProductScraper {

    private static final String LIST_URL =
            "https://www.busanbank.co.kr/ib20/mnu/FPMRDP001001001#SCH_RESULT_CNT";
    private static final String DETAIL_URL =
            "https://www.busanbank.co.kr/ib20/mnu/FPMDPO012001002";

    @Override
    public String providerCode() {
        return "0010017";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("busanbank.co.kr");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        return searchPages(context, productName, List.of(LIST_URL), this::extractProductList, true)
                .stream()
                .filter(candidate -> similarity.score(productName, candidate.name()) >= 0.55)
                .toList();
    }

    @Override
    protected void trySearchOnPage(Page page, String productName) {
        String cleaned = cleanText(productName);
        String stripped = cleaned.replaceFirst("(?i)^BNK\\s*", "");
        String shortened = stripped.replaceFirst("\\s*(예금|적금|통장)$", "");
        for (String query : List.of(cleaned, stripped, shortened).stream().distinct().toList()) {
            try {
                Locator input = page.locator("#INQ_CNTN").first();
                if (input.count() == 0) {
                    break;
                }
                input.fill(query);
                page.locator("#btn_search").first().click();
                settle(page);
                if (!Jsoup.parse(page.content()).select("a.FPCD_DTL[fpcd]").isEmpty()) {
                    return;
                }
            } catch (PlaywrightException ignored) {
                // Try the next query variant.
            }
        }
        super.trySearchOnPage(page, cleaned);
    }

    List<ProductCandidate> extractProductList(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (Element anchor : document.select("a.FPCD_DTL[fpcd]")) {
            String name = cleanProductName(anchor.text());
            String code = cleanText(anchor.attr("fpcd"));
            if (!code.isBlank() && looksLikeProductName(name)) {
                candidates.add(new ProductCandidate(name, DETAIL_URL + "?FPCD=" + code));
            }
        }
        for (Element anchor : document.select("a.goFpcd[data-value]")) {
            String code = cleanText(anchor.attr("data-value"));
            Element nameElement = anchor.selectFirst(".slide-txt");
            String name = cleanProductName(nameElement == null ? "" : nameElement.text());
            if (name.isBlank()) {
                Element title = anchor.selectFirst(".name");
                Element description = anchor.selectFirst(".desc");
                name = cleanProductName((title == null ? "" : title.text()) + " "
                        + (description == null ? "" : description.text()));
            }
            if (!code.isBlank() && looksLikeProductName(name)) {
                candidates.add(new ProductCandidate(name, DETAIL_URL + "?FPCD=" + code));
            }
        }
        return dedupe(candidates);
    }

    private String cleanProductName(String value) {
        return cleanText(value)
                .replaceAll("\\([^)]*판매중단[^)]*\\)", "")
                .replaceAll("\\s+(예금|적금|통장)", "$1")
                .replaceAll("\\s+", " ")
                .trim();
    }

    @Override
    protected double settleMillis() {
        return 4_000;
    }
}
