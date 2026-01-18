package ru.practicum.private_api.events;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.private_api.events.model.Event;

import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Long> {
    Event findByInitiatorIdAndId(long initiatorId, long eventId);

    Event getReferenceById(long eventId);

    //----------------------

    List<Event> findByIdIn(List<Integer> ids);
}
