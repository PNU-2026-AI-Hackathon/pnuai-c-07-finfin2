package apptive.fin.category.controller;

import apptive.fin.category.dto.CategoryResponseDto;
import apptive.fin.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponseDto> getCategories(){
        return categoryService.getCategories();
    }
}
