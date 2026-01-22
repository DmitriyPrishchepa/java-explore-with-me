package ru.practicum.private_api.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.events.EventRequestStatusUpdateRequest;
import ru.practicum.dtos.events.EventRequestStatusUpdateResult;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.requests.model.Request;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestsController {

    private final RequestsService service;

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<Request> getRequestsByEvent(
            @PathVariable("userId") long userId,
            @PathVariable("eventId") long eventId
    ) {
        try {
            long uId = Long.parseLong(String.valueOf(userId));
            long eId = Long.parseLong(String.valueOf(eventId));

            return service.getRequestsOfEvent(uId, eId);
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable("userId") long userId,
            @PathVariable("eventId") long eventId,
            @RequestBody EventRequestStatusUpdateRequest request
    ) {
        return service.updateRequestStatus(userId, eventId, request);
    }

    @PostMapping("/users/{userId}/requests")
    public Request addRequestToEvent(
            @PathVariable("userId") long userId,
            @RequestParam(value = "eventId") long eventId
    ) {
        try {
            long uId = Long.parseLong(String.valueOf(userId));
            long eId = Long.parseLong(String.valueOf(eventId));

            return service.addRequestToEvent(uId, eId);
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public Request cancelRequest(
            @PathVariable("userId") long userId,
            @PathVariable("requestId") long requestId
    ) {
        return service.cancelRequest(userId, requestId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<Request> getUserRequests(
            @PathVariable("userId") Long userId
    ) {
        try {
            long uId = Long.parseLong(String.valueOf(userId));

            return service.getRequestsByUser(uId);
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }
}
