package ru.practicum.admin_api.events;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.events.SearchEventsDto;
import ru.practicum.private_api.events.EventsService;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventsControllerAdmin {
    private final EventsService eventsService;

    @GetMapping
    public List<Event> searchEvents(
            @RequestParam("users") List<Integer> users,
            @RequestParam("states") List<String> states,
            @RequestParam("categories") List<Integer> categories,
            @RequestParam("rangeStart") String rangeStart,
            @RequestParam("rangeEnd") String rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return eventsService.searchEvents(SearchEventsDto.of(
                users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    public Event updateEventAndStatus(
            @PathVariable("eventId") long eventId,
            @RequestBody UpdateEventUserRequest request
    ) {
        return eventsService.updateEventAndStatus(eventId, request);
    }
}
