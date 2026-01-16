package ru.practicum.private_api.events;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.private_api.events.model.Event;

public interface EventsRepository extends JpaRepository<Event, Long> {
    Event findByInitiatorIdAndId(long initiatorId, long eventId);
}
