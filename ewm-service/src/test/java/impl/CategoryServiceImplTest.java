package impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.admin_api.categories.CategoriesRepository;
import ru.practicum.admin_api.categories.CategoryService;
import ru.practicum.admin_api.categories.CategoryServiceImpl;
import ru.practicum.admin_api.categories.mapper.CategoryMapper;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.categories.NewCategoryDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryServiceImplTest {

    @Mock
    CategoriesRepository repository;

    @Mock
    CategoryService service;

    @Mock
    CategoryMapper mapper;

    @InjectMocks
    CategoryServiceImpl impl;

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

        Mockito.when(mapper.mapToDto(Mockito.eq(mockedCategory)))
                .thenReturn(dto);

        Mockito.when(mapper.mapFromDto(Mockito.eq(newCategoryDto)))
                .thenReturn(mockedCategory);

        Mockito.when(repository.save(Mockito.any(Category.class))).thenReturn(mockedCategory);
    }

    @Test
    void createCategoryTest_Success() {
        CategoryDto returnedDto = impl.createCategory(newCategoryDto);
        assertThat(returnedDto.getName(), is("Category"));
    }

    @Test
    void createCategoryTest_Error() {
        impl.createCategory(newCategoryDto);

        try {
            impl.createCategory(newCategoryDto);
        } catch (RuntimeException e) {
            assertEquals("could not execute statement; SQL [n/a]; " +
                    "constraint [uq_category_name]; nested \" +\n" +
                    "\"exception is org.hibernate.exception.ConstraintViolationException: \" +\n" +
                    "\"could not execute statement", e.getMessage());
        }
    }

    @Test
    void updateCategoryTest_Success() {
        newCategoryDto.setName("NewCategory");

        Mockito.when(mockedCategory.getName())
                .thenReturn("NewCategory");

        dto.setName("NewCategory");

        Mockito.when(mapper.mapToDto(Mockito.eq(mockedCategory)))
                .thenReturn(dto);

        Mockito.when(service.createCategory(Mockito.any(NewCategoryDto.class)))
                .thenReturn(dto);

        Mockito.when(mapper.mapFromDto(Mockito.eq(newCategoryDto)))
                .thenReturn(mockedCategory);

        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);

        Mockito.when(repository.getReferenceById(Mockito.anyLong())).thenReturn(mockedCategory);

        Mockito.when(repository.save(Mockito.any(Category.class))).thenReturn(mockedCategory);

        CategoryDto updated = impl.updateCategory(1L, newCategoryDto);

        assertThat(updated.getName(), is("NewCategory"));
    }

    @Test
    void updateCategory_Error_NotExists() {
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(false);

        try {
            impl.updateCategory(1L, newCategoryDto);
        } catch (RuntimeException e) {
            assertEquals("Category with id=" + 1L + " was not found", e.getMessage());
        }
    }

    @Test
    void updateCategory_Error_DuplicateName() {
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(repository.findByName(Mockito.anyString())).thenReturn(mockedCategory);

        try {
            impl.updateCategory(1L, newCategoryDto);
        } catch (RuntimeException e) {
            assertEquals("could not execute statement; SQL [n/a]; constraint [uq_category_name]; nested " +
                    "exception is org.hibernate.exception.ConstraintViolationException: " +
                    "could not execute statement", e.getMessage());
        }
    }

    @Test
    void deleteCategoryTest_Success() {
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);

        service.deleteCategory(1L);

        Mockito.verify(service, Mockito.times(1)).deleteCategory(Mockito.anyLong());
    }

    @Test
    void getCategoriesTest_Success() {
        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(1L);
        categoryDto1.setName("Category 1");

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setName("Category 2");

        List<Category> categories = List.of(
                Mockito.mock(Category.class),
                Mockito.mock(Category.class)
        );

        Mockito.when(mapper.mapToDto(categories.get(0))).thenReturn(categoryDto1);
        Mockito.when(mapper.mapToDto(categories.get(1))).thenReturn(categoryDto2);

        Page<Category> page = new PageImpl<>(categories);
        Mockito.when(repository.findAll(PageRequest.of(0, 2))).thenReturn(page);

        List<CategoryDto> result = impl.getCategories(0, 2);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getName(), is("Category 1"));
        assertThat(result.get(1).getName(), is("Category 2"));
    }

    @Test
    void getCategoryByIdTest_Success() {
        long id = 1L;
        Category mockedCategory = Mockito.mock(Category.class);
        Mockito.when(mockedCategory.getId()).thenReturn(id);
        Mockito.when(mockedCategory.getName()).thenReturn("Category");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(id);
        categoryDto.setName("Category");

        Mockito.when(repository.existsById(id)).thenReturn(true);
        Mockito.when(repository.getReferenceById(id)).thenReturn(mockedCategory);
        Mockito.when(mapper.mapToDto(mockedCategory)).thenReturn(categoryDto);

        CategoryDto result = impl.getCategoryById(id);

        assertThat(result.getId(), is(id));
        assertThat(result.getName(), is("Category"));
    }

    @Test
    void getCategoryByIdTest_Error_NotFound() {
        long id = 1L;
        Mockito.when(repository.existsById(id)).thenReturn(false);

        try {
            impl.getCategoryById(id);
        } catch (RuntimeException e) {
            assertEquals("Category with id=" + id + " was not found", e.getMessage());
        }
    }
}
