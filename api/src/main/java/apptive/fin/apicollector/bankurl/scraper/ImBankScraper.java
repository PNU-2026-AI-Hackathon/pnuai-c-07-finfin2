package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Component
public class ImBankScraper extends AbstractBankProductScraper {

    // 상품 상세 화면은 내부 AJAX가 서버 세션에 FNM_DETAIL_DATA를 저장한 뒤 공통 프레임을 연다.
    // pd_cd를 붙인 직접 GET은 빈 화면이므로, 바로 열 수 있는 상품 검색 결과 URL을 저장한다.
    private static final String SEARCH_URL =
            "https://www.imbank.co.kr/dcz_ebz_10010_0010.act?kwd={q}&category=PRODUCT";

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
        List<ProductCandidate> candidates = searchPages(
                context, productName, List.of(SEARCH_URL), this::extractProducts, false
        );
        if (candidates.isEmpty()) {
            return candidates;
        }
        String directlyNavigableSearchUrl = SEARCH_URL.replace(
                "{q}", URLEncoder.encode(productName, StandardCharsets.UTF_8)
        );
        return candidates.stream()
                .map(candidate -> new ProductCandidate(candidate.name(), directlyNavigableSearchUrl))
                .toList();
    }

    @Override
    protected double settleMillis() {
        return 5_000;
    }
}
