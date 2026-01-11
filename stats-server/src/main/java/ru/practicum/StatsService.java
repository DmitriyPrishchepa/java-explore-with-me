package ru.practicum;

import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.ViewStatsResponse;

import java.util.List;

public interface StatsService {
    void addEndpointHit(EndpointHit endpointHit);

    List<ViewStatsResponse> getViewStats(String start, String end, List<String> uris, boolean unique);
}
