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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.events.EventRequestStatusUpdateRequest;
import ru.practicum.dtos.events.EventRequestStatusUpdateResult;
import ru.practicum.dtos.events.states.State;
import ru.practicum.dtos.requests.ParticipationRequestDto;
import ru.practicum.dtos.requests.RequestStatus;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.location.Location;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.requests.RequestsController;
import ru.practicum.private_api.requests.RequestsRepository;
import ru.practicum.private_api.requests.RequestsService;
import ru.practicum.private_api.requests.model.Request;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RequestsControllerTest {

    @Mock
    private RequestsRepository requestsRepository;

    @Mock
    private RequestsService service;

    @InjectMocks
    private RequestsController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    Request request;
    Event event;
    Category category;
    User initiator;
    Location location;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        request = new Request();
        request.setId(1L);
        request.setRequester(1L);
        request.setStatus(RequestStatus.PENDING);
        request.setEvent(1L);
        request.setCreated("2022-09-06T21:10:05.432");

        category = new Category();
        category.setId(1L);
        category.setName("Category");

        initiator = new User();
        initiator.setId(2L);
        initiator.setName("User");
        initiator.setEmail("Email");

        location = new Location();
        location.setLat(55.754167F);
        location.setLon(37.62F);

        event = new Event();
        event.setAnnotation("Сплав на байдарках похож на полет.");
        event.setCategory(category);
        event.setConfirmedRequests(5);
        event.setCreatedOn("2022-09-06 11:00:23");
        event.setDescription("Сплав на байдарках похож на полет. На спокойной воде — это парение. " +
                "На бурной, порожистой — выполнение фигур высшего пилотажа. И то, и другое дарят чувство обновления, " +
                "феерические эмоции, яркие впечатления.");
        event.setId(1L);
        event.setInitiator(initiator);
        event.setLocation(location);
        event.setPaid(true);
        event.setParticipantLimit(10);
        event.setPublishedOn("2022-09-06 15:10:05");
        event.setState(State.PUBLISHED);
        event.setTitle("Сплав на байдарках");
        event.setViews(999);
        event.setRequestModeration(true);

        requestsRepository.save(request);
    }

    @Test
    void createRequestToEvent() throws Exception {
        Mockito.when(service.addRequestToEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(request);

        mvc.perform(post("/users/" + 1L + "/requests")
                        .param("eventId", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(request.getStatus().name())));
    }

    @Test
    void createRequestToEvent_Error() throws Exception {
        Mockito.when(service.addRequestToEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new ApiError(
                        HttpStatus.BAD_REQUEST,
                        "Incorrectly made request.",
                        "Failed to convert value of type java.lang.String to required type long; nested" +
                                " exception is java.lang.NumberFormatException: For input string: ad"
                ));

        mvc.perform(post("/users/" + 1L + "/requests")
                        .param("eventId", "1Lad")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRequestTest_Success() throws Exception {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setStatus("CONFIRMED");
        updateRequest.setRequestIds(List.of(1, 2, 3));

        ParticipationRequestDto resultDto = new ParticipationRequestDto();
        resultDto.setId(request.getId());
        resultDto.setRequester(request.getRequester());
        resultDto.setStatus(request.getStatus().name());
        resultDto.setEvent(request.getEvent());
        resultDto.setCreated(request.getCreated());

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(List.of(resultDto));
        result.setRejectedRequests(new ArrayList<>());

        Mockito.when(service.updateRequestStatus(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.any(EventRequestStatusUpdateRequest.class)))
                .thenReturn(result);

        mvc.perform(patch("/users/" + 1L + "/events/" + 1L + "/requests")
                        .content(mapper.writeValueAsString(updateRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests", hasSize(1)));

    }

    @Test
    void cancelRequestTest() throws Exception {

        Mockito.when(service.cancelRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(request);

        mvc.perform(patch("/users/" + 1L + "/requests/" + 1L + "/cancel")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(RequestStatus.PENDING.name())));
    }

    @Test
    void getUserRequests() throws Exception {
        Mockito.when(service.getRequestsByUser(Mockito.anyLong()))
                .thenReturn(List.of(request));

        mvc.perform(get("/users/" + 1L + "/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is(RequestStatus.PENDING.name())));
    }

    @Test
    void getUserRequests_Error() throws Exception {
        mvc.perform(get("/users/" + "1adl" + "/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestsByEvent_Success() throws Exception {
        Mockito.when(service.getRequestsOfEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(request));

        mvc.perform(get("/users/" + 1L + "/events/" + 1L + "/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is(RequestStatus.PENDING.name())));
    }

    @Test
    void getRequestsByEvent_Error() throws Exception {
        mvc.perform(get("/users/" + "1fkjkre" + "/events/" + 1L + "/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
