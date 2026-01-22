package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dtos.events.stats.EndpointHit;
import ru.practicum.dtos.events.stats.ViewStatsResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public void addHit(@RequestBody EndpointHit endpointHit) {
        log.info("Hit created {}", endpointHit);
        statsService.addEndpointHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStatsResponse> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        return statsService.getViewStats(start, end, uris, unique);
    }
}
