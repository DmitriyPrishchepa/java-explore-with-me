package ru.practicum.private_api.events.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.admin_api.users.UserRepository;
import ru.practicum.dtos.events.ratings.UpdateRatingDto;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.EventsRepository;
import ru.practicum.private_api.events.RatingRepository;

@Component
@RequiredArgsConstructor
public class RatingUpdateValidator {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    public void validateUpdateRating(UpdateRatingDto dto) {
        //если такая запись уже есть в таблице
        if (ratingRepository.existsByEventIdAndUserId(dto.getEventId(), dto.getUserId())) {
            throw new ApiError(
                    HttpStatus.CONFLICT,
                    "User has already rated this event.",
                    "User with id=" + dto.getUserId() + " has already rated event with id=" + dto.getEventId()
            );
        }

        //проверка существования пользователя
        if (!userRepository.existsById(dto.getUserId())) {
            throw new ApiError(
                    HttpStatus.NOT_FOUND,
                    "User not found.",
                    "User with id=" + dto.getUserId() + " was not found"
            );
        }

        //проверка существования события
        if (!eventsRepository.existsById(dto.getEventId())) {
            throw new ApiError(
                    HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "Event with id=" + dto.getEventId() + " was not found"
            );
        }
    }
}
