package apptive.fin.category;

import apptive.fin.category.entity.CategoryOption;
import apptive.fin.category.repository.CategoryOptionRepository;
import apptive.fin.category.service.CategoryOptionService;
import apptive.fin.search.KeywordValueEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryOptionServiceTest {

    @Mock
    private CategoryOptionRepository categoryOptionRepository;

    @InjectMocks
    private CategoryOptionService categoryOptionService;

    @Test
    void 유효한_코드가_있는_옵션만_키워드맵으로_반환한다() {
        when(categoryOptionRepository.findAll()).thenReturn(List.of(
                createOption(1L, "STATUS_UNEMPLOYED"),
                createOption(2L, "STATUS_MILITARY"),
                createOption(3L, null),
                createOption(4L, " "),
                createOption(5L, "NOT_EXISTING_KEYWORD")
        ));

        Map<Long, KeywordValueEnum> result = categoryOptionService.getOptionMap();

        assertThat(result).hasSize(2);
        assertThat(result).containsEntry(1L, KeywordValueEnum.STATUS_UNEMPLOYED);
        assertThat(result).containsEntry(2L, KeywordValueEnum.STATUS_MILITARY);
        assertThat(result).doesNotContainKeys(3L, 4L, 5L);

        verify(categoryOptionRepository).findAll();
    }

    @Test
    void 매핑가능한_코드가_없으면_빈_맵을_반환한다() {
        when(categoryOptionRepository.findAll()).thenReturn(List.of(
                createOption(1L, null),
                createOption(2L, ""),
                createOption(3L, "INVALID")
        ));

        Map<Long, KeywordValueEnum> result = categoryOptionService.getOptionMap();

        assertThat(result).isEmpty();

        verify(categoryOptionRepository).findAll();
    }

    private CategoryOption createOption(Long id, String code) {
        CategoryOption option = new CategoryOption();
        ReflectionTestUtils.setField(option, "id", id);
        ReflectionTestUtils.setField(option, "code", code);
        return option;
    }
}
