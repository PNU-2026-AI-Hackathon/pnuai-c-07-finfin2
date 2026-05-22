package apptive.fin.apicollector.normalize.classifier;

import apptive.fin.apicollector.normalize.ProductClassification;
import apptive.fin.apicollector.normalize.normalizer.AbstractProductNormalizer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Component
public class OntongYouthPolicyClassifier extends AbstractProductNormalizer {

    private static final String FINANCE_CATEGORY = "취약계층 및 금융지원";

    private static final int FINANCIAL_PRODUCT_THRESHOLD = 40;

    private static final List<String> LOAN_METHOD_CODES = List.of("42003", "42007");

    private static final List<String> LOAN_TITLE_KEYWORDS = List.of(
            "대출",
            "융자",
            "이차보전",
            "대출이자",
            "이자지원",
            "햇살론",
            "보증"
    );

    private static final List<String> DEBT_RECOVERY_KEYWORDS = List.of(
            "채무",
            "부채",
            "신용회복",
            "장기연체",
            "연체",
            "상환",
            "분할상환"
    );

    private static final List<String> PROTECTED_SAVINGS_PRODUCT_TERMS = List.of(
            "청년주택드림청약통장",
            "주택드림청약통장",
            "청약통장"
    );

    private static final List<String> STRONG_PRODUCT_TITLE_TERMS = List.of(
            "청년도약계좌",
            "청년내일저축계좌",
            "내일저축계좌",
            "청년저축계좌",
            "청년희망적금",
            "청년미래적금",
            "장병내일준비적금",
            "청년주택드림청약통장",
            "주택드림청약통장",
            "청약통장",
            "재형저축",
            "내일채움공제",
            "사랑채움",
            "희망공제",
            "상생희망공제",
            "기쁨두배통장",
            "두배적금",
            "희망디딤돌 통장",
            "모다드림 청년통장",
            "드림For 청년통장",
            "행복씨앗통장",
            "함안정착 청년통장",
            "청년통장"
    );

    private static final List<String> STRONG_PRODUCT_CONTENT_TERMS = List.of(
            "청년도약계좌",
            "청년내일저축계좌",
            "내일저축계좌",
            "청년저축계좌",
            "청년희망적금",
            "청년미래적금",
            "장병내일준비적금",
            "재형저축",
            "내일채움공제",
            "상생희망공제"
    );

    private static final Map<String, Integer> POSITIVE_WEIGHT_MAP = Map.ofEntries(
            // 실제 상품명 또는 상품성이 매우 강한 표현
            Map.entry("청년도약계좌", 100),
            Map.entry("청년내일저축계좌", 100),
            Map.entry("내일저축계좌", 90),
            Map.entry("청년저축계좌", 80),
            Map.entry("청년희망적금", 90),
            Map.entry("청년미래적금", 90),
            Map.entry("장병내일준비적금", 90),

            Map.entry("청년주택드림청약통장", 100),
            Map.entry("주택드림청약통장", 95),
            Map.entry("청약통장", 70),

            Map.entry("재형저축", 85),
            Map.entry("희망사다리 재형저축", 100),

            Map.entry("내일채움공제", 85),
            Map.entry("내일채움", 60),
            Map.entry("사랑채움", 55),
            Map.entry("희망공제", 60),
            Map.entry("상생희망공제", 65),

            Map.entry("기쁨두배통장", 90),
            Map.entry("두배적금", 85),
            Map.entry("희망디딤돌 통장", 90),
            Map.entry("모다드림 청년통장", 90),
            Map.entry("드림For 청년통장", 90),
            Map.entry("행복씨앗통장", 90),
            Map.entry("함안정착 청년통장", 90),
            Map.entry("청년통장", 65),

            // 구조적 신호
            Map.entry("자산형성지원", 70),
            Map.entry("자산형성 지원", 60),
            Map.entry("자산형성", 45),
            Map.entry("본인저축", 30),
            Map.entry("본인 저축", 30),
            Map.entry("저축액", 25),
            Map.entry("월 저축", 25),
            Map.entry("적립금 매칭", 35),
            Map.entry("매칭지원", 25),
            Map.entry("매칭 지원", 25),
            Map.entry("매칭", 8),
            Map.entry("만기예금", 45),
            Map.entry("만기 적립", 30),
            Map.entry("만기저축", 30),
            Map.entry("만기", 6),
            Map.entry("목돈", 18),
            Map.entry("비과세", 12),
            Map.entry("기여금", 10),

            // 일반 금융상품 토큰
            Map.entry("통장", 35),
            Map.entry("적금", 40),
            Map.entry("예금", 35),
            Map.entry("저축", 20),
            Map.entry("공제금", 30),
            Map.entry("공제", 8),
            Map.entry("계좌", 8),
            Map.entry("적립", 10),
            Map.entry("납입", 10),
            Map.entry("우대금리", 12)
    );

    private static final Map<String, Integer> NEGATIVE_WEIGHT_MAP = Map.ofEntries(
            // 교육/상담/콘텐츠성 정책
            Map.entry("재무상담", -40),
            Map.entry("금융교육", -40),
            Map.entry("경제교육", -35),
            Map.entry("재무설계", -35),
            Map.entry("금융상품분석", -25),
            Map.entry("상담", -22),
            Map.entry("교육", -18),
            Map.entry("강의", -30),
            Map.entry("특강", -30),
            Map.entry("워크샵", -45),
            Map.entry("클래스", -40),
            Map.entry("학교", -25),
            Map.entry("센터", -20),
            Map.entry("컨설팅", -30),
            Map.entry("프로그램", -16),
            Map.entry("가계부", -35),
            Map.entry("돈관리", -35),

            // 단순 현금지원/환급/생활지원 문맥
            Map.entry("계좌 입금", -35),
            Map.entry("계좌입금", -35),
            Map.entry("계좌지급", -35),
            Map.entry("계좌로", -18),
            Map.entry("현금 지급", -25),
            Map.entry("일시금", -20),
            Map.entry("환급", -22),
            Map.entry("수당", -18),
            Map.entry("장려금", -18),
            Map.entry("지원금 지급", -15),
            Map.entry("월세", -22),
            Map.entry("주거비", -22),
            Map.entry("임대료", -18),
            Map.entry("이사비", -20),
            Map.entry("교통비", -18),
            Map.entry("응시료", -25),

            // 대출/신용회복/채무 문맥
            Map.entry("채무조정", -60),
            Map.entry("신용회복", -50),
            Map.entry("장기연체", -50),
            Map.entry("연체", -30),
            Map.entry("부채", -35),
            Map.entry("상환", -45),
            Map.entry("분할상환", -65),
            Map.entry("초입금", -30),
            Map.entry("대출이자", -70),
            Map.entry("이자지원", -55),
            Map.entry("이차보전", -65),
            Map.entry("융자", -65),
            Map.entry("보증료", -45),

            // 세제 공제 문맥. 금융상품의 공제가 아님
            Map.entry("소득공제", -70),
            Map.entry("소득 공제", -70),
            Map.entry("세액공제", -70),
            Map.entry("세액 공제", -70),
            Map.entry("근로·사업소득 공제", -100),
            Map.entry("근로ㆍ사업소득 공제", -100),
            Map.entry("등록금 공제", -80),
            Map.entry("문화비 소득공제", -100)
    );

    public ProductClassification classify(JsonNode policy) {
        if (!isFinanceCandidate(policy)) {
            return ProductClassification.EXCLUDED;
        }

        if (isLoanOrDebtProduct(policy)) {
            return ProductClassification.LOAN_EXCLUDED;
        }

        int score = financeScore(policy);

        if (score >= FINANCIAL_PRODUCT_THRESHOLD) {
            return ProductClassification.FINANCIAL_PRODUCT;
        }

        return ProductClassification.UNCLASSIFIED;
    }

    private boolean isFinanceCandidate(JsonNode policy) {
        String category = text(policy, "mclsfNm");

        return FINANCE_CATEGORY.equals(category)
                || hasStrongFinancialProductSignal(policy);
    }

    private boolean hasStrongFinancialProductSignal(JsonNode policy) {
        String title = defaultString(text(policy, "plcyNm"));
        String value = classifierText(policy);

        if (containsAny(title, STRONG_PRODUCT_TITLE_TERMS)) {
            return true;
        }

        return containsAny(value, STRONG_PRODUCT_CONTENT_TERMS);
    }

    private boolean isLoanOrDebtProduct(JsonNode policy) {
        String title = defaultString(text(policy, "plcyNm"));
        String keywords = defaultString(text(policy, "plcyKywdNm"));
        String methodCode = defaultString(text(policy, "plcyPvsnMthdCd"));
        String value = classifierText(policy);

        String titleAndKeywords = title + " " + keywords;

        boolean protectedSavingsProduct = containsAny(title, PROTECTED_SAVINGS_PRODUCT_TERMS);

        if (!protectedSavingsProduct && containsAny(titleAndKeywords, LOAN_TITLE_KEYWORDS)) {
            return true;
        }

        if (containsAny(titleAndKeywords, DEBT_RECOVERY_KEYWORDS)) {
            return true;
        }

        if (!hasStrongFinancialProductSignal(policy)
                && containsAny(value, List.of("대출금", "대출잔액", "융자지원", "이차보전", "채무분할", "분할상환", "연체이자"))) {
            return true;
        }

        return LOAN_METHOD_CODES.stream()
                .anyMatch(code -> hasCode(methodCode, code))
                && !hasStrongFinancialProductSignal(policy);
    }

    private int financeScore(JsonNode policy) {
        String value = classifierText(policy);

        int positiveScore = weightedScore(value, POSITIVE_WEIGHT_MAP);
        int negativeScore = weightedScore(value, NEGATIVE_WEIGHT_MAP);

        int score = positiveScore + negativeScore;

        if (hasSavingsAccumulationStructure(value)) {
            score += 25;
        }

        if (hasMaturityStructure(value)) {
            score += 15;
        }

        if (hasAssetFormationStructure(value)) {
            score += 20;
        }

        return score;
    }

    private boolean hasSavingsAccumulationStructure(String value) {
        return containsAny(value, List.of("본인저축", "본인 저축", "월 저축", "저축액", "적립금", "납입"))
                && containsAny(value, List.of("매칭", "지원금", "적립", "만기", "이자"));
    }

    private boolean hasMaturityStructure(String value) {
        return containsAny(value, List.of("만기", "목돈"))
                && containsAny(value, List.of("적금", "예금", "통장", "계좌", "공제", "저축"));
    }

    private boolean hasAssetFormationStructure(String value) {
        return contains(value, "자산형성")
                && containsAny(value, List.of("저축", "적립", "통장", "계좌", "공제"));
    }

    private int weightedScore(String value, Map<String, Integer> weightMap) {
        return weightMap.entrySet().stream()
                .map(entry -> StringUtils.countOccurrencesOf(value, entry.getKey()) * entry.getValue())
                .reduce(Integer::sum)
                .orElse(0);
    }

    private String classifierText(JsonNode policy) {
        return String.join(" ",
                        defaultString(text(policy, "plcyNm")),
                        defaultString(text(policy, "plcyKywdNm")),
                        defaultString(text(policy, "plcyExplnCn")),
                        defaultString(text(policy, "lclsfNm")),
                        defaultString(text(policy, "mclsfNm")),
                        defaultString(text(policy, "plcySprtCn")),
                        defaultString(text(policy, "earnEtcCn")),
                        defaultString(text(policy, "addAplyQlfcCndCn")),
                        defaultString(text(policy, "ptcpPrpTrgtCn"))
                )
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private boolean hasCode(String rawCode, String targetCode) {
        return rawCode != null && rawCode.replaceFirst("^0+", "").equals(targetCode);
    }

    private boolean contains(String value, String token) {
        return value != null && value.contains(token);
    }

    private boolean containsAny(String value, List<String> tokens) {
        return value != null && tokens.stream().anyMatch(value::contains);
    }
}