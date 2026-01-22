package ru.practicum;

import ru.practicum.dtos.events.stats.EndpointHit;
import ru.practicum.dtos.events.stats.ViewStatsResponse;

import java.util.List;

public interface StatsService {
    void addEndpointHit(EndpointHit endpointHit);

    List<ViewStatsResponse> getViewStats(String start, String end, List<String> uris, boolean unique);
}
