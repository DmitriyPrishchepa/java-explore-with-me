package ru.practicum.private_api.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dtos.events.State;
import ru.practicum.private_api.events.model.Event;

import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Long> {
    Event findByInitiatorIdAndId(long initiatorId, long eventId);

    Event getReferenceById(long eventId);

    //----------------------

    List<Event> findByIdIn(List<Integer> ids);

    Page<Event> findByInitiatorIdIn(List<Integer> users, PageRequest request);

    Page<Event> findAllByStateIn(List<State> states, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.id IN :eventIds")
    Page<Event> findAllById(@Param("eventIds") List<Long> eventIds, Pageable pageable);

    //-----------------------------------------------------

    //Методы для получения событий с фильтрами (public api)

    @Query("SELECT e FROM Event e WHERE e.state = :state " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "ORDER BY e.eventDate DESC")
    List<Event> findPublishedEventsWithTextSearch(@Param("state") State state, @Param("text") String text, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.state = :state " +
            "AND e.eventDate >= :rangeStart " +
            "AND e.eventDate <= :rangeEnd " +
            "ORDER BY e.eventDate DESC")
    List<Event> findPublishedEventsWithinDateRange(@Param("state") State state,
                                                   @Param("rangeStart") String rangeStart,
                                                   @Param("rangeEnd") String rangeEnd,
                                                   Pageable pageable);

    //--------------------------------------------

    //Методы для получения событий  по критериям (admin api)

    @Query("SELECT e FROM Event e WHERE e.initiator.id IN :users")
    Page<Event> findByInitiatorIdIn(@Param("users") List<Integer> users, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.state IN :states")
    Page<Event> findAllByStateInStates(@Param("states") List<State> states, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.category.id IN :categories")
    Page<Event> findAllByCategoryIdIn(@Param("categories") List<Long> categories, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    List<Event> findByEventDateBetween(
            @Param("rangeStart") String rangeStart,
            @Param("rangeEnd") String rangeEnd);
}