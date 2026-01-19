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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.admin_api.categories.CategoriesRepository;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.admin_api.users.UserRepository;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.events.SearchEventsDto;
import ru.practicum.dtos.events.State;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.EventsRepository;
import ru.practicum.private_api.events.EventsService;
import ru.practicum.private_api.events.EventsServiceImpl;
import ru.practicum.private_api.events.LocationRepository;
import ru.practicum.private_api.events.location.Location;
import ru.practicum.private_api.events.mapper.EventMapper;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.NewEventDto;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;
import ru.practicum.private_api.events.validation.EventUpdater;
import ru.practicum.private_api.events.validation.UpdateEventValidator;
import ru.practicum.public_api.events.SearchEventsDtoFiltered;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventsServiceImplTest {

    @Mock
    EventsRepository eventsRepository;

    @Mock
    EventsService service;

    @Mock
    LocationRepository locationRepository;

    @Mock
    UpdateEventValidator validator;

    @Mock
    EventUpdater eventUpdater;

    @Mock
    UserRepository userRepository;

    @Mock
    CategoriesRepository categoriesRepository;

    @Mock
    EventMapper mapper;

    @InjectMocks
    EventsServiceImpl impl;

    Event eventDto;
    Event mockedEvent;
    Event mockedEvent2;
    NewEventDto newEventDto;
    Category category;
    User initiator;
    Location location;

    @BeforeEach
    void setUp() {

        when(service.addEvent(anyLong(), Mockito.any(NewEventDto.class)))
                .thenReturn(eventDto);

        newEventDto = new NewEventDto();

        newEventDto.setAnnotation("Сплав на байдарках похож на полет.");
        newEventDto.setCategory(1);
        newEventDto.setDescription("Сплав на байдарках похож на полет. На спокойной воде — это парение. " +
                "На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, " +
                "феерические эмоции, яркие впечатления.");
        newEventDto.setEventDate("2024-12-31 15:10:05");
        location = new Location();
        location.setLat(55.754167F);
        location.setLon(37.62F);
        newEventDto.setLocation(new Location());
        newEventDto.setPaid(true);
        newEventDto.setParticipantLimit(10);
        newEventDto.setRequestModeration(false);
        newEventDto.setTitle("Сплав на байдарках");

        category = new Category();
        category.setId(1L);
        category.setName("Category");

        initiator = new User();

        initiator.setId(1L);
        initiator.setName("User");
        initiator.setEmail("Email");

        eventDto = new Event();

        eventDto.setAnnotation("Сплав на байдарках похож на полет.");
        eventDto.setCategory(category);
        eventDto.setConfirmedRequests(5);
        eventDto.setCreatedOn("2022-09-06 11:00:23");
        eventDto.setDescription("Сплав на байдарках похож на полет. На спокойной воде — это парение. " +
                "На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, " +
                "феерические эмоции, яркие впечатления.");
        eventDto.setId(1L);
        eventDto.setInitiator(initiator);
        eventDto.setLocation(location);
        eventDto.setPaid(true);
        eventDto.setParticipantLimit(10);
        eventDto.setPublishedOn("2022-09-06 15:10:05");
        eventDto.setState(State.PUBLISHED);
        eventDto.setTitle("Сплав на байдарках");
        eventDto.setViews(999);

        mockedEvent = Mockito.mock(Event.class);
        when(mockedEvent.getAnnotation()).thenReturn(eventDto.getAnnotation());
        when(mockedEvent.getCategory()).thenReturn(eventDto.getCategory());
        when(mockedEvent.getConfirmedRequests()).thenReturn(eventDto.getConfirmedRequests());
        when(mockedEvent.getCreatedOn()).thenReturn(eventDto.getCreatedOn());
        when(mockedEvent.getDescription()).thenReturn(eventDto.getDescription());
        when(mockedEvent.getId()).thenReturn(eventDto.getId());
        when(mockedEvent.getInitiator()).thenReturn(eventDto.getInitiator());
        when(mockedEvent.getLocation()).thenReturn(eventDto.getLocation());
        when(mockedEvent.getParticipantLimit()).thenReturn(eventDto.getParticipantLimit());
        when(mockedEvent.getPublishedOn()).thenReturn(eventDto.getPublishedOn());
        when(mockedEvent.getState()).thenReturn(eventDto.getState());
        when(mockedEvent.getTitle()).thenReturn(eventDto.getTitle());

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(categoriesRepository.existsById(anyLong())).thenReturn(true);
        when(eventsRepository.existsById(anyLong())).thenReturn(true);

        when(mapper.fromDto(Mockito.any(NewEventDto.class))).thenReturn(eventDto);

        when(categoriesRepository.getReferenceById(anyLong())).thenReturn(category);
        when(userRepository.getReferenceById(anyLong())).thenReturn(initiator);

        when(eventsRepository.save(Mockito.any(Event.class))).thenReturn(eventDto);
    }

    @Test
    void createEventTest_Success() {
        Event event = impl.addEvent(1L, newEventDto);

        assertEquals("Сплав на байдарках похож на полет.", event.getAnnotation());
        assertEquals("Сплав на байдарках похож на полет. На спокойной воде — это парение. " +
                "На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, " +
                "феерические эмоции, яркие впечатления.", event.getDescription());
    }

    @Test
    void createEventTest_UserNotExist() {

        when(userRepository.existsById(anyLong())).thenReturn(false);

        try {
            impl.addEvent(1L, newEventDto);
        } catch (RuntimeException e) {
            assertEquals("User with id=1 was not found", e.getMessage());
        }
    }

    @Test
    void createEventTest_CategoryNotExists() {

        when(categoriesRepository.existsById(anyLong())).thenReturn(false);

        try {
            impl.addEvent(1L, newEventDto);
        } catch (RuntimeException e) {
            assertEquals("Category with id=1 was not found", e.getMessage());
        }
    }

    @Test
    void updateEventTest_Success() {
        eventDto.setTitle("Updated Event Title");

        when(eventsRepository.findByInitiatorIdAndId(anyLong(),
                anyLong())
        ).thenReturn(eventDto);

        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setAnnotation("Сплав на байдарках похож на полет.");
        request.setCategory((int) category.getId());
        request.setDescription("Сплав на байдарках похож на полет. На спокойной воде — это парение. " +
                "На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, " +
                "феерические эмоции, яркие впечатления.");
        request.setLocation(location);
        request.setPaid(true);
        request.setParticipantLimit(10);
        request.setTitle("Updated Event Title");

        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated Event Title");

        when(eventUpdater.updateEvent(Mockito.any(Event.class), Mockito.any(Category.class),
                Mockito.any(UpdateEventUserRequest.class))).thenReturn(updatedEvent);

        Event updatingdEvent = impl.updateEvent(1L, 1L, request);

        assertEquals("Updated Event Title", updatingdEvent.getTitle());
    }

    @Test
    void getEvents_Success() {
        List<Event> eventsList = new ArrayList<>();
        Event event1 = new Event();
        event1.setTitle("Event 1");
        Event event2 = new Event();
        event2.setTitle("Event 2");
        eventsList.add(event1);
        eventsList.add(event2);

        when(eventsRepository.findAll(Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(eventsList));

        List<Event> events = impl.getEvents(1L, 0, 10);

        assertEquals(2, events.size());
        assertEquals("Event 1", events.get(0).getTitle());
        assertEquals("Event 2", events.get(1).getTitle());
    }

    @Test
    void getEvents_ReturnEmptyList() {
        List<Event> eventsList = new ArrayList<>();

        when(eventsRepository.findAll(Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(eventsList));

        List<Event> events = impl.getEvents(1L, 0, 10);

        assertEquals(0, events.size());
    }

    @Test
    void getEventById_Success() {
        when(eventsRepository.findByInitiatorIdAndId(anyLong(), anyLong()))
                .thenReturn(eventDto);

        Event event = impl.getEventById(1L, 1L);

        assertEquals("Сплав на байдарках", event.getTitle());
    }

    @Test
    void searchEventsFiltered_Success() {
        SearchEventsDtoFiltered dto = new SearchEventsDtoFiltered();
        dto.setText("Сплав");
        dto.setFrom(0);
        dto.setSize(10);

        List<Event> eventsList = new ArrayList<>();
        Event event1 = new Event();
        event1.setTitle("Сплав на байдарках");
        Event event2 = new Event();
        event2.setTitle("Поход в горы");
        eventsList.add(event1);
        eventsList.add(event2);

        when(eventsRepository.findPublishedEventsWithTextSearch(
                Mockito.any(State.class),
                Mockito.anyString(),
                Mockito.any(PageRequest.class)))
                .thenReturn(eventsList);

        List<Event> events = impl.searchEventsFiltered(dto);

        assertEquals(2, events.size());
        assertEquals("Сплав на байдарках", events.get(0).getTitle());
        assertEquals("Поход в горы", events.get(1).getTitle());
    }

    @Test
    void getEventByIdAndPublished_Success() {
        Event event = new Event();
        event.setId(1L);
        event.setState(State.PUBLISHED);

        when(eventsRepository.findById(anyLong()))
                .thenReturn(Optional.of(event));

        Event returnedEvent = impl.getEventByIdAndPublished(1L);
        assertEquals(event, returnedEvent);
    }

    @Test
    void getEventByIdAndPublished_EventNotFound() {
        long nonExistingEventId = 999L;

        when(eventsRepository.findById(nonExistingEventId))
                .thenReturn(Optional.empty());

        assertThrows(ApiError.class, () -> {
            impl.getEventByIdAndPublished(nonExistingEventId);
        });
    }

    @Test
    void searchEvents_Success() {
        SearchEventsDto dto = new SearchEventsDto();
        dto.setFrom(0);
        dto.setSize(10);
        dto.setUsers(List.of(1, 2));
        dto.setStates(List.of("PUBLISHED"));
        dto.setCategories(List.of(1, 2));
        dto.setRangeStart("2024-01-01 00:00:00");
        dto.setRangeEnd("2024-12-31 23:59:59");

        List<Event> eventsList = new ArrayList<>();
        Event event1 = new Event();
        event1.setTitle("Event 1");
        event1.setState(State.PUBLISHED);
        event1.setCategory(category);

        eventsList.add(event1);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Another Category");

        Event event2 = new Event();
        event2.setTitle("Event 2");
        event2.setState(State.PUBLISHED);
        event2.setCategory(category2);

        eventsList.add(event2);

        when(eventsRepository.findByInitiatorIdIn(Mockito.anyList(), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(eventsList));

        when(eventsRepository.findAllByStateInStates(Mockito.anyList(), Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(event1)));

        when(eventsRepository.findByEventDateBetween(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(List.of(event1));

        List<Event> events = impl.searchEvents(dto);

        assertEquals(1, events.size());
        assertEquals("Event 1", events.getFirst().getTitle());
    }

//    @Test
//    void testUpdateEventAndStatus_SuccessfulUpdate() {
//        // Подготовка данных
//        long eventId = 1L;
//        UpdateEventUserRequest request = new UpdateEventUserRequest();
//        request.setAnnotation("Updated annotation");
//        request.setStateAction(StateAction.PUBLISH_EVENT);
//        request.setEventDate("2024-01-01 00:00:00");
//        request.setCategory(1);
//
//        Category category = new Category();
//        category.setId(1L);
//
//        Event event = new Event();
//        event.setId(eventId);
//        event.setAnnotation("Updated annotation");
//        event.setState(State.PUBLISHED);
//        event.setCategory(category);
//        event.setCreatedOn("2023-12-31 00:00:00"); // Инициализация даты создания события
//
//        when(eventsRepository.findById(eventId)).thenReturn(Optional.of(event));
//        when(categoriesRepository.getReferenceById(Mockito.anyLong())).thenReturn(category);
//        when(eventsRepository.save(Mockito.any(Event.class)))
//                .thenReturn(event);
//        when(eventUpdater.updateEvent(
//                Mockito.any(Event.class),
//                Mockito.any(Category.class),
//                Mockito.any(UpdateEventUserRequest.class)))
//                .thenReturn(event);
//
//        Event updatedEvent = impl.updateEventAndStatus(eventId, request);
//
//        assertThat(updatedEvent.getAnnotation()).isEqualTo("Updated annotation");
//        assertThat(updatedEvent.getState()).isEqualTo(State.PUBLISHED);
//    }
}
