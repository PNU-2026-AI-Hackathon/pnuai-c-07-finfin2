package apptive.fin.apicollector.bankurl.scraper;

import com.microsoft.playwright.BrowserContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

class InternetBankScrapersTest {

    @Test
    void kakaoExtractsProductLink() {
        var result = new KakaoBankScraper().extractProductLinks(Jsoup.parse("""
                <a href="/products/savings">카카오뱅크 자유적금</a>
                """), "https://www.kakaobank.com/products/withdrawal");

        assertThat(result).containsExactly(new ProductCandidate(
                "카카오뱅크 자유적금", "https://www.kakaobank.com/products/savings"
        ));
    }

    @Test
    void kbankUsesStructuredNameWhenLinkTextIsGeneric() {
        var result = new KbankScraper(new ObjectMapper()).extractProductLinks(Jsoup.parse("""
                <script type="application/ld+json">
                {
                  "@type": "FinancialProduct",
                  "name": "데굴데굴농장",
                  "url": "https://www.kbanknow.com/web/product/deposit/rolling-farm"
                }
                </script>
                <a href="/web/product/deposit/rolling-farm">자세히 보기</a>
                """), "https://www.kbanknow.com/list");

        assertThat(result).containsExactly(new ProductCandidate(
                "데굴데굴농장", "https://www.kbanknow.com/web/product/deposit/rolling-farm"
        ));
    }

    @Test
    void kbankExtractsChallengeBoxWithoutProductWord() {
        var result = new KbankScraper(new ObjectMapper()).extractProductLinks(Jsoup.parse("""
                <script type="application/ld+json">
                {
                  "@type": "FinancialProduct",
                  "name": "챌린지박스",
                  "url": "https://www.kbanknow.com/web/product/deposit/challengebox"
                }
                </script>
                """), "https://www.kbanknow.com/list");

        assertThat(result).containsExactly(new ProductCandidate(
                "챌린지박스", "https://www.kbanknow.com/web/product/deposit/challengebox"
        ));
    }

    @Test
    void kbankExtractsUnmappedProductFromStructuredData() {
        var result = new KbankScraper(new ObjectMapper()).extractProductLinks(Jsoup.parse("""
                <script type="application/ld+json">
                {
                  "@graph": [{
                    "@type": "ItemList",
                    "itemListElement": [{
                      "item": {
                        "@type": "FinancialProduct",
                        "name": "새로운박스",
                        "url": "https://www.kbanknow.com/web/product/deposit/new-box"
                      }
                    }]
                  }]
                }
                </script>
                """), "https://www.kbanknow.com/list");

        assertThat(result).containsExactly(new ProductCandidate(
                "새로운박스", "https://www.kbanknow.com/web/product/deposit/new-box"
        ));
    }

    @Test
    void imBankUsesProductOnlySearchUrlAsNavigableProductLink() {
        var result = new StubImBankScraper().search(null, "iM함께예금");

        assertThat(result).containsExactly(new ProductCandidate(
                "iM함께예금",
                "https://www.imbank.co.kr/dcz_ebz_10010_0010.act?kwd="
                        + "iM%ED%95%A8%EA%BB%98%EC%98%88%EA%B8%88&category=PRODUCT"
        ));
    }

    @Test
    void tossExtractsProductServiceLink() {
        var result = new TossBankScraper().extractProductLinks(Jsoup.parse("""
                <a href="/product-service/savings/time-deposit">토스뱅크 정기예금</a>
                """), "https://www.tossbank.com/");

        assertThat(result).containsExactly(new ProductCandidate(
                "토스뱅크 정기예금", "https://www.tossbank.com/product-service/savings/time-deposit"
        ));
    }

    private static class StubImBankScraper extends ImBankScraper {

        @Override
        protected List<ProductCandidate> searchPages(
                BrowserContext context,
                String productName,
                List<String> urlTemplates,
                BiFunction<Document, String, List<ProductCandidate>> extractor,
                boolean searchOnPage
        ) {
            return List.of(new ProductCandidate(
                    productName,
                    "https://www.imbank.co.kr/com_ebz_fpm_main.act?pd_cd=test"
            ));
        }
    }
}
