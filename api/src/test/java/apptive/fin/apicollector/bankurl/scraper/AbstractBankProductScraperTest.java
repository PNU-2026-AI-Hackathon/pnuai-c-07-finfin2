package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractBankProductScraperTest {

    @Test
    void usesPageTitleAndFallsBackToCandidateOnlyWhenBlank() {
        // 후보명은 대상명과 닮아서 뽑힌 것이라, 페이지 제목 대신 쓰면 검증이 자기 자신을 확인하게 된다.
        assertThat(AbstractBankProductScraper.chooseTitle("실제 상세 제목", "후보명")).isEqualTo("실제 상세 제목");
        assertThat(AbstractBankProductScraper.chooseTitle("", "후보명")).isEqualTo("후보명");
        assertThat(AbstractBankProductScraper.chooseTitle("   ", "후보명")).isEqualTo("후보명");
        assertThat(AbstractBankProductScraper.chooseTitle(null, "후보명")).isEqualTo("후보명");
    }

    @Test
    void skipsProductBlocksWithoutRealLink() {
        // 링크 없는 블록에 목록 페이지 조각 URL 을 만들어 붙이면, 같은 도메인이라 검증을 통과해
        // 목록 페이지가 상품 URL 로 저장된다. 후보로 만들지 않는 것이 맞다.
        TestScraper scraper = new TestScraper();

        List<ProductCandidate> candidates = scraper.extractProducts(
                Jsoup.parse("""
                        <ul class="product_list">
                          <li><h3>링크없는 정기예금</h3></li>
                          <li><h3>링크있는 정기적금</h3><a href="/deposit/2">보기</a></li>
                        </ul>
                        """, "https://bank.example/list"),
                "https://bank.example/list"
        );

        assertThat(candidates).extracting(ProductCandidate::url)
                .allSatisfy(url -> assertThat(url).doesNotContain("#product-"));
        assertThat(candidates).extracting(ProductCandidate::name)
                .doesNotContain("링크없는 정기예금");
    }

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
