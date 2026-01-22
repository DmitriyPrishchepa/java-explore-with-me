package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.admin_api.categories.CategoryController;
import ru.practicum.admin_api.categories.CategoryService;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.categories.NewCategoryDto;
import ru.practicum.public_api.categories.CategoryControllerPublic;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryControllerTest {
    @Mock
    private CategoryService service;

    @InjectMocks
    private CategoryController controller;

    @InjectMocks
    private CategoryControllerPublic categoryControllerPublic;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private CategoryDto categoryDto;
    //    private UserDto userDto2;
    private NewCategoryDto newCategoryDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller, categoryControllerPublic)
                .build();

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("category_1");

        newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName(categoryDto.getName());
    }

    @Test
    void saveNewCategory() throws Exception {
        when(service.createCategory(any()))
                .thenReturn(categoryDto);

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    void updateCategory() throws Exception {

        newCategoryDto.setName("updatedCategory");

        categoryDto.setName("updatedCategory");


        when(service.updateCategory(Mockito.anyLong(), Mockito.any(NewCategoryDto.class)))
                .thenReturn(categoryDto);

        mvc.perform(patch("/admin/categories/" + 1L)
                        .param("categoryId", String.valueOf(1L))
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("updatedCategory")));
    }

    @Test
    void deleteCategoryTest() throws Exception {
        mvc.perform(delete("/admin/categories/" + 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).deleteCategory(1L);
    }

    @Test
    void getCategories() throws Exception {
        Mockito.when(service.getCategories(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(categoryDto));

        mvc.perform(get("/categories")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name", is(categoryDto.getName())));
    }

    @Test
    void getCategoryById() throws Exception {
        Mockito.when(service.getCategoryById(Mockito.anyLong()))
                .thenReturn(categoryDto);

        mvc.perform(get("/categories/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }
}
