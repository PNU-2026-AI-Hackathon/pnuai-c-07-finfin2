package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.RequestOptions;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class KwangjuBankScraper extends AbstractBankProductScraper {

    private static final String LIST_URL = "https://www.kjbank.com/ib20/mnu/FPMDPTR030001";
    private static final String MOBILE_API =
            "https://m.kjbank.com/mweb/api/mobile/s/bms/adapter/JexAdapter/PrdList.biz";
    private static final Map<String, String> KIND_BY_TYPE = Map.of(
            "100", "deposit",
            "101", "saving",
            "102", "installment_01"
    );

    private final ObjectMapper objectMapper;

    public KwangjuBankScraper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String providerCode() {
        return "0010019";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("kjbank.com");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        List<ProductCandidate> candidates = new ArrayList<>(searchMobileProducts(context));
        candidates.addAll(searchPages(
                context, productName, List.of(LIST_URL), this::extractProductList, false
        ));
        return dedupe(candidates);
    }

    List<ProductCandidate> extractProductList(Document document, String currentUrl) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (Element anchor : document.select("a.btn_guide[data-idx]")) {
            String index = cleanText(anchor.attr("data-idx"));
            String name = cleanText(anchor.text());
            if (index.isBlank() || !looksLikeProductName(name)) {
                continue;
            }
            Map<String, String> parameters = new LinkedHashMap<>();
            parameters.put("INBN_GDS_NO", hiddenValue(document, "INBN_GDS_NO_" + index));
            parameters.put("INBN_GDS_CLCD", hiddenValue(document, "INBN_GDS_CLCD_" + index));
            parameters.put("INBN_GDS_ATRB_CD", hiddenValue(document, "INBN_GDS_ATRB_CD_" + index));
            if (!parameters.get("INBN_GDS_NO").isBlank()) {
                candidates.add(new ProductCandidate(
                        name,
                        absoluteUrl("/ib20/mnu/FPMDPTR030100?" + queryString(parameters), currentUrl)
                ));
            }
        }
        return dedupe(candidates);
    }

    List<ProductCandidate> extractMobileProducts(JsonNode payload) {
        List<ProductCandidate> candidates = new ArrayList<>();
        JsonNode products = payload.path("PRD_LIST");
        if (!products.isArray()) {
            return candidates;
        }
        for (JsonNode product : products) {
            String name = text(product, "SMRT_BNKN_GDS_NM");
            String pick = text(product, "SMRT_BNKN_PCK_GDS_CD");
            if (name.isBlank() || pick.isBlank()) {
                continue;
            }
            Map<String, String> parameters = new LinkedHashMap<>();
            parameters.put("pick", pick);
            parameters.put("kind", KIND_BY_TYPE.getOrDefault(text(product, "SMRT_BNKN_GDS_TYCD"), ""));
            parameters.put("prdCd", text(product, "SMRT_BNKN_GDS_CD"));
            parameters.put("hostGdsCd", text(product, "SMRT_BNKN_HOST_GDS_CD"));
            candidates.add(new ProductCandidate(
                    name,
                    "https://m.kjbank.com/mweb/spa/goodsDetail/?" + queryString(parameters)
            ));
        }
        return dedupe(candidates);
    }

    private List<ProductCandidate> searchMobileProducts(BrowserContext context) {
        List<ProductCandidate> candidates = new ArrayList<>();
        for (String type : List.of("100", "101", "102")) {
            try {
                String body = "{\"jexId\":\"PrdList\",\"NFF_INFL_PATH_CD\":\"03\","
                        + "\"ORDER_DVCD\":\"regDt\",\"SMRT_BNKN_GDS_TYCD\":\"" + type
                        + "\",\"GDS_CMPR_UZ_YN\":\"\"}";
                APIResponse response = context.request().post(MOBILE_API, RequestOptions.create()
                        .setData(body)
                        .setHeader("Accept", "application/json")
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Referer", "https://m.kjbank.com/mweb/main/products")
                        .setTimeout(30_000));
                candidates.addAll(extractMobileProducts(objectMapper.readTree(response.text())));
            } catch (RuntimeException ignored) {
                // The desktop list remains as a fallback.
            }
        }
        return dedupe(candidates);
    }

    private String hiddenValue(Document document, String id) {
        Element element = document.getElementById(id);
        return cleanText(element == null ? "" : element.attr("value"));
    }

    private String text(JsonNode node, String field) {
        return cleanText(node.path(field).asString(""));
    }

    private String queryString(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .filter(entry -> !entry.getValue().isBlank())
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
