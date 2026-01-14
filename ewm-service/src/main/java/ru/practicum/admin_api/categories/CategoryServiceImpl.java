package ru.practicum.admin_api.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.admin_api.categories.mapper.CategoryMapper;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.categories.NewCategoryDto;
import ru.practicum.exception.exceptions.ApiError;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoriesRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto createCategory(NewCategoryDto dto) {

        checkDuplicateCategoryName(dto.getName());

        Category categoryToSave = mapper.mapFromDto(dto);
        Category saved = repository.save(categoryToSave);

        return mapper.mapToDto(saved);
    }

    @Override
    public CategoryDto updateCategory(long categoryId, NewCategoryDto dto) {
        checkExistsCategory(categoryId);
        checkDuplicateCategoryName(dto.getName());

        Category category = repository.getReferenceById(categoryId);
        category.setName(dto.getName());

        Category savedCategory = repository.save(category);
        return mapper.mapToDto(savedCategory);
    }

    @Override
    public void deleteCategory(long categoryId) {
        //если есть события events, связанные с категорией - выкинуть исключение
        checkExistsCategory(categoryId);
        repository.deleteById(categoryId);
    }

    public void checkExistsCategory(long categoryId) {
        if (!repository.existsById(categoryId)) {
            throw new ApiError(
                    HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "Category with id=" + categoryId + " was not found"
            );
        }
    }

    public void checkDuplicateCategoryName(String name) {
        Category categorySameByName = repository.findByName(name);

        if (categorySameByName != null) {
            throw new ApiError(
                    HttpStatus.CONFLICT,
                    "Integrity constraint has been violated.",
                    "could not execute statement; SQL [n/a]; constraint [uq_category_name]; nested " +
                            "exception is org.hibernate.exception.ConstraintViolationException: " +
                            "could not execute statement"
            );
        }
    }
}
