package ru.practicum.dtos.events.ratings;

import lombok.Data;

@Data
public class AuthorRatingDto {
    private long authorId;
    private String authorName;
    private int rating;

    public AuthorRatingDto(long authorId, String authorName, int rating) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.rating = rating;
    }
}
