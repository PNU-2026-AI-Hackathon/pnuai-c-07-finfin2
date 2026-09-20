package apptive.fin.apicollector.normalize.enrich;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.raw.ProductRaw;
import org.springframework.stereotype.Component;

/** FSS LLM enrichment 요청 프롬프트를 생성한다. */
@Component
public class FssEnrichmentPromptBuilder {

    public String build(ProductRaw rawProduct, ProductDraft draft) {
        return """
                금융감독원 FSS 금융상품 원문 JSON을 보고 사용자 화면에 필요한 보강값만 추출해라.

                규칙:
                - 응답은 schema에 맞는 JSON만 반환한다.
                - 원문에 명시되지 않은 값은 null 또는 false로 둔다.
                - 금리, 기간, 은행명, 상품명, 상품코드, 신청 URL은 생성하지 않는다.
                - minMonthlyLimit, maxMonthlyLimit은 월 납입액(적금의 월 정기 납입) 전용 필드이다.
                - productType이 SAVING(적금)일 때만, 최소/최대 월 납입액이 명시된 경우 채운다. 없거나 제한 없음이면 null로 둔다.
                - productType이 DEPOSIT(정기예금)이면 minMonthlyLimit, maxMonthlyLimit은 항상 null로 둔다. 일시납 가입금액·가입한도는 여기에 넣지 않는다.
                - keywords에는 기간 키워드(TERM_*)를 넣지 않는다.
                - summaryContent는 마케팅 문구 없이 가입방법, 우대조건, 가입대상, 유의사항을 짧게 정리한다.
                - requiredKeywords에는 가입 가능 여부를 제한하는 STATUS_* 필수/제외 조건만 넣는다.
                - requiredKeywords는 가입대상 문구에 신분 조건이 명시된 경우만 넣는다. 상품명, 은행명, 우대금리 조건, 급여이체 조건, 카드 실적 조건에서 추론하지 않는다.
                - "실명의 개인", "개인", "개인사업자 포함", "개인사업자 제외", "만 N세 이상" 같은 일반 가입 조건은 STATUS_*로 매핑하지 않는다.
                - "직장인", "급여", "급여이체"는 STATUS_SME_WORKER가 아니다.
                - "아이", "우리아이", "자녀", "미성년"은 STATUS_PART_TIME 또는 STATUS_UNEMPLOYED가 아니다.
                - "병역 이행 기간만큼 나이 연장"은 STATUS_MILITARY requiredKeyword가 아니다.
                - requiredKeywords의 confidence가 HIGH가 아닐 정도로 불확실하면 항목을 만들지 말고 빈 배열로 둔다.
                - EXCLUDE는 "가입 불가", "제외", "대상 아님" 같은 배제 표현과 해당 신분이 같은 가입대상 문맥에 명시된 경우만 넣는다.
                - preferentialRates에는 조건별 가산금리가 명시된 경우만 넣는다. 최고/최대 우대금리 총합만 있으면 빈 배열로 둔다.
                - preferentialRates의 keywordCode는 원문의 우대조건 의미와 정확히 일치할 때만 선택한다. 비슷해 보인다는 이유로 끼워맞추지 않는다.
                - 상위 포괄조건 아래 "가/나/다" 등 하위조건이 딸려 있으면, 가산금리는 하위조건에만 붙어있으므로 하위조건 각각만 별도 preferentialRates 항목으로 넣는다. 상위 포괄조건 자체는 별도 항목으로 만들지 않는다.
                - "쿠폰을 적용", "우대금리 제공" 처럼 구체적인 %%p 수치가 없는 항목은 이유를 설명하지 말고 그냥 preferentialRates에서 제외한다.
                - "최고우대금리", "최고우대이율", "가산금리 최고", "우대금리 최대한도", "최대 X%%" 등 표현과 무관하게, 전체 우대금리의 합계/상한만 적은 안내 문구 아래에 개별 조건과 %%가 따로 나열돼 있으면, 그 안내 문구 자체는 무시하고 아래 개별 조건들만 각각 preferentialRates 항목으로 사용한다. 안내 문구가 있다고 해서 아래 개별 조건들까지 빈 배열로 만들지 않는다.
                - 하나의 조건 안에서 금액 등 기준에 따라 요율이 여러 구간으로 나뉘면(예: 300만원이상 0.1%%, 500만원이상 0.2%%), 구간마다 별도의 preferentialRates 항목으로 나누어 넣는다. 각 항목의 description에 해당 구간의 기준을 명시한다.
                - 구간별 항목을 나눌 때 흔히 하는 실수: 모든 구간에 똑같은(대개 가장 큰) rate를 복사해버리는 것. 반드시 원문에서 그 구간 바로 옆에 적힌 숫자를 그대로 옮겨써야 하며, 구간마다 rate가 달라야 한다.
                  예시 원문: "300만원이상 0.1%%, 500만원이상 0.2%%"
                  올바른 출력: [{"rate":0.1,"description":"...300만원 이상"},{"rate":0.2,"description":"...500만원 이상"}]
                  잘못된 출력(금지): [{"rate":0.2,"description":"...300만원 이상"},{"rate":0.2,"description":"...500만원 이상"}] (두 rate가 같으면 틀린 것이다)
                - 허용되는 preferentialRates 매핑:
                  * BANK_CARD_USAGE: 카드 보유/사용/결제실적/전월결제 조건
                  * BANK_SALARY_TRANSFER: 급여/월급 이체 조건
                  * BANK_AUTO_TRANSFER: 자동이체 조건
                  * BANK_MARKETING: 마케팅/상품서비스/개인정보 수집이용 동의 조건. 모바일메시지/알림 수신동의도 여기에 포함한다.
                  * BANK_FIRST_TRANSACTION: 첫거래/최초거래/신규고객 조건
                  * BANK_REDEPOSIT: 재예치/재가입 조건
                  * BANK_ONLINE_JOIN: 인터넷/모바일/비대면/온라인 가입 조건. 모바일메시지/알림 수신동의는 온라인 가입이 아니다.
                  * BANK_AGE: 특정 나이 구간 우대. 이 경우 minAge/maxAge를 반드시 채운다(예: 만 19~34세 → minAge=19,maxAge=34; 만 65세 이상 → minAge=65). 나이 구간을 특정할 수 없으면 BANK_AGE 대신 BANK_ETC로 넣는다.
                  * BANK_ETC: 위 조건 중 어디에도 정확히 해당하지 않지만 조건별 가산금리가 명시된 우대금리(기타)
                - 위 매핑으로 정확히 표현할 수 없는 우대금리는 BANK_ETC로 매핑한다. (단, 최고/최대 우대금리 총합만 있으면 여전히 제외)
                - 재예치/재가입이라는 단어가 있어도 조건의 핵심이 가입금액, 가입잔액, 요구불평잔, 평균잔액이면 BANK_REDEPOSIT에 매핑하지 않는다.
                - 예: 가입금액, 추천/쿠폰/이벤트 등은 억지로 BANK_*에 매핑하지 않는다.
                - FSS 원문에 정부기여금/병역연장/비교제외가 명시되지 않았으면 관련 필드는 null 또는 false로 둔다.
                - earnMaxAmt는 가입자격의 연소득 상한(소득요건)이 원문에 명시된 경우에만 채운다. 가입금액·예치한도·최고한도·월 납입한도 등 금액 한도는 earnMaxAmt에 절대 넣지 않는다.
                - earnPercent는 소득기준(예: 기준중위소득 대비 %%)이 원문에 명시된 경우에만 채운다.
                - 반드시 아래 JSON skeleton의 모든 top-level key를 포함한다. 모르는 값은 null, false, [] 중 schema에 맞는 기본값으로 둔다.

                JSON skeleton:
                {
                  "summaryContent": null,
                  "keywords": [],
                  "minMonthlyLimit": null,
                  "maxMonthlyLimit": null,
                  "minAge": null,
                  "maxAge": null,
                  "earnMaxAmt": null,
                  "earnPercent": null,
                  "requiresHomeless": false,
                  "requiresHouseholder": false,
                  "govContributionRate": null,
                  "govContributionType": null,
                  "govMatchingRatio": null,
                  "govMonthlyFixedContribution": null,
                  "govContributionPeriodMonths": null,
                  "excludeFromRateComparison": false,
                  "allowsMilitaryAgeExtension": false,
                  "militaryMaxAge": null,
                  "requiredKeywords": [],
                  "preferentialRates": []
                }

                현재 정규화 결과:
                productName=%s
                productType=%s
                content=%s

                FSS raw JSON:
                %s
                """.formatted(
                draft.productName(),
                draft.type(),
                draft.content(),
                rawProduct.getRawJson()
        );
    }
}
