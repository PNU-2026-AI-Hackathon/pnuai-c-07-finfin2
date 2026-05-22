package apptive.fin.apicollector.normalize.extractor.keywords;

import apptive.fin.apicollector.normalize.dto.ProductDraft;
import apptive.fin.apicollector.normalize.dto.ProductPropertyDraft;
import apptive.fin.apicollector.product.KeywordValueEnum;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RegionKeywordRecognizer implements KeywordRecognizer {

    @Override
    public List<KeywordValueEnum> recognize(ProductDraft productDraft, ProductPropertyDraft propertyDraft) {

        String providerName = propertyDraft.providerName();
        Set<KeywordValueEnum> keywords = new HashSet<>();
        if (providerName != null && !providerName.contains("은행")) {
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_SEOUL, "서울");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_BUSAN, "부산");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_DAEGU, "대구");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_INCHEON, "인천");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_GWANGJU, "광주");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_DAEJEON, "대전");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_ULSAN, "울산");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_SEJONG, "세종");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_GYEONGGI, "경기");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_GANGWON, "강원");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_CHUNGBUK, "충북", "충청북도");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_CHUNGNAM, "충남", "충청남도");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_JEONBUK, "전북", "전라북도");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_JEONNAM, "전남", "전라남도");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_GYEONGBUK, "경북", "경상북도");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_GYEONGNAM, "경남", "경상남도");
            addIfContains(keywords, providerName, KeywordValueEnum.REGION_JEJU, "제주");
        }


        if (!keywords.isEmpty())
            return keywords.stream().toList();


        return List.of(
                KeywordValueEnum.REGION_SEOUL,
                KeywordValueEnum.REGION_BUSAN,
                KeywordValueEnum.REGION_DAEGU,
                KeywordValueEnum.REGION_INCHEON,
                KeywordValueEnum.REGION_GWANGJU,
                KeywordValueEnum.REGION_DAEJEON,
                KeywordValueEnum.REGION_ULSAN,
                KeywordValueEnum.REGION_SEJONG,
                KeywordValueEnum.REGION_GYEONGGI,
                KeywordValueEnum.REGION_GANGWON,
                KeywordValueEnum.REGION_CHUNGBUK,
                KeywordValueEnum.REGION_CHUNGNAM,
                KeywordValueEnum.REGION_JEONBUK,
                KeywordValueEnum.REGION_JEONNAM,
                KeywordValueEnum.REGION_GYEONGBUK,
                KeywordValueEnum.REGION_GYEONGNAM,
                KeywordValueEnum.REGION_JEJU
        );
    }



}
