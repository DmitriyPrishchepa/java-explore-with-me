package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import ru.practicum.dtos.Location;
import ru.practicum.dtos.events.State;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.EventsRepository;
import ru.practicum.private_api.events.EventsService;
import ru.practicum.private_api.events.mapper.EventMapper;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.NewEventDto;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;
import ru.practicum.private_api.events.validation.UpdateEventValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventsServiceTest {

    @Mock
    EventsRepository eventsRepository;

    @Mock
    EventsService service;

    @Mock
    UpdateEventValidator validator;

    @Mock
    UserRepository userRepository;

    @Mock
    CategoriesRepository categoriesRepository;

    @Mock
    EventMapper mapper;

    Event eventDto;
    Event mockedEvent;
    NewEventDto newEventDto;
    Category category;
    User initiator;
    Location location;

    @BeforeEach
    void setUp() {

        Mockito.when(service.addEvent(Mockito.anyLong(), Mockito.any(NewEventDto.class)))
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
        Mockito.when(mockedEvent.getAnnotation()).thenReturn(eventDto.getAnnotation());
        Mockito.when(mockedEvent.getCategory()).thenReturn(eventDto.getCategory());
        Mockito.when(mockedEvent.getConfirmedRequests()).thenReturn(eventDto.getConfirmedRequests());
        Mockito.when(mockedEvent.getCreatedOn()).thenReturn(eventDto.getCreatedOn());
        Mockito.when(mockedEvent.getDescription()).thenReturn(eventDto.getDescription());
        Mockito.when(mockedEvent.getId()).thenReturn(eventDto.getId());
        Mockito.when(mockedEvent.getInitiator()).thenReturn(eventDto.getInitiator());
        Mockito.when(mockedEvent.getLocation()).thenReturn(eventDto.getLocation());
        Mockito.when(mockedEvent.getParticipantLimit()).thenReturn(eventDto.getParticipantLimit());
        Mockito.when(mockedEvent.getPublishedOn()).thenReturn(eventDto.getPublishedOn());
        Mockito.when(mockedEvent.getState()).thenReturn(eventDto.getState());
        Mockito.when(mockedEvent.getTitle()).thenReturn(eventDto.getTitle());

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(categoriesRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(eventsRepository.existsById(Mockito.anyLong())).thenReturn(true);

        Mockito.when(mapper.fromDto(Mockito.any(NewEventDto.class))).thenReturn(eventDto);

        Mockito.when(categoriesRepository.getReferenceById(Mockito.anyLong())).thenReturn(category);
        Mockito.when(userRepository.getReferenceById(Mockito.anyLong())).thenReturn(initiator);

        Mockito.when(eventsRepository.save(Mockito.any(Event.class))).thenReturn(eventDto);
    }

    @Test
    void createEventTest_Success() {

        Mockito.when(service.addEvent(Mockito.anyLong(), Mockito.any(NewEventDto.class)))
                .thenReturn(eventDto);

        Event event = service.addEvent(1L, newEventDto);

        assertEquals("Сплав на байдарках похож на полет.", event.getAnnotation());
        assertEquals("Сплав на байдарках похож на полет. На спокойной воде — это парение. " +
                "На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, " +
                "феерические эмоции, яркие впечатления.", event.getDescription());
    }

    @Test
    void createEventTest_UserNotExist() {

        Mockito.when(service.updateEvent(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(UpdateEventUserRequest.class)))
                .thenThrow(ApiError.class);

        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(false);

        try {
            service.addEvent(1L, newEventDto);
        } catch (RuntimeException e) {
            assertEquals("User with id=1 was not found", e.getMessage());
        }
    }

    @Test
    void createEventTest_CategoryNotExists() {

        Mockito.when(service.updateEvent(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(UpdateEventUserRequest.class)))
                .thenThrow(ApiError.class);

        Mockito.when(categoriesRepository.existsById(Mockito.anyLong())).thenReturn(false);

        try {
            service.addEvent(1L, newEventDto);
        } catch (RuntimeException e) {
            assertEquals("Category with id=1 was not found", e.getMessage());
        }
    }

    @Test
    void updateEventTest_Success() {
        eventDto.setTitle("Updated Event Title");

        Mockito.when(eventsRepository.findByInitiatorIdAndId(
                Mockito.anyLong(), Mockito.anyLong())
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

        Mockito.when(validator.validateEventAndUpdate(Mockito.any(Event.class), Mockito.any(Category.class),
                Mockito.any(UpdateEventUserRequest.class))).thenReturn(updatedEvent);

        Mockito.when(service.updateEvent(
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.any(UpdateEventUserRequest.class))).thenReturn(eventDto);

        Event updatingdEvent = service.updateEvent(1L, 1L, request);

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

        Mockito.when(eventsRepository.findAll(Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(eventsList));

        Mockito.when(service.getEvents(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(eventsList);

        List<Event> events = service.getEvents(1L, 0, 10);

        assertEquals(2, events.size());
        assertEquals("Event 1", events.get(0).getTitle());
        assertEquals("Event 2", events.get(1).getTitle());
    }

    @Test
    void getEvents_ReturnEmptyList() {
        List<Event> eventsList = new ArrayList<>();

        Mockito.when(eventsRepository.findAll(Mockito.any(PageRequest.class)))
                .thenReturn(new PageImpl<>(eventsList));

        Mockito.when(service.getEvents(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(eventsList);

        List<Event> events = service.getEvents(1L, 0, 10);

        assertEquals(0, events.size());
    }

    @Test
    void getEventById_Success() {
        Mockito.when(eventsRepository.findByInitiatorIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(eventDto);

        Mockito.when(service.getEventById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(eventDto);

        Event event = service.getEventById(1L, 1L);

        assertEquals("Сплав на байдарках", event.getTitle());
    }
}
