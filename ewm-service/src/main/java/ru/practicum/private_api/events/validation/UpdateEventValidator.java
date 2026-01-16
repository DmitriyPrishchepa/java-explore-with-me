package ru.practicum.private_api.events.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.dtos.events.State;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;

@Component
public class UpdateEventValidator {
    public Event validateEventAndUpdate(
            Event event,
            Category category,
            UpdateEventUserRequest request
    ) {
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ApiError(
                    HttpStatus.BAD_REQUEST,
                    "Incorrectly made request.",
                    "Event must not be published");
        }

        if (event.getState().equals(State.PENDING) || event.getState().equals(State.CANCELED)) {

            if (request.getAnnotation() != null) {
                event.setAnnotation(request.getAnnotation());
            }

            if (request.getCategory() != null) {
                event.setCategory(category);
            }

            if (request.getDescription() != null) {
                event.setDescription(request.getDescription());
            }

            if (request.getEventDate() != null) {
                event.setEventDate(request.getEventDate());
            }

            if (request.getLocation() != null) {
                event.setLocation(request.getLocation());
            }

            if (request.getPaid() != null) {
                event.setPaid(request.getPaid());
            }

            if (request.getParticipantLimit() != null) {
                event.setParticipantLimit(request.getParticipantLimit());
            }

            if (request.getRequestModeration() != null) {
                event.setRequestModeration(request.getRequestModeration());
            }

            if (request.getStateAction() != null) {
                switch (request.getStateAction()) {
                    case SEND_TO_REVIEW:
                        event.setState(State.PENDING);
                        break;
                    case CANCEL_REVIEW:
                        // Здесь можно решить, в какое состояние переводить событие
                        event.setState(State.CANCELED);
                        break;
                }
            }

            if (request.getTitle() != null) {
                event.setTitle(request.getTitle());
            }

        } else {
            throw new ApiError(
                    HttpStatus.FORBIDDEN,
                    "For the requested operation the conditions are not met.",
                    "Only pending or canceled events can be changed"
            );
        }

        return event;
    }
}
