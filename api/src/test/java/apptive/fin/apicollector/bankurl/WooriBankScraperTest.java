package apptive.fin.apicollector.bankurl;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WooriBankScraperTest {

    @Test
    void extractsProductFromSearchResult() {
        WooriBankScraper scraper = new WooriBankScraper();

        var result = scraper.extractSearchResults(Jsoup.parse("""
                <ul class="searchResult"><li><strong><a href="/product/1">WON플러스예금</a></strong></li></ul>
                """), "https://spib.wooribank.com/search");

        assertThat(result).containsExactly(new ProductCandidate(
                "WON플러스예금",
                "https://spib.wooribank.com/product/1"
        ));
    }
}
