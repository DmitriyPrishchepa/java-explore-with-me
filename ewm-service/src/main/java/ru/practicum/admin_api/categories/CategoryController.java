package ru.practicum.admin_api.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.categories.NewCategoryDto;
import ru.practicum.exception.exceptions.ApiError;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@Validated
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    public CategoryDto createCategory(
            @RequestBody NewCategoryDto dto
    ) {
        if (dto.getName().isBlank()) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Field: name. Error: must not be blank. Value: null"
            );
        }

        return service.createCategory(dto);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto updateCategory(
            @PathVariable("categoryId") long categoryId,
            @RequestBody NewCategoryDto dto
    ) {
        return service.updateCategory(categoryId, dto);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(
            @PathVariable("categoryId") long categoryId
    ) {
        service.deleteCategory(categoryId);
    }
}
