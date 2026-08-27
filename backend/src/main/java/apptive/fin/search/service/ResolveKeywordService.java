package apptive.fin.search.service;

import apptive.fin.category.service.CategoryOptionService;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.enums.CategoryIdEnum;
import apptive.fin.search.enums.KeywordValueEnum;
import apptive.fin.search.SearchErrorCode;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ResolvedKeywords;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import apptive.fin.category.service.CategoryOptionService.OptionMapping;

@Service
@RequiredArgsConstructor
public class ResolveKeywordService {
    private final CategoryOptionService categoryOptionService;

    public ResolvedKeywords resolveKeywords(List<OptionRequestDto> options){
        Map<Long, OptionMapping> mapping = categoryOptionService.getOptionMappings();

        List<KeywordValueEnum> regions = new ArrayList<>();
        List<KeywordValueEnum> identities = new ArrayList<>();
        KeywordValueEnum savingPeriod = null;
        List<KeywordValueEnum> benefits = new ArrayList<>();
        List<KeywordValueEnum> bankConds = new ArrayList<>();

        for (OptionRequestDto option : options){
            OptionMapping optionMapping = mapping.get(option.optionId());
            if (optionMapping == null) continue;

            Long categoryId = option.categoryId();
            CategoryIdEnum category = CategoryIdEnum.fromId(categoryId)
                    .orElseThrow(()->new BusinessException(SearchErrorCode.OPTION_CATEGORY_NOT_FOUND));
            if (!categoryId.equals(optionMapping.categoryId())) {
                throw new BusinessException(SearchErrorCode.OPTION_CATEGORY_NOT_FOUND);
            }
            KeywordValueEnum kw = optionMapping.keyword();

            switch(category){
                case REGION -> regions.add(kw);
                case IDENTITY -> identities.add(kw);
                case BENEFIT -> benefits.add(kw);
                case BANK_COND -> bankConds.add(kw);
                case PERIOD -> savingPeriod = kw;
                case INTEREST -> {}
            }

        }
        return new ResolvedKeywords(regions, identities, savingPeriod, benefits, bankConds);
    }
}
