package apptive.fin.apicollector.bankurl;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MajorBankScrapersTest {

    @Test
    void kbExtractsProductResultLink() {
        var result = new KbBankScraper().extractSearchResults(Jsoup.parse("""
                <ul id="procList"><li><strong><a href="/quics?page=detail">KB Star 정기예금</a></strong></li></ul>
                """), "https://obank.kbstar.com/quics?page=search");

        assertThat(result).containsExactly(new ProductCandidate(
                "KB Star 정기예금", "https://obank.kbstar.com/quics?page=detail"
        ));
    }

    @Test
    void hanaExtractsOnlyProductInfoBlocks() {
        var result = new HanaBankScraper().extractSearchResults(Jsoup.parse("""
                <div class="resultDiv"><div class="productInfo"><h5>
                  <a href="/cont/product/1">하나의정기예금</a>
                </h5></div></div>
                """), "https://www.kebhana.com/search");

        assertThat(result).containsExactly(new ProductCandidate(
                "하나의정기예금", "https://www.kebhana.com/cont/product/1"
        ));
    }

    @Test
    void nhAddsKnownMiniSavingsProduct() {
        var result = new NhBankScraper().extractSearchResults(
                Jsoup.parse("<script>const code='1004713600002';</script>"),
                "https://smartmarket.nonghyup.com/list"
        );

        assertThat(result).singleElement().satisfies(candidate -> {
            assertThat(candidate.name()).isEqualTo("NH올원e미니적금");
            assertThat(candidate.url()).contains("detailPsnFncWrsC=1004713600002");
        });
    }

    @Test
    void ibkBuildsDetailUrlFromGoBankingArguments() {
        String url = new IbkBankScraper().urlFromGoBanking(
                "gobanking_url('/uib/detail.jsp','21011310089','page','*****','IBK회전정기 예금');"
        );

        assertThat(url)
                .contains("lncd=21", "grcd=01", "tmcd=131", "pdcd=0089", "wvcd=*****");
    }

    @Test
    void kdbExtractsKnownProductCode() {
        var result = new KdbBankScraper().extractProducts(
                Jsoup.parse("const PROD_C='100237000101'; const PROD_NM='KDB 정기예금';"),
                "https://banking.kdb.co.kr"
        );

        assertThat(result).contains(new ProductCandidate(
                "KDB 정기예금",
                "https://banking.kdb.co.kr/bp/BMDEWP01N10.act?PRD_C=100237000101#prd=100237000101"
        ));
    }

    @Test
    void scEncodesSearchQueryAsEucKr() {
        assertThat(new ScBankScraper().quoteEucKr("예금")).isEqualTo("%BF%B9%B1%DD");
    }

    @Test
    void shinhanExtractsProductNameAndCode() {
        var result = new ShinhanBankScraper().extractProductCodes(Jsoup.parse("""
                <ul class="listTyProducts"><li>
                  <div class="prdtName"><a>쏠편한 정기예금</a></div>
                  <span id="x상품코드y">P123</span>
                </li></ul>
                """), "https://bank.shinhan.com");

        assertThat(result).containsExactly(new ProductCandidate("쏠편한 정기예금", "P123"));
    }
}
