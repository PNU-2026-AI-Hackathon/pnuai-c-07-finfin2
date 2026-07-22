package apptive.fin.apicollector.normalize.extractor;

import apptive.fin.apicollector.normalize.dto.PreferentialRateDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FssPreferentialRateExtractor {

    private static final Pattern RATE_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%\\s*p?");
    private static final Pattern MIN_AGE_PATTERN = Pattern.compile("만\\s*(\\d+)\\s*세\\s*이상");
    private static final Pattern MAX_AGE_PATTERN = Pattern.compile("만\\s*(\\d+)\\s*세\\s*(?:이하|미만)");
    private static final Pattern AGE_RANGE_PATTERN = Pattern.compile("만\\s*(\\d+)\\s*세\\s*[~\\-]\\s*(\\d+)\\s*세");
    // 불릿 마커: 기호(-·*▶), 원문자(①-⑳), 구분자(./))가 붙은 1~2자리 숫자, 괄호숫자((1)), 한글 열거자(가./나.)만.
    // "3년", "50만원" 같은 내용 숫자는 불릿이 아니며, "0.5%p"처럼 구분자 뒤에 숫자가 이어지는 소수점도 불릿이 아니다.
    // 한글 열거자는 [가-하] 범위가 사이 모든 음절을 포함해 버리므로 명시적 문자 집합으로만 매칭한다.
    private static final Pattern BULLET_PATTERN = Pattern.compile(
            "^\\s*(?:[-·*▶]+|[①-⑳]+|\\d{1,2}\\s*[.)]+(?!\\d)|\\(\\d{1,2}\\)|[가나다라마바사아자차카타파하]\\s*[.)])\\s*");
    // 줄병합 오염 방지용 화이트리스트 토큰: 이어지는 라인에 자체 우대조건 토큰이 있으면 별도 항목으로 본다.
    private static final List<String> KEYWORD_TOKENS = List.of(
            "급여", "카드", "자동이체", "자동 이체", "마케팅", "상품서비스", "개인정보", "개인(신용)정보", "수집이용동의",
            "첫거래", "최초거래", "신규고객", "첫 예금거래", "입출금통장 최초", "재예치", "재가입",
            "인터넷", "스마트폰", "비대면", "모바일", "나이", "연령");

    public List<PreferentialRateDraft> extract(String preferentialCondition) {
        if (preferentialCondition == null || preferentialCondition.isBlank()) {
            return List.of();
        }

        // 기타(BANK_ETC)는 keyword당 1건으로 합치지 않고 라인별로 보존한다. 완전 중복(동일 description)만 최고금리로 정리.
        List<PreferentialRateDraft> candidates = new ArrayList<>();
        List<String> lines = mergeWrappedLines(preferentialCondition);
        for (String rawLine : lines) {
            String line = normalize(rawLine);
            if (line.isBlank() || isAggregateLine(line)) {
                continue;
            }

            Optional<BigDecimal> rate = rate(line);
            if (rate.isEmpty()) {
                continue;
            }

            for (KeywordValueEnum keyword : keywords(line)) {
                candidates.add(PreferentialRateDraft.builder()
                        .keywordCode(keyword)
                        .rate(rate.get())
                        .description(line)
                        .minAge(minAge(line))
                        .maxAge(maxAge(line))
                        .build());
            }
        }

        List<PreferentialRateDraft> result = new ArrayList<>(PreferentialRateReducer.reduce(candidates));
        if (result.isEmpty()) {
            fallback(lines).ifPresent(result::add);
        }
        return List.copyOf(result);
    }

    // 원문이 줄바꿈으로 문장이 잘린 경우("…중소기업 재직자" + "인 경우 : 연 1.0%p") 이어붙인다.
    // 금리 표기가 없는 라인 뒤에, 불릿 없이 시작하고 자체 우대조건 토큰도 없는 라인이 오면 같은 문장으로 본다.
    // (이어지는 라인에 키워드 토큰이 있으면 별도 항목일 가능성이 높다 — 헤더 라인의 키워드가 다음 항목의 금리를 상속하는 오염 방지)
    // 반대 방향(금리 라인 뒤에 이어지는 무금리 조건)은 별도 항목/주석과 규칙으로 구분할 수 없어 병합하지 않는다.
    // 그 경우 설명이 불완전할 수 있으나 LLM 보강(preferentialRates)이 전체 문맥으로 보완한다.
    private List<String> mergeWrappedLines(String preferentialCondition) {
        List<String> merged = new ArrayList<>();
        for (String rawLine : preferentialCondition.split("\\R")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (!merged.isEmpty()) {
                String previous = merged.get(merged.size() - 1);
                boolean previousHasRate = RATE_PATTERN.matcher(previous).find();
                boolean startsWithBullet = BULLET_PATTERN.matcher(rawLine).find();
                if (!previousHasRate && !startsWithBullet && !containsKeywordToken(line)) {
                    merged.set(merged.size() - 1, previous + " " + line);
                    continue;
                }
            }
            merged.add(line);
        }
        return merged;
    }

    private boolean containsKeywordToken(String line) {
        for (String token : KEYWORD_TOKENS) {
            if (line.contains(token)) {
                return true;
            }
        }
        return false;
    }

    // 개별 우대항목이 하나도 추출되지 않았지만 원문에 금리 표기가 있으면(총합 표현만 있는 상품 등)
    // 최고 금리 라인 1건을 기타(BANK_ETC)로 보존해 max_rate와의 정합성을 지킨다.
    // 총합 표현은 "연 0.10%p 제공(최대 연 3.10%p)"처럼 상한이 뒤에 오는 경우가 많아, 라인 내 최고 금리를 쓴다.
    private Optional<PreferentialRateDraft> fallback(List<String> lines) {
        PreferentialRateDraft best = null;
        for (String rawLine : lines) {
            String line = normalize(rawLine);
            if (line.isBlank()) {
                continue;
            }
            Optional<BigDecimal> rate = maxRate(line);
            if (rate.isEmpty()) {
                continue;
            }
            if (best == null || rate.get().compareTo(best.rate()) > 0) {
                best = PreferentialRateDraft.builder()
                        .keywordCode(KeywordValueEnum.BANK_ETC)
                        .rate(rate.get())
                        .description(line)
                        .minAge(minAge(line))
                        .maxAge(maxAge(line))
                        .build();
            }
        }
        return Optional.ofNullable(best);
    }

    private Optional<BigDecimal> maxRate(String line) {
        Matcher matcher = RATE_PATTERN.matcher(line);
        BigDecimal max = null;
        while (matcher.find()) {
            BigDecimal value = new BigDecimal(matcher.group(1));
            if (max == null || value.compareTo(max) > 0) {
                max = value;
            }
        }
        return Optional.ofNullable(max);
    }

    private boolean isAggregateLine(String line) {
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
                || line.matches(".*(최고|최대)\\s*연\\s*\\d.*%.*");
    }

    private List<KeywordValueEnum> keywords(String line) {
        Set<KeywordValueEnum> keywords = EnumSet.noneOf(KeywordValueEnum.class);
        addIfContains(keywords, line, KeywordValueEnum.BANK_SALARY_TRANSFER, "급여");
        addIfContains(keywords, line, KeywordValueEnum.BANK_CARD_USAGE, "카드", "체크카드", "신용카드");
        addIfContains(keywords, line, KeywordValueEnum.BANK_AUTO_TRANSFER, "자동이체", "자동 이체");
        addIfContains(keywords, line, KeywordValueEnum.BANK_MARKETING, "마케팅", "상품서비스", "개인정보", "개인(신용)정보", "수집이용동의");
        addIfContains(keywords, line, KeywordValueEnum.BANK_FIRST_TRANSACTION, "첫거래", "최초거래", "신규고객", "첫 예금거래", "입출금통장 최초");
        addIfContains(keywords, line, KeywordValueEnum.BANK_REDEPOSIT, "재예치", "재가입");
        addIfContains(keywords, line, KeywordValueEnum.BANK_ONLINE_JOIN, "인터넷", "스마트폰", "비대면", "모바일");
        // 나이 구간(만 N세)이 실제로 파싱될 때만 BANK_AGE. 구간 없는 "나이/연령" 언급은
        // BANK_AGE로 보존해봐야 백엔드가 나이 필터를 못 하므로, 아래 fallback(BANK_ETC)로 둔다.
        if (minAge(line) != null || maxAge(line) != null) {
            keywords.add(KeywordValueEnum.BANK_AGE);
        }
        // 화이트리스트 토큰에 하나도 매칭되지 않으면 기타(BANK_ETC)로 수집한다.
        if (keywords.isEmpty()) {
            keywords.add(KeywordValueEnum.BANK_ETC);
        }
        return List.copyOf(keywords);
    }

    private void addIfContains(Set<KeywordValueEnum> keywords, String line, KeywordValueEnum keyword, String... tokens) {
        for (String token : tokens) {
            if (line.contains(token)) {
                keywords.add(keyword);
                return;
            }
        }
    }

    private Optional<BigDecimal> rate(String line) {
        Matcher matcher = RATE_PATTERN.matcher(line);
        if (!matcher.find()) {
            return Optional.empty();
        }
        return Optional.of(new BigDecimal(matcher.group(1)));
    }

    private Integer minAge(String line) {
        Matcher range = AGE_RANGE_PATTERN.matcher(line);
        if (range.find()) {
            return parseInt(range.group(1));
        }
        Matcher matcher = MIN_AGE_PATTERN.matcher(line);
        return matcher.find() ? parseInt(matcher.group(1)) : null;
    }

    private Integer maxAge(String line) {
        Matcher range = AGE_RANGE_PATTERN.matcher(line);
        if (range.find()) {
            return parseInt(range.group(2));
        }
        Matcher matcher = MAX_AGE_PATTERN.matcher(line);
        return matcher.find() ? parseInt(matcher.group(1)) : null;
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalize(String line) {
        if (line == null) {
            return "";
        }
        return BULLET_PATTERN.matcher(line).replaceFirst("").trim();
    }
}
