package apptive.fin.apicollector.bankurl;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class JejuBankScraper extends AbstractBankProductScraper {

    private static final List<String> URLS = List.of(
            "https://www.jejubank.co.kr/hmpg/intgSrch.do?intgSrchText={q}",
            "https://www.jejubank.co.kr/hmpg/prdGdnc/sid/mndp.do",
            "https://www.jejubank.co.kr/hmpg/prdGdnc/sid/indp.do"
    );

    @Override
    public String providerCode() {
        return "0010020";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("jejubank.co.kr");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (String query : jejuQueryVariants(productName)) {
            candidates.addAll(searchPages(context, query, URLS, this::extractBankProducts, false));
            if (candidates.stream().anyMatch(candidate -> compact(candidate.name()).contains(compact(productName)))) {
                break;
            }
        }
        List<ProductCandidate> preferred = preferMatchingInterestType(productName, dedupe(candidates));
        String target = compact(productName);
        return preferred.stream()
                .map(candidate -> target.isBlank() || !compact(candidate.name()).contains(target)
                        ? candidate
                        : new ProductCandidate(cleanText(productName), candidate.url()))
                .toList();
    }

    List<ProductCandidate> extractBankProducts(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>(extractProductsWithSelectors(
                document,
                currentUrl,
                List.of(".product_list li", ".product-list > li", ".list-con-area", ".result_list li"),
                List.of(".tit a", ".name a", "dt a", "strong a", ".tit", ".name", "strong"),
                false
        ));
        for (Element row : document.select("#fnncPrdTable tbody tr")) {
            Element nameElement = row.selectFirst("p.size18");
            Element anchor = row.selectFirst("a.view-btn[href]");
            String name = cleanText(nameElement == null ? "" : nameElement.text());
            String url = anchor == null ? "" : urlFromAnchor(anchor, currentUrl);
            if (!name.isBlank() && !url.isBlank()) {
                candidates.add(new ProductCandidate(name, url));
            }
        }
        return dedupe(candidates);
    }

    List<ProductCandidate> preferMatchingInterestType(
            String productName,
            List<ProductCandidate> candidates
    ) {
        String target = compact(productName);
        for (String marker : List.of("만기", "월이자", "선이자")) {
            if (!target.contains(marker)) {
                continue;
            }
            List<ProductCandidate> matching = candidates.stream()
                    .filter(candidate -> compact(candidate.name()).contains(marker))
                    .toList();
            if (!matching.isEmpty()) {
                return matching;
            }
        }
        return candidates;
    }

    private List<String> jejuQueryVariants(String productName) {
        List<String> variants = new ArrayList<>(queryVariants(productName));
        for (String variant : List.copyOf(variants)) {
            String compact = variant.replaceAll("[^0-9a-zA-Z가-힣]", "");
            variants.add(compact);
            variants.add(compact.toLowerCase(Locale.ROOT));
            variants.add(compact.replaceFirst("(?i)^jbank", ""));
        }
        return variants.stream().filter(value -> !value.isBlank()).distinct().toList();
    }

    private String compact(String value) {
        return cleanText(value).replaceAll("[^0-9a-zA-Z가-힣]", "").toLowerCase(Locale.ROOT);
    }
}
