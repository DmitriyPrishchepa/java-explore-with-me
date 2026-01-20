package ru.practicum.public_api.events;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EventsClient;
import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.ViewStatsResponse;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.EventsService;
import ru.practicum.private_api.events.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class EventsControllerPublic {
    private final EventsService service;
    private final EventsClient eventsClient;

    @GetMapping
    public List<Event> searchEventsFiltered(
            HttpServletRequest request,
            @RequestParam("text") String text,
            @RequestParam("categories") List<Integer> categories,
            @RequestParam("paid") boolean paid,
            @RequestParam("rangeStart") String rangeStart,
            @RequestParam("rangeEnd") String rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
            @RequestParam("sort") String sort,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        addHit(request);

        ResponseEntity<List<ViewStatsResponse>> response = eventsClient.getStats(
                "2021-01-01 00:00:00", "2027-01-01 00:00:00", null, false
        );

        List<Event> events = service.searchEventsFiltered(
                SearchEventsDtoFiltered.of(
                        text,
                        categories,
                        paid,
                        rangeStart,
                        rangeEnd,
                        onlyAvailable,
                        sort,
                        from,
                        size)
        );

        return setReviewsFromStats(events, Objects.requireNonNull(response.getBody()));
    }



    @GetMapping("/{id}")
    public Event getEventByIdAndPublished(
            HttpServletRequest request,
            @PathVariable("id") long id
    ) {
        try {
            int idValue = Integer.parseInt(String.valueOf(id));

            addHit(request);

            Event event = service.getEventByIdAndPublished(idValue);

            ResponseEntity<List<ViewStatsResponse>> response = eventsClient.getStats(
                    "2021-01-01 00:00:00", "2027-01-01 00:00:00", null, false
            );

            List<Event> updated = setReviewsFromStats(List.of(event), Objects.requireNonNull(response.getBody()));

            return updated.getFirst();


        } catch (NumberFormatException e) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Failed to convert value of type java.lang.String to required type long; nested" +
                            " exception is java.lang.NumberFormatException: For input string: ad"
            );
        }
    }

    private void addHit(HttpServletRequest request) {

        String userIp = request.getRemoteAddr();
        String requestUri = request.getRequestURI();

        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("ewm-service");
        endpointHit.setUri(requestUri);
        endpointHit.setIp(userIp);
        endpointHit.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        eventsClient.addHits(endpointHit);
    }

    private List<Event> setReviewsFromStats(
            List<Event> events,
            List<ViewStatsResponse> stats
    ) {
        // мапа для хранения количества просмотров по идентификатору события
        HashMap<String, Long> viewsMap = new HashMap<>();
        for (ViewStatsResponse stat : stats) {
            viewsMap.put(stat.getUri(), stat.getHits());
        }

        //записываем кол-во посмотров из статистики в события
        for (Event event : events) {
            Long views = viewsMap.get(String.valueOf(event.getId()));
            if (views != null) {
                event.setViews(views.intValue());
            }
        }

        return events;
    }

}
