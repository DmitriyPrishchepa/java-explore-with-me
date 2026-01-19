package ru.practicum.private_api.events.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.dtos.events.State;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.LocationRepository;
import ru.practicum.private_api.events.location.Location;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class EventUpdater {

    private final LocationRepository locationRepository;

    public Event updateEvent(
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
                LocalDateTime newDate = LocalDateTime.parse(
                        request.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedDate = newDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                event.setEventDate(formattedDate);
            }

            if (request.getLocation() != null &&
                    request.getLocation().getLat() != 0f
                    && request.getLocation().getLon() != 0f) {
                // Проверяем, изменилось ли местоположение

                Location location = new Location();
                location.setLat(request.getLocation().getLat());
                location.setLon(request.getLocation().getLon());

                // Сохраняем новое местоположение
                locationRepository.save(location);

                // Связываем событие с новым местоположением
                event.setLocation(location);
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
                        event.setState(State.CANCELED);
                        break;
                    case PUBLISH_EVENT:
                        event.setState(State.PUBLISHED);
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = now.format(formatter);
                        event.setPublishedOn(formattedDate);
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
