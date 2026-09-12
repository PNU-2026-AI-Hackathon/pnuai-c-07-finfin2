package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
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
public class ImBankScraper extends AbstractBankProductScraper {

    private static final String PRODUCT_API =
            "https://www.imbank.co.kr/fnp_ebz_21010_depo_d001.jct";
    private static final String MOBILE_DETAIL_URL =
            "https://mbanking.imbank.co.kr/com_ebz_mbs_00001.act"
                    + "?svcId=fis_ebz_sbs_21030_depo&PD_CD=";

    private final ObjectMapper objectMapper;

    public ImBankScraper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String providerCode() {
        return "0010016";
    }

    @Override
    public Set<String> allowedDomains() {
        return Set.of("imbank.co.kr");
    }

    @Override
    protected List<ProductCandidate> search(BrowserContext context, String productName) {
        return extractProductsFromApi(requestProducts(context, productName));
    }

    String requestProducts(BrowserContext context, String productName) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("HMPG_PD_CLACD", "02");
        payload.put("MALL_INQ_DVCD", "1");
        payload.put("DPO_BPD_JN_PURP_CN", "99");
        payload.put("DPO_BPD_TRGET_AGE_CN", "99");
        payload.put("DPO_JN_AMT_DVCD", "99");
        payload.put("DPO_SVNG_PRID_DVCD", "99");
        payload.put("DPO_DV_INQ_CNT", 0);
        payload.put("PD_NM", productName);
        payload.put("VLD_VAL", "01");
        payload.put("SMRT_LNUP_DV", "favor");
        payload.put("EBZ_WEB_WORK_COMM", Map.of("INQ_SEQ", "1", "INQ_NCSE", 20));

        String encodedPayload = URLEncoder.encode(
                objectMapper.writeValueAsString(payload), StandardCharsets.UTF_8
        );
        APIResponse response = context.request().post(PRODUCT_API, RequestOptions.create()
                .setForm(FormData.create().set("_JSON_", encodedPayload))
                .setHeader("Accept", "application/json")
                .setHeader("Referer", "https://www.imbank.co.kr/district/fnp_ebz_21010_depo.act")
                .setTimeout(30_000));
        try {
            if (!response.ok()) {
                throw new IllegalStateException("iM Bank product API returned HTTP " + response.status());
            }
            return response.text();
        } finally {
            response.dispose();
        }
    }

    List<ProductCandidate> extractProductsFromApi(String responseBody) {
        List<ProductCandidate> candidates = new ArrayList<>();
        JsonNode products = objectMapper.readTree(responseBody).path("REC1");
        if (!products.isArray()) {
            return candidates;
        }
        for (JsonNode product : products) {
            String name = cleanText(product.path("PD_NM").asString(""));
            String code = cleanText(product.path("PD_CD").asString(""));
            if (!name.isBlank() && !code.isBlank()) {
                candidates.add(new ProductCandidate(name, MOBILE_DETAIL_URL + code));
            }
        }
        return dedupe(candidates);
    }

    @Override
    protected ScrapedProduct collect(BrowserContext context, ProductCandidate selected) {
        // pnp4web redirects automation browsers to warning.jsp. The URL is built only from
        // the official API's product code and was verified in a regular browser.
        return new ScrapedProduct(selected.name(), selected.url());
    }
}
