package apptive.fin.search.controller;

import apptive.fin.search.dto.DynamicFormResponseDto;
import apptive.fin.search.dto.OptionRequestDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.service.DynamicFormService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final DynamicFormService dynamicFormService;

    @PostMapping("/dynamic-form")
    public DynamicFormResponseDto dynamicForm(@Valid @RequestBody SearchRequestDto searchRequestDto) {
        return dynamicFormService.calcFormCondition(searchRequestDto);
    }

    @PostMapping
    public SearchRequestDto search(@Valid @RequestBody SearchRequestDto searchRequestDto) {
        return searchRequestDto;
    }

}
