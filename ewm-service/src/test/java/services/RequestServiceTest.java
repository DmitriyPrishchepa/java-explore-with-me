package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.admin_api.users.UserRepository;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.Location;
import ru.practicum.dtos.events.EventRequestStatusUpdateRequest;
import ru.practicum.dtos.events.EventRequestStatusUpdateResult;
import ru.practicum.dtos.events.State;
import ru.practicum.dtos.requests.ParticipationRequestDto;
import ru.practicum.dtos.requests.RequestStatus;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.EventsRepository;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.requests.RequestsRepository;
import ru.practicum.private_api.requests.RequestsService;
import ru.practicum.private_api.requests.model.Request;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RequestServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    RequestsRepository requestsRepository;

    @Mock
    EventsRepository eventsRepository;

    @Mock
    RequestsService requestsService;

    Request request;
    Event event;
    Category category;
    User initiator;
    Location location;

    @BeforeEach
    void setUp() {
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

        Mockito.when(eventsRepository.getReferenceById(Mockito.anyLong())).thenReturn(event);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(eventsRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(requestsRepository.countByEvent(Mockito.anyLong())).thenReturn(5L);
        Mockito.when(requestsRepository.findByRequesterAndEvent(
                Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        Mockito.when(requestsRepository.save(Mockito.any(Request.class))).thenReturn(request);
    }

    @Test
    void createRequestTest_Success() {
        Mockito.when(requestsService.addRequestToEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(request);

        Request requestSaved = requestsService.addRequestToEvent(1L, 1L);

        assertEquals("2022-09-06T21:10:05.432", requestSaved.getCreated());
    }

    @Test
    void createRequestTest_SameUser_Error() {
        Mockito.when(requestsService.addRequestToEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(ApiError.class);

        assertThrows(ApiError.class, () -> requestsService.addRequestToEvent(2L, 1L),
                "user is the initiator of the event and cannot create a request for participation.");
    }

    @Test
    void createRequestTest_EventStateNotPublished_Error() {
        event.setState(State.PENDING);
        Mockito.when(eventsRepository.getReferenceById(Mockito.anyLong())).thenReturn(event);

        Mockito.when(requestsService.addRequestToEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(ApiError.class);

        assertThrows(ApiError.class, () -> requestsService.addRequestToEvent(1L, 1L),
                "the event has not been published yet.");
    }

    @Test
    void createRequestTest_CountByEvent_Error() {
        Mockito.when(requestsRepository.countByEvent(Mockito.anyLong())).thenReturn(11L);
        Mockito.when(requestsService.addRequestToEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(ApiError.class);
        assertThrows(ApiError.class, () -> requestsService.addRequestToEvent(1L, 1L),
                "participant limit for the event has been reached.");
    }

    @Test
    void createRequestTest_SameUserRequest_Error() {
        Mockito.when(requestsService.addRequestToEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(ApiError.class);
        assertThrows(ApiError.class, () -> requestsService.addRequestToEvent(2L, 1L),
                "could not execute statement; SQL [n/a]; constraint [uq_request]; nested exception is " +
                        "org.hibernate.exception.ConstraintViolationException: could not execute statement");
    }

    @Test
    void updateRequestStatusTest_Success() {

        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setStatus("CONFIRMED");
        updateRequest.setRequestIds(List.of(1, 2, 3));

        request.setStatus(RequestStatus.CONFIRMED);

        Mockito.when(requestsRepository.findAllByEvent(Mockito.anyLong()))
                .thenReturn(List.of(request));

        Mockito.when(requestsRepository.findAllById(Mockito.anyList()))
                .thenReturn(List.of(request));

        ParticipationRequestDto resultDto = new ParticipationRequestDto();
        resultDto.setId(request.getId());
        resultDto.setRequester(request.getRequester());
        resultDto.setStatus(request.getStatus().name());
        resultDto.setEvent(request.getEvent());
        resultDto.setCreated(request.getCreated());

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(List.of(resultDto));
        result.setRejectedRequests(new ArrayList<>());

        Mockito.when(requestsService.updateRequestStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.eq(updateRequest)))
                .thenReturn(result);

        EventRequestStatusUpdateResult res = requestsService.updateRequestStatus(1L, 1L, updateRequest);

        assertEquals(updateRequest.getStatus(), res.getConfirmedRequests().getFirst().getStatus());
        assertEquals(0, res.getRejectedRequests().size());
    }

    @Test
    void updateRequestStatusTest_OverParticipantLimit_And_NoModeration() {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setStatus("CONFIRMED");
        updateRequest.setRequestIds(List.of(1, 2, 3));

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(new ArrayList<>());
        result.setRejectedRequests(new ArrayList<>());

        Mockito.when(requestsService.updateRequestStatus(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.eq(updateRequest)))
                .thenReturn(result);

        event.setParticipantLimit(0);
        event.setRequestModeration(false);
        Mockito.when(eventsRepository.getReferenceById(Mockito.anyLong())).thenReturn(event);

        EventRequestStatusUpdateResult res = requestsService.updateRequestStatus(1L, 1L, updateRequest);
        assertEquals(0, res.getConfirmedRequests().size());
        assertEquals(0, res.getRejectedRequests().size());
    }

    @Test
    void updateRequestStatusTest_ConfirmedMoreThanLimit_Error() {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setStatus("CONFIRMED");
        updateRequest.setRequestIds(List.of(1, 2, 3));

        Mockito.when(requestsService.updateRequestStatus(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.eq(updateRequest)))
                .thenThrow(ApiError.class);

        request.setStatus(RequestStatus.CONFIRMED);

        Mockito.when(requestsRepository.findAllByEvent(Mockito.anyLong()))
                .thenReturn(List.of(request));

        event.setParticipantLimit(1);

        Mockito.when(eventsRepository.getReferenceById(Mockito.anyLong())).thenReturn(event);

        assertThrows(ApiError.class, () -> requestsService.updateRequestStatus(1L, 1L, updateRequest),
                "The participant limit has been reached");
    }

    @Test
    void updateRequestStatusTest_ConfirmedMoreThanLimit() {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setStatus("CONFIRMED");
        updateRequest.setRequestIds(List.of(1, 2, 3));


        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(new ArrayList<>());
        result.setRejectedRequests(new ArrayList<>());

        Mockito.when(requestsService.updateRequestStatus(
                        Mockito.anyLong(),
                        Mockito.anyLong(),
                        Mockito.eq(updateRequest)))
                .thenReturn(result);

        event.setParticipantLimit(4);

        Mockito.when(eventsRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(event);

        EventRequestStatusUpdateResult res = requestsService.updateRequestStatus(1L, 1L, updateRequest);
        assertEquals(0, res.getRejectedRequests().size());
    }

    @Test
    void cancelRequestTest_Success() {
        Mockito.when(requestsRepository.findByIdAndRequester(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(request);

        Mockito.when(requestsService.cancelRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(request);

        request.setStatus(RequestStatus.PENDING);
        Mockito.when(requestsRepository.save(Mockito.any(Request.class))).thenReturn(request);

        Request request1 = requestsService.cancelRequest(1L, 1L);
        assertEquals("PENDING", request1.getStatus().name());
    }

    @Test
    void cancelRequestTest_Error() {
        Mockito.when(requestsRepository.findByIdAndRequester(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(null);

        Mockito.when(requestsService.cancelRequest(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(ApiError.class);

        assertThrows(ApiError.class, () -> requestsService.cancelRequest(1L, 1L),
                "Request with id=" + 1 + " was not found");
    }

    @Test
    void getRequestsOfEventTest() {
        Mockito.when(requestsRepository.findAllByEvent(Mockito.anyLong()))
                .thenReturn(List.of(request));

        Mockito.when(requestsService.getRequestsOfEvent(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(List.of(request));

        List<Request> requests = requestsService.getRequestsOfEvent(1L, 1L);

        assertEquals(1, requests.size());
    }

    @Test
    void getRequestsByUserTest() {
        Mockito.when(requestsRepository.findByRequesterId(Mockito.anyLong())).thenReturn(List.of(request));

        Mockito.when(requestsService.getRequestsByUser(Mockito.anyLong()))
                .thenReturn(List.of(request));

        List<Request> requests = requestsService.getRequestsByUser(1L);

        assertEquals(1, requests.size());
    }
}
