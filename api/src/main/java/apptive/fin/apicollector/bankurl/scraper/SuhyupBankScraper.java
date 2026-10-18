package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SuhyupBankScraper extends AbstractBankProductScraper {

    private static final String LIST_URL = "https://m.suhyup-bank.com/ib20/mnu/WBK00172";
    private static final String DETAIL_URL = "https://www.suhyup-bank.com/ib20/mnu/FPD00118/"
            + "_menuId/FPD00124/_productCode/";

    @Override
    public String providerCode() {
        return "0014807";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("suhyup-bank.com");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        return searchPages(
                context, productName, List.of(LIST_URL), this::extractMobileProducts, false
        );
    }

    List<ProductCandidate> extractMobileProducts(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();
        for (Element item : document.select("li.item[data-prodcd]")) {
            String code = cleanText(item.attr("data-prodcd"));
            Element nameElement = item.selectFirst(".pdt-nm");
            String name = cleanText(nameElement == null ? "" : nameElement.text());
            if (code.isBlank() || "deduction".equals(code) || !seenCodes.add(code)
                    || !looksLikeProductName(name)) {
                continue;
            }
            candidates.add(new ProductCandidate(name, DETAIL_URL + code));
        }
        return dedupe(candidates);
    }

    @Override
    protected double settleMillis() {
        return 2_000;
    }
}
