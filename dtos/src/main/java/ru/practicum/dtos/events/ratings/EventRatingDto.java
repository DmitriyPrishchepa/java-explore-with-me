package ru.practicum.dtos.events.ratings;

import lombok.Data;

@Data
public class EventRatingDto {
    private long eventId;
    private String eventAnnotation;
    private int eventRating;

    public EventRatingDto(long eventId, String eventAnnotation, int eventRating) {
        this.eventId = eventId;
        this.eventAnnotation = eventAnnotation;
        this.eventRating = eventRating;
    }
}
