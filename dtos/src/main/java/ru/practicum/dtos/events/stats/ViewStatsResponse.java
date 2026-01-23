package ru.practicum.dtos.events.stats;

import lombok.Data;

@Data
public class ViewStatsResponse {
    private String app;
    private String uri;
    private long hits;
}
