package ru.practicum.admin_api.categories;

import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.categories.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto dto);

    CategoryDto updateCategory(long categoryId, NewCategoryDto dto);

    void deleteCategory(long categoryId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long id);
}
