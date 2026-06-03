package apptive.fin.category.dto;

import java.util.List;

public record CategoryResponseDto(Long categoryId, String categoryName, List<OptionDto> options) {
    public static CategoryResponseDto of(
            Long categoryId, String categoryName, List<OptionDto> options
    ) {
        return new  CategoryResponseDto(categoryId, categoryName, options);
    }
}