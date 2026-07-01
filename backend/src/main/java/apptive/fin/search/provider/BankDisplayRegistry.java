package apptive.fin.search.provider;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * FSS(금감원) API가 내려주는 등기상 명칭(예: "주식회사 하나은행")과
 * 실제 사용자에게 노출해야 할 브랜드명(예: "하나은행")이 달라 생기는 괴리를
 * 흡수하기 위한 정적 참조 테이블. FSS provider code(fin_co_no)는 은행이 바뀌지 않는 한
 * 안정적이므로 이를 키로 사용한다.
 */
@Component
public class BankDisplayRegistry {

    private static final Map<String, BankDisplayInfo> BY_CODE = Map.ofEntries(
            Map.entry("0010001", new BankDisplayInfo("0010001", "우리은행", "시중", null)),
            Map.entry("0010002", new BankDisplayInfo("0010002", "SC제일은행", "시중", null)),
            Map.entry("0010016", new BankDisplayInfo("0010016", "iM뱅크", "시중", null)),
            Map.entry("0010017", new BankDisplayInfo("0010017", "부산은행", "지방", "reg_05")),
            Map.entry("0010019", new BankDisplayInfo("0010019", "광주은행", "지방", "reg_07")),
            Map.entry("0010020", new BankDisplayInfo("0010020", "제주은행", "지방", "reg_09")),
            Map.entry("0010022", new BankDisplayInfo("0010022", "전북은행", "지방", "reg_08")),
            Map.entry("0010024", new BankDisplayInfo("0010024", "경남은행", "지방", "reg_06")),
            Map.entry("0010026", new BankDisplayInfo("0010026", "IBK기업은행", "특수", null)),
            Map.entry("0010030", new BankDisplayInfo("0010030", "KDB산업은행", "특수", null)),
            Map.entry("0010927", new BankDisplayInfo("0010927", "KB국민은행", "시중", null)),
            Map.entry("0011625", new BankDisplayInfo("0011625", "신한은행", "시중", null)),
            Map.entry("0013175", new BankDisplayInfo("0013175", "NH농협은행", "특수", null)),
            Map.entry("0013909", new BankDisplayInfo("0013909", "하나은행", "시중", null)),
            Map.entry("0014674", new BankDisplayInfo("0014674", "케이뱅크", "인터넷", null)),
            Map.entry("0014807", new BankDisplayInfo("0014807", "Sh수협은행", "특수", null)),
            Map.entry("0015130", new BankDisplayInfo("0015130", "카카오뱅크", "인터넷", null)),
            Map.entry("0017801", new BankDisplayInfo("0017801", "토스뱅크", "인터넷", null))
    );

    private static final String UNKNOWN_CATEGORY = "기타";

    public Optional<BankDisplayInfo> find(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(BY_CODE.get(code));
    }

    public String displayNameOrFallback(String code, String rawName) {
        return find(code).map(BankDisplayInfo::displayName).orElse(rawName);
    }

    public String categoryOrFallback(String code) {
        return find(code).map(BankDisplayInfo::category).orElse(UNKNOWN_CATEGORY);
    }

    public String region(String code) {
        return find(code).map(BankDisplayInfo::region).orElse(null);
    }

    public List<BankDisplayInfo> all() {
        return List.copyOf(BY_CODE.values());
    }
}
