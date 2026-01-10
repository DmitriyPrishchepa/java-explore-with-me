package ru.practicum;

import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.ViewStats;

import java.util.List;

public interface StatsService {
    void addEndpointHit(EndpointHit endpointHit);

    List<ViewStats> getViewStats(String start, String end, List<String> uris, boolean unique);
}
