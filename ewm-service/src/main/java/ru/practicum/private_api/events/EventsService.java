package ru.practicum.private_api.events;

import ru.practicum.private_api.events.model.Event;
import ru.practicum.private_api.events.model.NewEventDto;
import ru.practicum.private_api.events.model.UpdateEventUserRequest;

import java.util.List;

public interface EventsService {
    Event addEvent(long userId, NewEventDto newEventDto);

    Event updateEvent(long userId, long eventId, UpdateEventUserRequest request);

    List<Event> getEvents(long userId, int from, int size);

    Event getEventById(long userId, long eventId);

}
