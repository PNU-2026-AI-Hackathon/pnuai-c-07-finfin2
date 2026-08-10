package apptive.fin.apicollector.bankurl;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class IbkBankScraper extends AbstractBankProductScraper {

    private static final String SEARCH_URL =
            "https://www.ibk.co.kr/jsp/search/search.jsp?equery={q}";

    @Override
    public String providerCode() {
        return "0010026";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("ibk.co.kr");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (String query : ibkQueryVariants(productName)) {
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
        for (Element section : document.select(".section_txt")) {
            Element heading = section.selectFirst("h4");
            if (heading == null || !heading.text().contains("금융상품")) {
                continue;
            }
            for (Element anchor : section.select(".result_mall a[onclick]")) {
                String name = cleanText(anchor.text());
                String url = urlFromGoBanking(anchor.attr("onclick"));
                if (looksLikeProductName(name) && !url.isBlank()) {
                    candidates.add(new ProductCandidate(name, url));
                }
            }
        }
        return dedupe(candidates);
    }

    String urlFromGoBanking(String onclick) {
        Matcher call = Pattern.compile("gobanking_url\\((.*)\\)\\s*;?$").matcher(onclick);
        if (!call.find()) {
            return "";
        }
        Matcher arguments = Pattern.compile("['\"]([^'\"]*)['\"]").matcher(call.group(1));
        List<String> values = new ArrayList<>();
        while (arguments.find()) {
            values.add(arguments.group(1));
        }
        if (values.size() < 4) {
            return "";
        }
        String detail = values.get(0);
        String processCode = values.get(1);
        String wvcd = values.get(3);
        String productName = values.size() >= 5 ? values.get(4) : "";
        if (processCode.length() < 11 || !detail.startsWith("/uib/")) {
            return "";
        }
        return "https://mybank.ibk.co.kr" + detail
                + "?lncd=" + processCode.substring(0, 2)
                + "&grcd=" + processCode.substring(2, 4)
                + "&tmcd=" + processCode.substring(4, 7)
                + "&pdcd=" + processCode.substring(7, 11)
                + "&wvcd=" + wvcd
                + "&i_trns_biz_kncd=" + URLEncoder.encode(productName, StandardCharsets.UTF_8);
    }

    private List<String> ibkQueryVariants(String productName) {
        List<String> variants = new ArrayList<>(queryVariants(productName));
        String withoutParentheses = productName.replaceAll("\\([^)]*\\)", "");
        String withoutIbk = withoutParentheses.replaceFirst("(?i)^IBK\\s*", "");
        String withoutSuffix = withoutIbk.replaceAll(
                "(자유적립식|정액적립식|정기예금|실세금리정기예금)", ""
        );
        variants.add(withoutIbk);
        variants.add(withoutSuffix);
        variants.add(withoutIbk.replaceAll("\\s+", ""));
        variants.add(withoutSuffix.replaceAll("\\s+", ""));
        return variants.stream().map(this::cleanText).filter(value -> !value.isBlank()).distinct().toList();
    }
}
