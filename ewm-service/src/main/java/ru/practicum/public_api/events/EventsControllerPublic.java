package ru.practicum.public_api.events;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EventsClient;
import ru.practicum.dtos.events.SearchEventsDtoFiltered;
import ru.practicum.dtos.events.ratings.AuthorRatingDto;
import ru.practicum.dtos.events.ratings.EventRatingDto;
import ru.practicum.dtos.events.ratings.Rating;
import ru.practicum.dtos.events.ratings.UpdateRatingDto;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.EventsService;
import ru.practicum.private_api.events.model.Event;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventsControllerPublic {
    private final EventsService service;

    private final EventsClient eventsClient;

    @GetMapping
    public List<Event> searchEventsFiltered(
            @RequestParam("text") String text,
            @RequestParam("categories") List<Integer> categories,
            @RequestParam("paid") boolean paid,
            @RequestParam("rangeStart") String rangeStart,
            @RequestParam("rangeEnd") String rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
            @RequestParam("sort") String sort,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request
    ) {

        return service.searchEventsFiltered(
                SearchEventsDtoFiltered.of(
                        text,
                        categories,
                        paid,
                        rangeStart,
                        rangeEnd,
                        onlyAvailable,
                        sort,
                        from,
                        size,
                        request.getRemoteAddr(),
                        request.getRequestURI())
        );
    }


    @GetMapping("/{id}")
    public Event getEventByIdAndPublished(
            @PathVariable("id") long id,
            HttpServletRequest request
    ) {
        try {
            int idValue = Integer.parseInt(String.valueOf(id));

            return service.getEventByIdAndPublished(idValue, request.getRequestURI(), request.getRemoteAddr());
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }

    @PostMapping("/update_rating")
    public Rating updateRating(
            @RequestParam("eventId") long eventId,
            @RequestParam("userId") long userId,
            @RequestParam("rating") String rating
    ) {
        try {
            int eventIdValue = Integer.parseInt(String.valueOf(eventId));
            int userIdValue = Integer.parseInt(String.valueOf(userId));

            return service.updateRating(UpdateRatingDto.of(eventIdValue, userIdValue, rating));
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }

    @GetMapping("/{userId}/events/{eventId}/rating")
    public EventRatingDto getEventRating(
            @PathVariable("userId") long userId,
            @PathVariable("eventId") long eventId
    ) {
        try {
            long uId = Long.parseLong(String.valueOf(userId));
            long eId = Long.parseLong(String.valueOf(eventId));

            return service.getEventRating(uId, eId);
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }

    @GetMapping("/{userId}/rating")
    public AuthorRatingDto getEventRating(
            @PathVariable("userId") long userId
    ) {
        try {
            long uId = Long.parseLong(String.valueOf(userId));

            return service.getAuthorRating(uId);
        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }

    @GetMapping("/sorted/rating")
    public List<EventRatingDto> getSortedEventsRating() {
        return service.getSortedEventsRating();
    }
}
