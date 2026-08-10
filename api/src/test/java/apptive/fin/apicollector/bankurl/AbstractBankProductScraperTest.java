package apptive.fin.apicollector.bankurl;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractBankProductScraperTest {

    @Test
    void extractsOnlyNamedProductLinks() {
        TestScraper scraper = new TestScraper();

        List<ProductCandidate> candidates = scraper.extractProducts(
                Jsoup.parse("""
                        <a href="/deposit/1">테스트 정기예금</a>
                        <a href="/manual.pdf">상품설명서</a>
                        <a href="/deposit">예금</a>
                        """, "https://bank.example"),
                "https://bank.example"
        );

        assertThat(candidates).containsExactly(
                new ProductCandidate("테스트 정기예금", "https://bank.example/deposit/1")
        );
    }

    @Test
    void extractsProductNameAndJavascriptUrlFromProductBlock() {
        TestScraper scraper = new TestScraper();

        List<ProductCandidate> candidates = scraper.extractProducts(
                Jsoup.parse("""
                        <ul class="product_list"><li>
                          <p class="product_tit"><strong>iM함께예금</strong></p>
                          <a href="javascript:goProductDetailByPdCd('10511008001166004', '', '', 'D','V');">
                            <span>상세보기</span>
                          </a>
                        </li></ul>
                        """, "https://www.imbank.co.kr/search"),
                "https://www.imbank.co.kr/search"
        );

        assertThat(candidates).containsExactly(new ProductCandidate(
                "iM함께예금",
                "https://www.imbank.co.kr/com_ebz_fpm_main.act?pd_cd=10511008001166004"
        ));
    }

    private static class TestScraper extends AbstractBankProductScraper {

        @Override
        public String providerCode() {
            return "TEST";
        }

        @Override
        public Set<String> allowedDomains() {
            return Set.of("bank.example");
        }

        @Override
        protected List<ProductCandidate> search(BrowserContext context, String productName) {
            return List.of();
        }
    }
}
