package apptive.fin.apicollector.bankurl.scraper;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegionalBankScrapersTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void suhyupBuildsDesktopProductUrlFromMobileCode() {
        var result = new SuhyupBankScraper().extractMobileProducts(Jsoup.parse("""
                <li class="item" data-prodcd="D00175"><span class="pdt-nm">헤이(Hey)정기예금</span></li>
                """), "https://m.suhyup-bank.com/list");

        assertThat(result).containsExactly(new ProductCandidate(
                "헤이(Hey)정기예금",
                "https://www.suhyup-bank.com/ib20/mnu/FPD00118/_menuId/FPD00124/_productCode/D00175"
        ));
    }

    @Test
    void kyongnamBuildsDetailUrlFromGoDetailCall() {
        var result = new KyongnamBankScraper().extractBankProducts(Jsoup.parse("""
                <a onclick="goDetail('0000020178')">BNK 위더스자유적금</a>
                """), "https://www.knbank.co.kr/list");

        assertThat(result).containsExactly(new ProductCandidate(
                "BNK 위더스자유적금",
                "https://www.knbank.co.kr/ib20/mnu/FPMDPT020103000?fnc_prd_no=0000020178"
        ));
    }

    @Test
    void busanBuildsDirectDetailUrlFromFpcd() {
        var result = new BusanBankScraper().extractProductList(Jsoup.parse("""
                <a class="FPCD_DTL" fpcd="0010100191">더(THE) 레벨업 정기예금</a>
                """), "https://www.busanbank.co.kr/list");

        assertThat(result).containsExactly(new ProductCandidate(
                "더(THE) 레벨업 정기예금",
                "https://www.busanbank.co.kr/ib20/mnu/FPMDPO012001002?FPCD=0010100191"
        ));
    }

    @Test
    void kwangjuBuildsMobileProductUrl() {
        var payload = objectMapper.readTree("""
                {"PRD_LIST":[{
                  "SMRT_BNKN_GDS_NM":"굿스타트예금",
                  "SMRT_BNKN_PCK_GDS_CD":"PICK1",
                  "SMRT_BNKN_GDS_TYCD":"100",
                  "SMRT_BNKN_GDS_CD":"PRD1",
                  "SMRT_BNKN_HOST_GDS_CD":"HOST1"
                }]}
                """);

        var result = new KwangjuBankScraper(objectMapper).extractMobileProducts(payload);

        assertThat(result).containsExactly(new ProductCandidate(
                "굿스타트예금",
                "https://m.kjbank.com/mweb/spa/goodsDetail/?pick=PICK1&kind=deposit&prdCd=PRD1&hostGdsCd=HOST1"
        ));
    }

    @Test
    void jejuPrefersMatchingInterestPaymentType() {
        JejuBankScraper scraper = new JejuBankScraper();
        List<ProductCandidate> candidates = List.of(
                new ProductCandidate("제주Dream정기예금 (고정금리형-월이자지급식)", "https://www.jejubank.co.kr/monthly"),
                new ProductCandidate("제주Dream정기예금 (고정금리형-만기이자지급식)", "https://www.jejubank.co.kr/maturity")
        );

        var result = scraper.preferMatchingInterestType(
                "제주Dream 정기예금 (개인/만기지급식)", candidates
        );

        assertThat(result).containsExactly(candidates.get(1));
    }

    @Test
    void jeonbukBuildsEncodedMobileDetailUrl() {
        var payload = objectMapper.readTree("""
                {"GRID":[{
                  "GDS_NM":"JB 123 정기예금",
                  "GDS_CD":"1001200240059",
                  "GDS_DTLS_CD":"0000",
                  "GDS_WHOL_CD":"10012002400590000"
                }]}
                """);

        var result = new JeonbukBankScraper(objectMapper).extractProducts(payload);

        assertThat(result).singleElement().satisfies(candidate -> {
            assertThat(candidate.name()).isEqualTo("JB 123 정기예금");
            assertThat(candidate.url()).startsWith(
                    "https://m.jbbank.co.kr:8543/JBbank.act?TRGT_URL=P_M_SID_MALL_DTL&TRGT_PARAM="
            );
        });
    }
}
