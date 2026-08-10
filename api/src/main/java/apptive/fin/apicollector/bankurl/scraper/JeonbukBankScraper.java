package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.WaitUntilState;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class JeonbukBankScraper extends AbstractBankProductScraper {

    private static final String MOBILE_BASE = "https://m.jbbank.co.kr:8543";
    private static final List<String> LIST_URLS = List.of(
            MOBILE_BASE + "/JBN/P_M_SID_MALL",
            MOBILE_BASE + "/JBN/P_M_SVMN_MALL"
    );
    private static final Set<String> PRODUCT_APIS = Set.of(
            "EBCIB_NGDSB_M_R014.jct", "EBCIB_NGDSB_M_R001.jct"
    );

    private final ObjectMapper objectMapper;

    public JeonbukBankScraper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String providerCode() {
        return "0010022";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("jbbank.co.kr", "m.jbbank.co.kr");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        Map<String, ProductCandidate> products = new LinkedHashMap<>();
        try (Page page = context.newPage()) {
            page.onResponse(response -> collectProductResponse(response, products));
            for (String listUrl : LIST_URLS) {
                page.navigate(listUrl, new Page.NavigateOptions()
                        .setWaitUntil(WaitUntilState.NETWORKIDLE)
                        .setTimeout(40_000));
                page.waitForTimeout(3_000);
            }
        }
        return preferMatchingSavingsType(productName, List.copyOf(products.values()));
    }

    List<ProductCandidate> extractProducts(JsonNode payload) {
        Map<String, ProductCandidate> products = new LinkedHashMap<>();
        JsonNode rows = payload.path("GRID");
        if (!rows.isArray()) {
            rows = payload.path("REC");
        }
        if (!rows.isArray()) {
            return List.of();
        }
        for (JsonNode row : rows) {
            String name = text(row, "GDS_NM");
            String code = text(row, "GDS_CD");
            String detailCode = text(row, "GDS_DTLS_CD");
            String wholeCode = text(row, "GDS_WHOL_CD");
            if (name.isBlank() || code.isBlank() || wholeCode.isBlank()) {
                continue;
            }
            if (detailCode.isBlank()) {
                detailCode = "0000";
            }
            String url = MOBILE_BASE + "/JBbank.act?TRGT_URL=P_M_SID_MALL_DTL&TRGT_PARAM="
                    + targetParameter(code, detailCode, wholeCode);
            products.putIfAbsent(name, new ProductCandidate(name, url));
        }
        return List.copyOf(products.values());
    }

    List<ProductCandidate> preferMatchingSavingsType(
            String productName,
            List<ProductCandidate> candidates
    ) {
        if (productName.contains("정액적립")) {
            List<ProductCandidate> preferred = candidates.stream()
                    .filter(candidate -> candidate.name().contains("정액적립"))
                    .toList();
            return preferred.isEmpty() ? candidates : preferred;
        }
        if (productName.contains("자유적립")) {
            List<ProductCandidate> preferred = candidates.stream()
                    .filter(candidate -> candidate.name().contains("자유적립"))
                    .toList();
            return preferred.isEmpty() ? candidates : preferred;
        }
        return candidates;
    }

    private void collectProductResponse(Response response, Map<String, ProductCandidate> products) {
        String endpoint = response.url().replaceAll(".*[/]", "").replaceAll("[?].*", "");
        String contentType = response.headers().getOrDefault("content-type", "");
        if (!response.url().contains("jbbank") || !contentType.contains("json")
                || !PRODUCT_APIS.contains(endpoint)) {
            return;
        }
        try {
            for (ProductCandidate candidate : extractProducts(objectMapper.readTree(response.text()))) {
                products.putIfAbsent(candidate.name(), candidate);
            }
        } catch (RuntimeException ignored) {
            // Another product API response can still provide the list.
        }
    }

    private String targetParameter(String code, String detailCode, String wholeCode) {
        String json = "{\"GDS_CD\":\"" + code + "\",\"GDS_DTLS_CD\":\"" + detailCode
                + "\",\"GDS_WHOL_CD\":\"" + wholeCode
                + "\",\"BDT_GDS_MGMT_NO\":\"\",\"KIND\":\"SID\",\"RCMD_YN\":\"N\","
                + "\"CUPN_YN\":\"N\",\"PAGE_NO\":1,\"PAGE_SIZE\":3}";
        String quoted = URLEncoder.encode(json, StandardCharsets.UTF_8).replace("+", "%20");
        return Base64.getEncoder().encodeToString(quoted.getBytes(StandardCharsets.UTF_8));
    }

    private String text(JsonNode node, String field) {
        return cleanText(node.path(field).asString(""));
    }

    @Override
    protected double settleMillis() {
        return 3_000;
    }
}
