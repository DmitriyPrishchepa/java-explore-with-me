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
import ru.practicum.admin_api.categories.mapper.CategoryMapper;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.admin_api.compilations.CompilationEventsRepository;
import ru.practicum.admin_api.compilations.CompilationRepository;
import ru.practicum.admin_api.compilations.CompilationsServiceImpl;
import ru.practicum.admin_api.compilations.entities.Compilation;
import ru.practicum.admin_api.compilations.mapper.CompilationsEventsMapper;
import ru.practicum.admin_api.users.mapper.UserMapper;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.compilations.CompilationDto;
import ru.practicum.dtos.compilations.NewCompilationDto;
import ru.practicum.dtos.compilations.UpdateCompilationRequest;
import ru.practicum.dtos.events_public.EventShortDto;
import ru.practicum.dtos.users.UserShortDto;
import ru.practicum.private_api.events.EventsRepository;
import ru.practicum.private_api.events.model.Event;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CompilationServiceImplTest {

    @Mock
    private EventsRepository eventsRepository;
    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private CompilationEventsRepository compilationEventsRepository;
    @Mock
    private CompilationsEventsMapper compilationsEventsMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CompilationsServiceImpl impl;

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

        Mockito.when(categoryMapper.mapToDto(Mockito.any(Category.class)))
                .thenReturn(categoryDto);

        Mockito.when(userMapper.mapToShortDto(Mockito.any(User.class)))
                .thenReturn(userShortDto);

        Mockito.when(compilationsEventsMapper.toDto(Mockito.any(Event.class)))
                .thenReturn(eventShortDto);

        Mockito.when(compilationRepository.findByTitle(Mockito.anyString()))
                .thenReturn(null);

        Mockito.when(compilationRepository.save(Mockito.any(Compilation.class)))
                .thenReturn(compilation);

        Mockito.when(eventsRepository.findByIdIn(Mockito.anyList()))
                .thenReturn(List.of(event, event2));
    }

    @Test
    void createCompilation_Success() {
        CompilationDto dto = impl.addCompilation(newCompilationDto);

        assertEquals("Летние концерты", dto.getTitle());
    }

    @Test
    void createCompilation_Error() {
        Mockito.when(compilationRepository.findByTitle(Mockito.anyString()))
                .thenReturn(compilation);

        try {
            impl.addCompilation(newCompilationDto);
        } catch (RuntimeException e) {
            assertEquals("could not execute statement; SQL [n/a]; constraint [uq_compilation_name]; " +
                    "nested exception is org.hibernate.exception.ConstraintViolationException: " +
                    "could not execute statement", e.getMessage());
        }
    }

    @Test
    void updateCompilationInfo_Success() {

        Mockito.when(compilationRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(compilation));

        CompilationDto dto = impl.updateCompilationInfo(1L, updateCompilationRequest);

        assertEquals(2, dto.getEvents().size());
    }

    @Test
    void updateCompilationInfo_Error() {

        Mockito.when(compilationRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            impl.updateCompilationInfo(1L, updateCompilationRequest);
        } catch (RuntimeException e) {
            assertEquals("Compilation with id=" + 1 + " was not found", e.getMessage());
        }
    }

    @Test
    void deleteCompilationTest_Success() {
        Mockito.when(compilationRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        impl.deleteCompilation(1L);

        Mockito.verify(compilationRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteCompilationTest_Error() {
        Mockito.when(compilationRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        try {
            impl.deleteCompilation(1L);
        } catch (RuntimeException e) {
            assertEquals("Compilation with id=" + 1 + " was not found", e.getMessage());
        }
    }
}
