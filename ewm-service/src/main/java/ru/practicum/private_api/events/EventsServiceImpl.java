package ru.practicum.private_api.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.admin_api.categories.CategoriesRepository;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.admin_api.users.UserRepository;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.Location;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.mapper.EventMapper;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.NewEventDto;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;
import ru.practicum.private_api.events.validation.UpdateEventValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final EventMapper eventMapper;
    private final UpdateEventValidator validator;
    private final LocationRepository locationRepository;

    @Override
    public Event addEvent(long userId, NewEventDto newEventDto) {

        isUserExists(userId);
        isCategoryExists(newEventDto.getCategory());

        Category category = categoriesRepository.getReferenceById(Long.valueOf(newEventDto.getCategory()));
        Event event = eventMapper.fromDto(newEventDto);
        User initiator = userRepository.getReferenceById(userId);

        event.setCategory(category);
        event.setInitiator(initiator);

        // Получаем данные местоположения
        Location location = new Location();
        location.setLat(newEventDto.getLocation().getLat());
        location.setLon(newEventDto.getLocation().getLon());
        // Сохраняем местоположение
        locationRepository.save(location);
        event.setLocation(location);

        // Связываем событие с местоположением

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        event.setCreatedOn(formattedDate);

        return eventsRepository.save(event);
    }

    @Override
    public Event updateEvent(long userId, long eventId, UpdateEventUserRequest request) {
        isUserExists(userId);
        isEventExists(eventId);
        isCategoryExists(request.getCategory());

        Category category = categoriesRepository.getReferenceById(request.getCategory().longValue());
        Event existingEvent = eventsRepository.findByInitiatorIdAndId(userId, eventId);

        Event updatedEvent = validator.validateEventAndUpdate(existingEvent, category, request);
        return eventsRepository.save(updatedEvent);
    }

    @Override
    public List<Event> getEvents(long userId, int from, int size) {
        isUserExists(userId);

        PageRequest pageRequest = PageRequest.of(from, size);
        List<Event> events = eventsRepository.findAll(pageRequest).getContent();

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        return events;
    }

    @Override
    public Event getEventById(long userId, long eventId) {
        isUserExists(userId);
        isEventExists(eventId);
        return eventsRepository.findByInitiatorIdAndId(userId, eventId);
    }

    public void isUserExists(long id) {
        if (!userRepository.existsById(id)) {
            throw new ApiError(HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "User with id=" + id + " was not found");
        }
    }

    public void isEventExists(long id) {
        if (!eventsRepository.existsById(id)) {
            throw new ApiError(HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "Event with id=" + id + " was not found");
        }
    }

    public void isCategoryExists(long id) {
        if (!categoriesRepository.existsById(id)) {
            throw new ApiError(HttpStatus.NOT_FOUND,
                    "The required object was not found.",
                    "Category with id=" + id + " was not found");
        }
    }
}
