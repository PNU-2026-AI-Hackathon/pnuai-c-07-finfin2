package apptive.fin.apicollector.normalize.normalizer;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class FssBankUrlNormalizer {

    private static final Map<String, String> URLS = Map.ofEntries(
            Map.entry("0010001", "https://spot.wooribank.com/pot/Dream?withyou=PODEP0001"), // 우리은행
            Map.entry("0010002", "https://www.standardchartered.co.kr/np/kr/pl/se/SavingList.jsp?ptfrm=HIN.KOR.INTRO.mega.korPerA1_1&id=list1"), // SC제일은행
            Map.entry("0010016", "https://www.imbank.co.kr/com_ebz_fpm_sub_main.jsp"), // im뱅크
            Map.entry("0010017", "https://www.busanbank.co.kr/ib20/mnu/FPM00001"), // 부산은행
            Map.entry("0010019", "https://www.kjbank.com/ib20/mnu/FPM0000000001"), // 광주은행
            Map.entry("0010020", "https://www.jejubank.co.kr/hmpg/prdGdnc/sid/mndp.do"), // 제주은행
            Map.entry("0010022", "https://www.jbbank.co.kr/"), // 전북은행
            Map.entry("0010024", "https://www.knbank.co.kr/ib20/mnu/FPM000000000001"), // 경남은행
            Map.entry("0010026", "https://mybank.ibk.co.kr/uib/jsp/guest/ntr/ntr00/ntr0000/PNTR000000_i.jsp?_linkFrmChk=Y"), // 기업은행
            Map.entry("0010030", "https://www.kdb.co.kr/index.jsp"), // 산업은행
            Map.entry("0010927", "https://obank.kbstar.com/quics?page=C030037"), // 국민은행
            Map.entry("0011625", "https://bank.shinhan.com/index.jsp#020001000000"), // 신한은행
            Map.entry("0013175", "https://smartmarket.nonghyup.com/servlet/BFBCW0001R.view"), // 농협은행
            Map.entry("0013909", "https://www.kebhana.com/cont/mall/index.jsp"), // 하나은행
            Map.entry("0014674", "https://www.kbanknow.com/web/product/info/list?tab=deposit"), // 케이뱅크
            Map.entry("0014807", "https://www.suhyup-bank.com/"), // 수협은행
            Map.entry("0015130", "https://www.kakaobank.com/products/withdrawal"), // 카카오뱅크
            Map.entry("0017801", "https://www.tossbank.com/product-service/savings/time-deposit") // 토스뱅크
    );

    public Optional<String> normalize(String code) {
        
        String url = URLS.get(code);

        if (url == null)
            return Optional.empty();


        return Optional.of(url);
    }
}
