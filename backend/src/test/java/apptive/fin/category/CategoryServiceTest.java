package apptive.fin.category;

import apptive.fin.category.dto.*;
import apptive.fin.category.repository.CategoryRepository;
import apptive.fin.category.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

    @Test
    void 카테고리_옵션_조회_성공() {
        List<CategoryFlatDto> mockData = List.of(
                new CategoryFlatDto(1L, "거주지역", 1L, "서울"),
                new CategoryFlatDto(1L, "거주지역", 2L, "부산"),
                new CategoryFlatDto(2L, "현재신분", 3L, "미취업")
        );

        given(categoryRepository.findAllWithOptions()).willReturn(mockData);

        List<CategoryResponseDto> result = categoryService.getCategories();

        assertThat(result).hasSize(2);

        // 첫 번째 카테고리 검증
        CategoryResponseDto category1 = result.get(0);
        assertThat(category1.categoryId()).isEqualTo(1L);
        assertThat(category1.categoryName()).isEqualTo("거주지역");
        assertThat(category1.options()).hasSize(2);

        assertThat(category1.options().get(0).optionValue()).isEqualTo("서울");
        assertThat(category1.options().get(1).optionValue()).isEqualTo("부산");

        // 두 번째 카테고리 검증
        CategoryResponseDto category2 = result.get(1);
        assertThat(category2.categoryId()).isEqualTo(2L);
        assertThat(category2.categoryName()).isEqualTo("현재신분");
        assertThat(category2.options()).hasSize(1);

        assertThat(category2.options().get(0).optionValue()).isEqualTo("미취업");
    }
}