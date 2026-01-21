package ru.practicum.private_api.events;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.NewEventDto;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class EventsController {

    private final EventsService eventsService;

    @PostMapping("/{userId}/events")
    public Event addEvent(
            @PathVariable("userId") long userId,
            @RequestBody NewEventDto dto
    ) {
        if (dto.getCategory() == null) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Field: category. Error: must not be blank. Value: null"
            );
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateFromNewEvent = LocalDateTime.parse(dto.getEventDate(), formatter);

        if (!dateFromNewEvent.isAfter(LocalDateTime.now())) {
            throw new ApiError(
                    HttpStatus.FORBIDDEN,
                    "For the requested operation the conditions are not met.",
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + dateFromNewEvent
            );
        }

        return eventsService.addEvent(userId, dto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public Event updateEvent(
            @PathVariable("userId") long userId,
            @PathVariable("eventId") long eventId,
            @RequestBody UpdateEventUserRequest request
    ) {
        return eventsService.updateEvent(userId, eventId, request);
    }

    @GetMapping("/{userId}/events")
    public List<Event> getEvents(
            @PathVariable("userId") long userId,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return eventsService.getEvents(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public Event getEventById(
            @PathVariable("userId") long userId,
            @PathVariable("eventId") long eventId
    ) {
        try {
            long uId = Long.parseLong(String.valueOf(userId));
            long eId = Long.parseLong(String.valueOf(eventId));

            return eventsService.getEventById(uId, eId);
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
