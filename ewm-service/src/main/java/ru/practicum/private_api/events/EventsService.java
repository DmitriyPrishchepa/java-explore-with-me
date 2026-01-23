package ru.practicum.private_api.events;

import ru.practicum.dtos.events.SearchEventsDto;
import ru.practicum.dtos.events.SearchEventsDtoFiltered;
import ru.practicum.dtos.events.ratings.AuthorRatingDto;
import ru.practicum.dtos.events.ratings.EventRatingDto;
import ru.practicum.dtos.events.ratings.Rating;
import ru.practicum.dtos.events.ratings.UpdateRatingDto;
import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.NewEventDto;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;

import java.util.List;

public interface EventsService {
    Event addEvent(long userId, NewEventDto newEventDto);

    Event updateEvent(long userId, long eventId, UpdateEventUserRequest request);

    List<Event> getEvents(long userId, int from, int size);

    Event getEventById(long userId, long eventId);

    List<Event> searchEvents(SearchEventsDto dto);

    Event updateEventAndStatus(long eventId, UpdateEventUserRequest request);

    List<Event> searchEventsFiltered(SearchEventsDtoFiltered dto);

    Event getEventByIdAndPublished(long id, String uri, String addr);

    Rating updateRating(UpdateRatingDto updateRatingDto);

    EventRatingDto getEventRating(long userId, long eventId);

    AuthorRatingDto getAuthorRating(long userId);

    List<EventRatingDto> getSortedEventsRating();
}
