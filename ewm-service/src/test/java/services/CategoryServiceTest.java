package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.admin_api.categories.CategoryService;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.categories.NewCategoryDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryServiceTest {

    @Mock
    CategoryService service;

    CategoryDto dto;
    Category mockedCategory;
    NewCategoryDto newCategoryDto;

    @BeforeEach
    void setUp() {

        Mockito.when(service.createCategory(Mockito.any(NewCategoryDto.class)))
                .thenReturn(dto);

        dto = new CategoryDto();
        dto.setId(1L);
        dto.setName("Category");

        newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Category");

        mockedCategory = Mockito.mock(Category.class);
        Mockito.when(mockedCategory.getId()).thenReturn(1L);
        Mockito.when(mockedCategory.getName()).thenReturn("Category");
    }

    @Test
    void createCategoryTest_Success() {

        Mockito.when(service.createCategory(Mockito.any(NewCategoryDto.class)))
                .thenReturn(dto);

        CategoryDto returnedCategory = service.createCategory(newCategoryDto);

        assertThat(returnedCategory.getName(), is("Category"));
    }


    @Test
    void updateCategoryTest_Success() {

        CategoryDto newDto = new CategoryDto();
        newDto.setId(1L);
        newDto.setName("Category2");

        NewCategoryDto newCategoryDto1 = new NewCategoryDto();
        newCategoryDto1.setName("Category2");

        Mockito.when(service.updateCategory(Mockito.anyLong(), Mockito.any(NewCategoryDto.class)))
                .thenReturn(newDto);

        CategoryDto returnedCategory = service.updateCategory(1L, newCategoryDto1);

        assertThat(returnedCategory.getName(), is("Category2"));
    }

    @Test
    void deleteCategoryTest_Success() {

        service.deleteCategory(1L);

        Mockito.verify(service, Mockito.times(1)).deleteCategory(Mockito.anyLong());
    }

}
