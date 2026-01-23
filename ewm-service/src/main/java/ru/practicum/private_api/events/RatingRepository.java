package ru.practicum.private_api.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dtos.events.ratings.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.eventId = :eventId AND r.rating = 1")
    int getLikesForEvent(Long eventId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.eventId = :eventId AND r.rating = -1")
    int getDislikesForEvent(Long eventId);

    Rating findByEventIdAndUserId(long eventId, long userId);

    boolean existsByEventIdAndUserId(long eventId, long userId);

    //запрос рейтинга мероприятия
    @Query("SELECT SUM(CASE WHEN r.rating = 1 THEN 1 ELSE -1 END) " +
            "FROM Event e " +
            "JOIN Rating r ON e.id = r.eventId " +
            "WHERE e.id = :eventId")
    public Integer getEventRating(@Param("eventId") long eventId);

    //запрос рейтинга автора
    @Query("SELECT SUM(CASE WHEN r.rating = 1 THEN 1 ELSE -1 END) " +
            "FROM User u " +
            "JOIN Event e ON u = e.initiator " +
            "JOIN Rating r ON e.id = r.eventId " +
            "WHERE u.id = :authorId")
    public Integer getAuthorRating(@Param("authorId") long authorId);

    //Возможность сортировки событий в зависимости от рейтингов
    @Query("SELECT e.id, e.annotation, SUM(CASE WHEN r.rating = 1 THEN 1 ELSE -1 END) " +
            "FROM Event e " +
            "JOIN Rating r ON e.id = r.eventId " +
            "GROUP BY e.id " +
            "ORDER BY SUM(CASE WHEN r.rating = 1 THEN 1 ELSE -1 END) DESC")
    public List<Object[]> getSortedEventsRating();
}
