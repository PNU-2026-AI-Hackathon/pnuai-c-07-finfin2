package apptive.fin.search.controller;

import apptive.fin.auth.security.AuthUserDetails;
import apptive.fin.search.dto.DynamicFormResponseDto;
import apptive.fin.search.dto.ProductDetailRequestDto;
import apptive.fin.search.dto.ProductDetailResponseDto;
import apptive.fin.search.dto.ProductNameSearchDto;
import apptive.fin.search.dto.ProductSearchResultDto;
import apptive.fin.search.dto.SearchRequestDto;
import apptive.fin.search.service.DynamicFormService;
import apptive.fin.search.service.ProductDetailService;
import apptive.fin.search.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final DynamicFormService dynamicFormService;
    private final SearchService searchService;
    private final ProductDetailService productDetailService;

    @PostMapping("/dynamic-form")
    public DynamicFormResponseDto dynamicForm(@Valid @RequestBody SearchRequestDto searchRequestDto) {
        return dynamicFormService.calcFormCondition(searchRequestDto);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductSearchResultDto> search(
            @Valid @RequestBody SearchRequestDto searchRequestDto,
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        return ResponseEntity.ok(searchService.search(searchRequestDto, userDetails));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductNameSearchDto>> searchByName(
            @RequestParam(required = false, defaultValue = "") String searchInput
    ){
        if (searchInput.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(searchService.searchByName(searchInput));
    }

    @PostMapping("/products/{productId}/detail")
    public ResponseEntity<ProductDetailResponseDto> productDetail(
            @PathVariable Long productId,
            @RequestBody(required = false) ProductDetailRequestDto request,
            @AuthenticationPrincipal AuthUserDetails userDetails
    ) {
        return ResponseEntity.ok(productDetailService.getProductDetail(productId, request, userDetails));
    }

}
