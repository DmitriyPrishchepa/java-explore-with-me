package ru.practicum.public_api.events;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.EventsService;
import ru.practicum.private_api.events.model.Event;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class EventsControllerPublic {
    private final EventsService service;
//    private final EventsClient eventsClient;

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

        // Получаем IP-адрес и URI из запроса
//        String userIp = request.getRemoteAddr();
//        String requestUri = request.getRequestURI();
//
//        EndpointHit endpointHit = new EndpointHit();
//        endpointHit.setApp("ewm-service");
//        endpointHit.setUri(requestUri);
//        endpointHit.setIp(userIp);
//        endpointHit.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        eventsClient.addHits(endpointHit);

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
                        size)
        );
    }

    @GetMapping("/{id}")
    public Event getEventByIdAndPublished(
            @PathVariable("id") long id
    ) {
        try {
            int idValue = Integer.parseInt(String.valueOf(id));

            return service.getEventByIdAndPublished(id);
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
