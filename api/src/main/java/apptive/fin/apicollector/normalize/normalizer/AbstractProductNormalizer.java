package apptive.fin.apicollector.normalize.normalizer;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.normalize.extractor.KeywordExtractor;
import apptive.fin.apicollector.product.KeywordValueEnum;
import apptive.fin.apicollector.util.JsonNodes;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractProductNormalizer {

    protected ProductDraft extractKeywords(
            KeywordExtractor extractor,
            ProductDraft draft

    ) {
        List<ProductPropertyDraft> productPropertyDrafts = new ArrayList<>();
        for (ProductPropertyDraft property : draft.properties()) {
            List<KeywordValueEnum> keywords = extractor.extract(draft, property);

            productPropertyDrafts.add(
                    property
                            .toBuilder()
                            .keywords(keywords)
                            .build()
            );
        }
        return draft.toBuilder()
                .properties(productPropertyDrafts)
                .build();
    }

    protected String firstText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            String value = JsonNodes.text(node, fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    protected String joinContent(JsonNode node, String... fieldNames) {
        List<String> parts = new ArrayList<>();
        for (String fieldName : fieldNames) {
            String value = JsonNodes.text(node, fieldName);
            if (value != null) {
                parts.add(value);
            }
        }
        return parts.isEmpty() ? null : String.join("\n\n", parts);
    }

    protected String collapseWhitespace(String value) {
        if (value == null) return null;
        String collapsed = value.replaceAll("\\s+", " ").trim();
        return collapsed.isEmpty() ? null : collapsed;
    }


    protected List<KeywordValueEnum> keywordsFromText(String... values) {
        Set<KeywordValueEnum> keywords = EnumSet.noneOf(KeywordValueEnum.class);
        List<String> nonBlankValues = new ArrayList<>();
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.isBlank()) {
                    nonBlankValues.add(value);
                }
            }
        }
        String joined = String.join(" ", nonBlankValues);

        addIfContains(keywords, joined, KeywordValueEnum.REGION_SEOUL, "서울");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_BUSAN, "부산");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_DAEGU, "대구");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_INCHEON, "인천");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_GWANGJU, "광주");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_DAEJEON, "대전");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_ULSAN, "울산");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_SEJONG, "세종");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_GYEONGGI, "경기");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_GANGWON, "강원");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_CHUNGBUK, "충북", "충청북도");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_CHUNGNAM, "충남", "충청남도");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_JEONBUK, "전북", "전라북도");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_JEONNAM, "전남", "전라남도");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_GYEONGBUK, "경북", "경상북도");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_GYEONGNAM, "경남", "경상남도");
        addIfContains(keywords, joined, KeywordValueEnum.REGION_JEJU, "제주");
        addIfContains(keywords, joined, KeywordValueEnum.BENEFIT_TAX_FREE, "비과세", "세제");
        addIfContains(keywords, joined, KeywordValueEnum.BENEFIT_GOV_SUBSIDY, "보조금", "지원금", "수당");
        // BENEFIT_MAX_INTEREST는 정적 태깅하지 않는다. "최고이율(상위 30%)"은 검색 시점에 백엔드가
        // 결과셋 maxRate 기준으로 동적 판정하므로(PRD A-2), 텍스트("우대금리"/"최고금리") 기반 정적 태그는 제거.
        addIfContains(keywords, joined, KeywordValueEnum.INTEREST_SAVINGS, "적금", "예금", "저축");
        addIfContains(keywords, joined, KeywordValueEnum.INTEREST_LOAN, "대출");
        addIfContains(keywords, joined, KeywordValueEnum.BANK_FIRST_TRANSACTION, "최초", "첫거래");
        addIfContains(keywords, joined, KeywordValueEnum.BANK_SALARY_TRANSFER, "급여");
        addIfContains(keywords, joined, KeywordValueEnum.BANK_CARD_USAGE, "카드");
        addIfContains(keywords, joined, KeywordValueEnum.BANK_AUTO_TRANSFER, "자동이체");
        addIfContains(keywords, joined, KeywordValueEnum.BANK_MARKETING, "마케팅", "상품서비스", "개인정보");
        addIfContains(keywords, joined, KeywordValueEnum.BANK_REDEPOSIT, "재예치", "재가입");
        addIfContains(keywords, joined, KeywordValueEnum.BANK_ONLINE_JOIN, "인터넷", "스마트폰", "모바일", "비대면");

        return List.copyOf(keywords);
    }

    private void addIfContains(
            Set<KeywordValueEnum> keywords,
            String value,
            KeywordValueEnum keyword,
            String... tokens
    ) {
        if (value == null) {
            return;
        }

        for (String token : tokens) {
            if (value.contains(token)) {
                keywords.add(keyword);
                return;
            }
        }
    }
}
