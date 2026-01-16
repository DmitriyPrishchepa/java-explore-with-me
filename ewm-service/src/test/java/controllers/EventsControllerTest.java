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
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.Location;
import ru.practicum.dtos.events.State;
import ru.practicum.private_api.events.EventsController;
import ru.practicum.private_api.events.EventsService;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.NewEventDto;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventsControllerTest {
    @Mock
    private EventsService service;

    @InjectMocks
    private EventsController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    Event eventDto;
    Event mockedEvent;
    NewEventDto newEventDto;
    Category category;
    User initiator;
    Location location;

    @BeforeEach
    void setUp() {

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

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
    }

    @Test
    void createEventTest() throws Exception {
        newEventDto.setEventDate("2026-12-31T15:10:05");

        when(service.addEvent(Mockito.anyLong(), any(NewEventDto.class)))
                .thenReturn(eventDto);

        mvc.perform(post("/users/" + 1L + "/events")
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(eventDto.getTitle())));
    }

    @Test
    void updateEventTest() throws Exception {

        eventDto.setTitle("Updated Event Title");

        when(service.updateEvent(Mockito.anyLong(), Mockito.anyLong(), any(UpdateEventUserRequest.class)))
                .thenReturn(eventDto);

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

        mvc.perform(patch("/users/" + 1L + "/events/" + 1L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(request.getTitle())));
    }

    @Test
    void getEvents() throws Exception {
        List<Event> events = List.of(eventDto);

        Mockito.when(service.getEvents(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(events);

        mvc.perform(get("/users/" + 1L + "/events")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title", is(eventDto.getTitle())));
    }

    @Test
    void getEventByIdTest() throws Exception {
        Mockito.when(service.getEventById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(eventDto);

        mvc.perform(get("/users/" + 1L + "/events/" + 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(eventDto.getTitle())));
    }
}
