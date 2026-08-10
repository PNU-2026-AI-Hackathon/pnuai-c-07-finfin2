package apptive.fin.apicollector.bankurl;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class NhBankScraper extends AbstractBankProductScraper {

    private static final String SEARCH_URL =
            "https://smartmarket.nonghyup.com/servlet/BFBCW2021R.view?query={q}";
    private static final String LIST_URL =
            "https://smartmarket.nonghyup.com/servlet/BFDCW1021R.view";
    private static final String MINI_SAVINGS_CODE = "1004713600002";

    @Override
    public String providerCode() {
        return "0013175";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("nonghyup.com");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (String query : queryVariants(productName)) {
            candidates.addAll(searchPages(
                    context, query, List.of(SEARCH_URL), this::extractSearchResults, false
            ));
        }
        candidates.addAll(searchPages(
                context, productName, List.of(LIST_URL), this::extractSearchResults, true
        ));
        List<ProductCandidate> result = dedupe(candidates);
        if (productName.contains("미니")) {
            return result.stream().filter(candidate -> candidate.name().contains("미니")).toList();
        }
        return result;
    }

    List<ProductCandidate> extractSearchResults(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>(extractProductsWithSelectors(
                document,
                currentUrl,
                List.of(".subject_product_li > li", ".product_deposit_wrap li"),
                List.of("dt a", "dt", ".subject_product_text a"),
                false
        ));
        String html = document.html();
        if (html.contains(MINI_SAVINGS_CODE) || html.contains("올원e미니적금") || html.contains("미니적금")) {
            candidates.add(new ProductCandidate(
                    "NH올원e미니적금",
                    LIST_URL + "?detailPsnFncWrsC=" + MINI_SAVINGS_CODE
                            + "&psnFncWrsC=" + MINI_SAVINGS_CODE
                            + "&listServiceId=BFDCW1011R"
            ));
        }
        return dedupe(candidates);
    }
}
