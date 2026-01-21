package ru.practicum.private_api.events;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EventsClient;
import ru.practicum.admin_api.categories.CategoriesRepository;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.admin_api.users.UserRepository;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.events.EndpointHit;
import ru.practicum.dtos.events.SearchEventsDto;
import ru.practicum.dtos.events.State;
import ru.practicum.dtos.events.ViewStatsResponse;
import ru.practicum.exception.exceptions.ApiError;
import ru.practicum.private_api.events.location.Location;
import ru.practicum.private_api.events.mapper.EventMapper;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.NewEventDto;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;
import ru.practicum.private_api.events.validation.EventUpdater;
import ru.practicum.private_api.events.validation.UpdateEventValidator;
import ru.practicum.public_api.events.AvailableValues;
import ru.practicum.public_api.events.SearchEventsDtoFiltered;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;
    private final UpdateEventValidator updateEventValidator;
    private final EventUpdater eventUpdater;

    private final EventsClient statsClient;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Transactional
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

        // Связываем событие с местоположением

        event.setLocation(location);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        event.setCreatedOn(formattedDate);

        if (event.isRequestModeration()) {
            event.setState(State.PENDING);
        } else {
            event.setState(State.PUBLISHED);
            event.setCreatedOn(formattedDate);
        }

        return eventsRepository.save(event);
    }

    @Transactional
    @Override
    public Event updateEvent(long userId, long eventId, UpdateEventUserRequest request) {
        isUserExists(userId);
        isEventExists(eventId);
        isCategoryExists(request.getCategory());

        Category category = categoriesRepository.getReferenceById(request.getCategory().longValue());
        Event existingEvent = eventsRepository.findByInitiatorIdAndId(userId, eventId);

        updateEventValidator.validate(existingEvent, request);
        System.out.println(existingEvent);
        System.out.println(request);
        Event updatedEvent = eventUpdater.updateEvent(existingEvent, category, request);

        System.out.println(updatedEvent);
        return eventsRepository.save(updatedEvent);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Override
    public Event getEventById(long userId, long eventId) {
        isUserExists(userId);
        isEventExists(eventId);
        return eventsRepository.findByInitiatorIdAndId(userId, eventId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> searchEvents(SearchEventsDto dto) {
        PageRequest pageRequest = PageRequest.of(dto.getFrom(), dto.getSize());

        List<Event> events = new ArrayList<>();
        List<State> states;

        if (!dto.getUsers().isEmpty()) {
            events = eventsRepository.findByInitiatorIdIn(dto.getUsers(), pageRequest).getContent();
        }

        if (!events.isEmpty() && !dto.getStates().isEmpty()) {
            states = dto.getStates().stream()
                    .map(State::valueOf)
                    .toList();
            events = events.stream()
                    .filter(event -> states.contains(event.getState()))
                    .toList();
        } else if (!dto.getStates().isEmpty()) {
            states = dto.getStates().stream()
                    .map(State::valueOf)
                    .collect(Collectors.toList());
            events = eventsRepository.findAllByStateInStates(states, pageRequest).getContent();
        }

        List<Long> categoryIds = dto.getCategories().stream()
                .map(Integer::longValue)
                .toList();

        if (!events.isEmpty() && !categoryIds.isEmpty()) {
            events = events.stream()
                    .filter(event -> categoryIds.contains(event.getCategory().getId()))
                    .toList();
        } else if (!categoryIds.isEmpty()) {
            events = eventsRepository.findAllByCategoryIdIn(categoryIds, pageRequest).getContent();
        }

        // Поиск по диапазону дат (rangeStart и rangeEnd)
        if (dto.getRangeStart() != null && dto.getRangeEnd() != null) {
            LocalDateTime startDate = LocalDateTime.parse(
                    dto.getRangeStart(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endDate = LocalDateTime.parse(
                    dto.getRangeEnd(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String startDateString = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String endDateString = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            events = eventsRepository.findByEventDateBetween(startDateString, endDateString);
        }

        return events;
    }

    @Transactional
    @Override
    public Event updateEventAndStatus(long eventId, UpdateEventUserRequest request) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new ApiError(
                        HttpStatus.NOT_FOUND,
                        "The required object was not found.",
                        "Event with id=" + eventId + " was not found"
                ));

        updateEventValidator.validate(event, request);

        System.out.println("Дата публикации:" + event.getPublishedOn());
        System.out.println("Дата начала события" + request.getEventDate());

        LocalDateTime publicationDate = LocalDateTime.parse(request.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime eventDate = LocalDateTime.parse(event.getPublishedOn(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        if (eventDate.isBefore(publicationDate.plusHours(1))) {
            throw new ApiError(
                    HttpStatus.FORBIDDEN,
                    "Incorrectly made request.",
                    "Дата начала события должна быть не ранее чем через час после даты публикации");
        }

        Category category = categoriesRepository.getReferenceById(request.getCategory().longValue());

        Event updatedEvent = eventUpdater.updateEvent(event, category, request);

        return eventsRepository.save(updatedEvent);
    }

    @Transactional
    @Override
    public List<Event> searchEventsFiltered(SearchEventsDtoFiltered dto) {
        PageRequest pageRequest = PageRequest.of(dto.getFrom(), dto.getSize());

        List<Event> events;
        if (dto.getRangeStart() == null || dto.getRangeEnd() == null) {
            events = eventsRepository.findPublishedEventsWithTextSearch(State.PUBLISHED, dto.getText(), pageRequest);
        } else {
            events = eventsRepository.findPublishedEventsWithinDateRange(State.PUBLISHED,
                    dto.getRangeStart(),
                    dto.getRangeEnd(),
                    pageRequest);
        }

//        Map<Long, Long> views = getEventsView(
//                events.stream().map(Event::getId).toList(),
//                dto.getRangeStart(),
//                dto.getRangeEnd()
//        );

        if (dto.getSort().equals(AvailableValues.EVENT_DATE.name())) {
            events = eventsRepository.findPublishedEventsWithTextSearchByDate(State.PUBLISHED, dto.getText(), pageRequest);
        } else if (dto.getSort().equals(AvailableValues.VIEWS.name())) {
            events = eventsRepository.findPublishedEventsWithTextSearchByViews(State.PUBLISHED, dto.getText(), pageRequest);
        }

//        for (Event event : events) {
//            Long eventId = event.getId();
//            if (views.containsKey(eventId)) {
//                int viewCount = views.get(eventId).intValue();
//                event.setViews(viewCount);
//            }
//        }

        return events;
    }

    @Transactional(readOnly = true)
    @Override
    public Event getEventByIdAndPublished(long id, String uri, String addr) {
        Event event = eventsRepository.findById(id)
                .orElseThrow(() -> new ApiError(
                        HttpStatus.NOT_FOUND,
                        "The required object was not found.",
                        "Event with id=" + id + " was not found"
                ));

        if (!event.getState().name().equals("PUBLISHED")) {
            throw new ApiError(
                    HttpStatus.FORBIDDEN,
                    "Event must be PUBLISHED",
                    "Event must be PUBLISHED");
        }

        addHit(uri, addr);

        return event;
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

    private void addHit(String uri, String ip) {
        EndpointHit hit = new EndpointHit();
        hit.setUri(uri);
        hit.setApp("ewm-service");
        hit.setIp(ip);

        String addHitTime = LocalDateTime.now().format(formatter);
        hit.setTimestamp(addHitTime);

        statsClient.addHits(hit);
    }

    private Map<Long, Long> getEventsView(List<Long> ids, String start, String end) {
        List<String> uris = ids.stream()
                .map(id -> "/events/" + id)
                .toList();

        List<ViewStatsResponse> stats = statsClient.getStats(
                start,
                end,
                uris,
                false
        );

        if (stats.isEmpty()) {
            return new HashMap<>();
        }

        return stats.stream()
                .map(viewStatsDto -> {
                    String eventIdStr = viewStatsDto.getUri().substring("/events/".length());
                    Long eventId = Long.parseLong(eventIdStr);
                    return Map.entry(eventId, viewStatsDto.getHits());
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }
}
