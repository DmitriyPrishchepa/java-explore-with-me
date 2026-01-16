package ru.practicum.private_api.requests;

import ru.practicum.dtos.events.EventRequestStatusUpdateRequest;
import ru.practicum.dtos.events.EventRequestStatusUpdateResult;
import ru.practicum.private_api.requests.model.Request;

import java.util.List;

public interface RequestsService {
    List<Request> getRequestsOfEvent(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request
    );

    Request addRequestToEvent(long userId, long eventId);

    Request cancelRequest(long userId, long requestId);

    List<Request> getRequestsByUser(long userId);
}
