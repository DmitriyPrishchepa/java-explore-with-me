package ru.practicum.public_api.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin_api.categories.CategoryService;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.exception.exceptions.ApiError;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryControllerPublic {
    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getCategories(
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            int fromValue = Integer.parseInt(String.valueOf(from));
            int sizeValue = Integer.parseInt(String.valueOf(size));

            return service.getCategories(fromValue, sizeValue);
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(
            @PathVariable("catId") long catId
    ) {
        try {
            int catIdValue = Integer.parseInt(String.valueOf(catId));

            return service.getCategoryById(catIdValue);
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }
}
