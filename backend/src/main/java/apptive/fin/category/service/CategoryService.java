package apptive.fin.category.service;

import apptive.fin.category.dto.*;
import apptive.fin.category.repository.CategoryRepository;
import apptive.fin.global.error.BusinessException;
import apptive.fin.search.CategoryIdEnum;
import apptive.fin.search.KeywordValueEnum;
import apptive.fin.search.SearchErrorCode;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.ResolvedKeywords;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDto> getCategories() {
        List<CategoryFlatDto> flats = categoryRepository.findAllWithOptions();

        Map<Long, List<CategoryFlatDto>> grouped =
                flats.stream()
                        .collect(Collectors.groupingBy(
                                CategoryFlatDto::categoryId,
                                LinkedHashMap::new,
                                Collectors.toList()
                        ));
        return grouped.values().stream()
                .map(list -> {
                    if (list.isEmpty()) return null;

                    Long categoryId = list.get(0).categoryId();
                    String categoryName = list.get(0).categoryName();

                    List<OptionDto> options = list.stream()
                            .map(f -> new OptionDto(
                                    f.optionId(),
                                    f.optionValue()
                            ))
                            .toList();
                    return new CategoryResponseDto(categoryId, categoryName, options);
                })
                .toList();
    }



}
