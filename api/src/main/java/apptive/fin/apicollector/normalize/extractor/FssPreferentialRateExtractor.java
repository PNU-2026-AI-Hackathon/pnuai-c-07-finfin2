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

    public List<PreferentialRateDraft> extract(String preferentialCondition) {
        if (preferentialCondition == null || preferentialCondition.isBlank()) {
            return List.of();
        }

        Map<KeywordValueEnum, PreferentialRateDraft> bestByKeyword = new LinkedHashMap<>();
        for (String rawLine : preferentialCondition.split("\\R")) {
            String line = normalize(rawLine);
            if (line.isBlank() || isAggregateLine(line)) {
                continue;
            }

            Optional<BigDecimal> rate = rate(line);
            if (rate.isEmpty()) {
                continue;
            }

            for (KeywordValueEnum keyword : keywords(line)) {
                PreferentialRateDraft candidate = PreferentialRateDraft.builder()
                        .keywordCode(keyword)
                        .rate(rate.get())
                        .description(line)
                        .minAge(minAge(line))
                        .maxAge(maxAge(line))
                        .build();
                keepHighest(bestByKeyword, candidate);
            }
        }
        return List.copyOf(bestByKeyword.values());
    }

    private void keepHighest(Map<KeywordValueEnum, PreferentialRateDraft> bestByKeyword, PreferentialRateDraft candidate) {
        PreferentialRateDraft existing = bestByKeyword.get(candidate.keywordCode());
        if (existing == null || candidate.rate().compareTo(existing.rate()) > 0) {
            bestByKeyword.put(candidate.keywordCode(), candidate);
        }
    }

    private boolean isAggregateLine(String line) {
        return line.contains("최대우대금리")
                || line.contains("최고우대금리")
                || line.contains("최대 우대금리")
                || line.contains("최고 우대금리")
                || line.matches(".*항목별.*최고.*");
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
        if (minAge(line) != null || maxAge(line) != null || line.contains("나이") || line.contains("연령")) {
            keywords.add(KeywordValueEnum.BANK_AGE);
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
        return line.replaceAll("^\\s*[\\-·*①-⑳\\d.)]+\\s*", "").trim();
    }
}
