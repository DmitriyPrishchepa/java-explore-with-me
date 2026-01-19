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
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.admin_api.compilations.CompilationRepository;
import ru.practicum.admin_api.compilations.CompilationsController;
import ru.practicum.admin_api.compilations.CompilationsService;
import ru.practicum.admin_api.compilations.entities.Compilation;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.compilations.CompilationDto;
import ru.practicum.dtos.compilations.NewCompilationDto;
import ru.practicum.dtos.compilations.UpdateCompilationRequest;
import ru.practicum.dtos.events_public.EventShortDto;
import ru.practicum.dtos.users.UserShortDto;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.public_api.compilations.CompilationControllerPublic;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CompilationControllerTest {

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private CompilationsService compilationsService;

    @InjectMocks
    private CompilationsController compilationsController;

    @InjectMocks
    private CompilationControllerPublic compilationControllerPublic;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    UserShortDto userShortDto;
    User user;
    CategoryDto categoryDto;
    Category category;
    EventShortDto eventShortDto;
    EventShortDto eventShortDto2;
    NewCompilationDto newCompilationDto;
    UpdateCompilationRequest updateCompilationRequest;
    CompilationDto compilationDto;
    Compilation compilation;
    Event event;
    Event event2;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(compilationsController, compilationControllerPublic)
                .build();


        userShortDto = new UserShortDto();
        userShortDto.setId(1L);
        userShortDto.setName("UserShort");

        user = new User();
        user.setId(1L);
        user.setEmail("Email");
        user.setName("UserShort");

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("CategoryName");

        category = new Category();
        category.setId(1L);
        category.setName("CategoryName");

        eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setAnnotation("Эксклюзивность нашего шоу гарантирует привлечение " +
                "максимальной зрительской аудитории");
        eventShortDto.setCategory(categoryDto);
        eventShortDto.setConfirmedRequests(5);
        eventShortDto.setInitiator(userShortDto);
        eventShortDto.setPaid(true);
        eventShortDto.setTitle("Знаменитое шоу 'Летающая кукуруза'");
        eventShortDto.setViews(999);

        event = new Event();
        event.setId(1L);
        event.setAnnotation(eventShortDto.getAnnotation());
        event.setCategory(category);
        event.setInitiator(user);
        event.setTitle("Концерт рок-группы 'Java Core'");

        //------------------------------------

        event2 = new Event();
        event2.setId(2L);
        event2.setAnnotation(eventShortDto.getAnnotation());
        event2.setCategory(category);
        event2.setInitiator(user);
        event2.setTitle("Летние концерты");


        //--------------------------------------

        eventShortDto2 = new EventShortDto();
        eventShortDto2.setId(1L);
        eventShortDto2.setAnnotation("За почти три десятилетия группа 'Java Core' " +
                "закрепились на сцене как группа, объединяющая поколения.");
        eventShortDto2.setCategory(categoryDto);
        eventShortDto2.setConfirmedRequests(5);
        eventShortDto2.setInitiator(userShortDto);
        eventShortDto2.setPaid(true);
        eventShortDto2.setTitle("Концерт рок-группы 'Java Core'");
        eventShortDto2.setViews(999);

        newCompilationDto = new NewCompilationDto();
        newCompilationDto.setEvents(List.of(1, 2, 3));
        newCompilationDto.setPinned(false);
        newCompilationDto.setTitle("Летние концерты");

        compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Летние концерты");
        compilation.setPinned(true);

        updateCompilationRequest = new UpdateCompilationRequest();
        updateCompilationRequest.setEvents(List.of(0));
        updateCompilationRequest.setPinned(true);
        updateCompilationRequest.setTitle("Необычные фотозоны");

        compilationDto = new CompilationDto();
        compilationDto.setEvents(List.of(eventShortDto, eventShortDto2));
        compilationDto.setPinned(true);
    }

    @Test
    void createCompilationTest() throws Exception {
        Mockito.when(compilationsService.addCompilation(Mockito.any(NewCompilationDto.class)))
                .thenReturn(compilationDto);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events", hasSize(2)));
    }

    @Test
    void updateCompilationTest() throws Exception {

        Mockito.when(compilationsService.updateCompilationInfo(Mockito.anyLong(), Mockito.any(UpdateCompilationRequest.class)))
                .thenReturn(compilationDto);

        mvc.perform(patch("/admin/compilations/" + 1L)
                        .content(mapper.writeValueAsString(updateCompilationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(compilationDto.getTitle()));
    }

    @Test
    void deleteCompilation() throws Exception {

        mvc.perform(delete("/admin/compilations/" + 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCompilations() throws Exception {

        Mockito.when(compilationsService.getCompilations(Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(compilationDto));

        mvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pinned", is(true)));
    }

    @Test
    void getCompilationById() throws Exception {
        Mockito.when(compilationsService.getCompilationById(Mockito.anyLong()))
                .thenReturn(compilationDto);

        mvc.perform(get("/compilations/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pinned", is(true)));
    }
}
