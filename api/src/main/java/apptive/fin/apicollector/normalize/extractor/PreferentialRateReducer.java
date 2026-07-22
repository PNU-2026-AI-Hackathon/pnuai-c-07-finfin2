package apptive.fin.apicollector.normalize.extractor;

import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 우대금리 draft 목록을 정리(reduce)한다.
 * - 일반 BANK_* 키워드: 키워드당 최고금리 1건.
 * - BANK_ETC(기타): description별 최고금리(서로 다른 조건은 각각 보존, 동일 문구만 최고금리로 정리).
 * - 결과 순서: 키워드 best(등장순) 다음 ETC best(등장순).
 */
public final class PreferentialRateReducer {

    private PreferentialRateReducer() {
    }

    public static List<PreferentialRateDraft> reduce(List<PreferentialRateDraft> drafts) {
        Map<KeywordValueEnum, PreferentialRateDraft> byKeyword = new LinkedHashMap<>();
        Map<String, PreferentialRateDraft> etcByDescription = new LinkedHashMap<>();
        for (PreferentialRateDraft draft : drafts) {
            if (draft.keywordCode() == KeywordValueEnum.BANK_ETC) {
                keepHigher(etcByDescription, draft.description(), draft);
            }
            else {
                keepHigher(byKeyword, draft.keywordCode(), draft);
            }
        }

        List<PreferentialRateDraft> result = new ArrayList<>(byKeyword.values());
        result.addAll(etcByDescription.values());
        return List.copyOf(result);
    }

    private static <K> void keepHigher(Map<K, PreferentialRateDraft> best, K key, PreferentialRateDraft candidate) {
        PreferentialRateDraft existing = best.get(key);
        if (existing == null || candidate.rate().compareTo(existing.rate()) > 0) {
            best.put(key, candidate);
        }
    }
}
