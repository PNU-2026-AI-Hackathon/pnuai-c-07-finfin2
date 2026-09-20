package apptive.fin.category.service;

import apptive.fin.category.entity.CategoryOption;
import apptive.fin.category.repository.CategoryOptionRepository;
import apptive.fin.category.repository.CategoryRepository;
import apptive.fin.search.enums.KeywordValueEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryOptionService {

    private final CategoryOptionRepository categoryOptionRepository;

    @Cacheable(cacheNames = "keywordOptionMap")
    public Map<Long, KeywordValueEnum> getOptionMap() {
        List<CategoryOption> allOptions = categoryOptionRepository.findAll();
        Map<Long, KeywordValueEnum> map = new HashMap<>();

        for (CategoryOption option : allOptions) {
            if (option.getCode() != null && !option.getCode().isBlank()) {
                KeywordValueEnum keyword = KeywordValueEnum.from(option.getCode());
                if (keyword != null) {
                    map.put(option.getId(), keyword);
                }
            }
        }

        return map;
    }

    @Cacheable(cacheNames = "keywordOptionMappings")
    public Map<Long, OptionMapping> getOptionMappings() {
        List<CategoryOption> allOptions = categoryOptionRepository.findAll();
        Map<Long, OptionMapping> map = new HashMap<>();

        for (CategoryOption option : allOptions) {
            if (option.getCategory() == null || option.getCode() == null || option.getCode().isBlank()) {
                continue;
            }
            KeywordValueEnum keyword = KeywordValueEnum.from(option.getCode());
            if (keyword != null) {
                map.put(option.getId(), new OptionMapping(option.getCategory().getId(), keyword));
            }
        }
        return map;
    }

    public record OptionMapping(Long categoryId, KeywordValueEnum keyword) {
    }

}
