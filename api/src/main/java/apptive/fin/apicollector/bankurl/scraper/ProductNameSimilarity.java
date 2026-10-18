package apptive.fin.apicollector.bankurl.scraper;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ProductNameSimilarity {

    private static final List<String> BANK_WORDS = List.of(
            "KB", "국민은행", "신한은행", "우리은행", "하나은행",
            "NH", "농협은행", "IBK", "기업은행", "카카오뱅크",
            "토스뱅크", "케이뱅크"
    );
    private static final List<List<String>> VARIANT_MARKER_GROUPS = List.of(
            List.of("자유적립", "정액적립"),
            List.of("만기", "월이자", "선이자")
    );

    public double score(String left, String right) {
        if (hasConflictingVariant(left, right)) {
            return 0.0;
        }
        String normalizedLeft = normalize(left);
        String normalizedRight = normalize(right);
        if (normalizedLeft.isEmpty() || normalizedRight.isEmpty()) {
            return 0.0;
        }
        String koreanLeft = normalizedLeft.replaceAll("[^가-힣]", "");
        String koreanRight = normalizedRight.replaceAll("[^가-힣]", "");
        if (koreanLeft.length() >= 6 && koreanLeft.equals(koreanRight)) {
            return 1.0;
        }

        double weightedRatio = FuzzySearch.weightedRatio(normalizedLeft, normalizedRight) / 100.0;
        double tokenSetRatio = FuzzySearch.tokenSetRatio(normalizedLeft, normalizedRight) / 100.0;
        return 0.45 * weightedRatio
                + 0.35 * tokenSetRatio
                + 0.20 * bigramJaccard(normalizedLeft, normalizedRight);
    }

    public boolean hasConflictingVariant(String left, String right) {
        String compactLeft = compact(left);
        String compactRight = compact(right);
        for (List<String> group : VARIANT_MARKER_GROUPS) {
            boolean leftHasMarker = group.stream().anyMatch(compactLeft::contains);
            boolean rightHasMarker = group.stream().anyMatch(compactRight::contains);
            boolean sharesMarker = group.stream()
                    .anyMatch(marker -> compactLeft.contains(marker) && compactRight.contains(marker));
            if (leftHasMarker && rightHasMarker && !sharesMarker) {
                return true;
            }
        }
        return false;
    }

    String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.toLowerCase(Locale.ROOT)
                .replaceAll("\\([^)]*\\)", " ")
                .replaceAll("[^0-9a-zA-Z가-힣]", " ");
        for (String bankWord : BANK_WORDS) {
            normalized = normalized.replace(bankWord.toLowerCase(Locale.ROOT), " ");
        }
        return normalized.replaceAll("\\s+", "");
    }

    private String compact(String value) {
        return value == null ? "" : value.replaceAll("[^0-9a-zA-Z가-힣]", "")
                .toLowerCase(Locale.ROOT);
    }

    private double bigramJaccard(String left, String right) {
        Set<String> leftBigrams = bigrams(left);
        Set<String> rightBigrams = bigrams(right);
        if (leftBigrams.isEmpty() || rightBigrams.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(leftBigrams);
        intersection.retainAll(rightBigrams);
        Set<String> union = new HashSet<>(leftBigrams);
        union.addAll(rightBigrams);
        return (double) intersection.size() / union.size();
    }

    private Set<String> bigrams(String value) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < value.length() - 1; i++) {
            result.add(value.substring(i, i + 2));
        }
        return result;
    }
}
