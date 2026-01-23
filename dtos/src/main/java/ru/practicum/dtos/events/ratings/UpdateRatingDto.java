package ru.practicum.dtos.events.ratings;

import lombok.Data;

@Data
public class UpdateRatingDto {
    private long eventId;
    private long userId;
    private String rating;

    public static UpdateRatingDto of(
            long eventId,
            long userId,
            String rating
    ) {
        UpdateRatingDto updateRatingDto = new UpdateRatingDto();
        updateRatingDto.setEventId(eventId);
        updateRatingDto.setUserId(userId);
        updateRatingDto.setRating(rating);
        return updateRatingDto;
    }
}
