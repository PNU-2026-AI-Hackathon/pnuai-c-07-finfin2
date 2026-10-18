package apptive.fin.apicollector.normalize.extractor;

/**
 * 우대금리 설명 문구가 개별 조건이 아니라 "총합/상한 요약"인지 판정하는 공용 헬퍼.
 * 규칙추출({@link FssPreferentialRateExtractor})과 LLM 결과 파서(GeminiResponseParser)가 함께 사용해,
 * "우대이율 최대 2.5%"처럼 조건 없는 총합 문구가 BANK_ETC 우대금리로 저장되는 것을 막는다.
 * (백엔드는 BANK_ETC를 자동 합산하지 않으나, 프론트에서 사용자가 직접 선택해 수익률에 반영하므로 저장 단계에서 제거한다.)
 */
public final class PreferentialRatePhrases {

    private PreferentialRatePhrases() {
    }

    // 총합/상한 요약 표현이면 true. 개별 조건을 명시한 문구는 false.
    // 핵심 구분 신호: 총합 라인은 "우대금리/이율"이 문두 주어로 등장하고, 개별조건은 조건(급여이체/방문 인증 등)이 먼저 온다.
    // (예: "급여이체 실적 충족 시 우대금리 최대 0.3%p"는 개별 조건이므로 유지 — 문두가 조건이라 아래 앵커 패턴에 걸리지 않는다.)
    public static boolean isAggregate(String text) {
        if (text == null) {
            return false;
        }
        String line = text.trim();
        return line.contains("최대우대금리")
                || line.contains("최고우대금리")
                || line.contains("최대 우대금리")
                || line.contains("최고 우대금리")
                || line.contains("우대금리 합계")
                || line.contains("우대이율 합계")
                || line.matches(".*항목별.*최고.*")
                // "우대이율 최고 연 1.0%", "우대금리 최고 …" 등 총합 표현
                || line.matches(".*우대(금리|이율)\\s*최고.*")
                // "우대이율(최대 0.90%p)" 등 괄호로 상한을 묶은 총합 표현 (괄호 없는 "…우대금리 최대 0.3%p"는 개별 조건일 수 있어 제외)
                || line.matches(".*우대(금리|이율)\\s*\\(\\s*(최대|최고).*")
                // "우대금리 최대한도 : 1.0%p" 등 한도형 총합 표현
                || line.matches(".*우대(금리|이율)\\s*(최대|최고)\\s*한도.*")
                // "아래의 우대요건 충족시 최고0.3% 추가우대" 등 하위 항목을 가리키는 총합 표현(라인 선두일 때만)
                || (line.startsWith("아래") && line.contains("충족"))
                // "최고 연 1.0%", "최대 연 1.0%p" 등 상한/총합 표현(특정 조건 없이 상한만 명시)
                || line.matches(".*(최고|최대)\\s*연\\s*\\d.*%.*")
                // 문두 앵커 - "우대금리/이율"이 (공통/N개월제 등 짧은 수식어 뒤) 주어로 시작하고 최대/최고가 이어지는 총합
                // 예: "우대이율 최대 2.5%", "공통 우대이율 최대 2%", "우대이율 6개월 미만 최대2.00%…"
                || line.matches("^(공통|기본|전체|가입자격별|\\d+\\s*개월제?|\\d+\\s*년제?|\\s)*우대(금리|이율).*(최대|최고).*")
                // 문두 앵커 - "N개월제 최대 우대이율"처럼 (최대|최고)가 우대금리/이율 앞에 오는 총합
                || line.matches("^(공통|기본|전체|가입자격별|\\d+\\s*개월제?|\\d+\\s*년제?|\\s)*(최대|최고)\\s*우대(금리|이율).*");
    }
}
