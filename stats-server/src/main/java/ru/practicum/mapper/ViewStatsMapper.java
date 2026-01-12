package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dtos.events.ViewStats;
import ru.practicum.dtos.events.ViewStatsResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewStatsMapper {
    public static ViewStatsResponse map(ViewStats viewStats) {
        ViewStatsResponse viewStatsResponse = new ViewStatsResponse();
        viewStatsResponse.setApp(viewStats.getApp());
        viewStatsResponse.setUri(viewStats.getUri());
        viewStatsResponse.setHits(viewStats.getHits());
        return viewStatsResponse;
    }
}
