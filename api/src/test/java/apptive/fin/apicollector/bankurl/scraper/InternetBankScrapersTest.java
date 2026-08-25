package apptive.fin.apicollector.bankurl.scraper;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

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
    void kbankUsesKnownNameForProductWithoutProductWord() {
        var result = new KbankScraper().extractProductLinks(Jsoup.parse("""
                <a href="/web/product/deposit/rolling-farm">자세히 보기</a>
                """), "https://www.kbanknow.com/list");

        assertThat(result).containsExactly(new ProductCandidate(
                "데굴데굴농장", "https://www.kbanknow.com/web/product/deposit/rolling-farm"
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
}
