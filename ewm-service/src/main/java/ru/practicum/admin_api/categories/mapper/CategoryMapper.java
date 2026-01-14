package ru.practicum.admin_api.categories.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.categories.NewCategoryDto;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {
    public CategoryDto mapToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public Category mapFromDto(NewCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
