package ru.practicum.private_api.events.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.admin_api.categories.CategoriesRepository;
import ru.practicum.dtos.events.states.State;
import ru.practicum.dtos.events.states.StateAction;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.LocationRepository;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;

@Component
@RequiredArgsConstructor
public class UpdateEventValidator {

    private final LocationRepository locationRepository;
    private final CategoriesRepository categoriesRepository; // Добавляем репозиторий категорий

    public void validate(Event event, UpdateEventUserRequest request) {
        // Проверка состояния события
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Event must not be published");
        }

        // Проверяем условия для публикации события
        if (!event.getState().equals(State.PENDING)) {
            if (request.getStateAction() == StateAction.PUBLISH_EVENT) {
                throw new ApiError(
                        HttpStatus.FORBIDDEN,
                        "For the requested operation the conditions are not met.",
                        "Cannot publish the event because it's not in the right state: PUBLISHED"
                );
            }
        } else if (event.getState().equals(State.PUBLISHED) && request.getStateAction() == StateAction.CANCEL_REVIEW) {
            throw new ApiError(
                    HttpStatus.FORBIDDEN,
                    "For the requested operation the conditions are not met.",
                    "Cannot publish the event because it's not in the right state: PUBLISHED");
        }
    }
}
