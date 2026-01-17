package ru.practicum.private_api.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.private_api.requests.model.Request;

import java.util.List;

public interface RequestsRepository extends JpaRepository<Request, Long> {
    Request findByRequesterAndEvent(long requester, long event);

    List<Request> findAllByEvent(long event);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = :eventId")
    long countByEvent(@Param("eventId") long eventId);

    @Query("SELECT r FROM Request r WHERE r.id = :requestId AND r.requester = :userId")
    Request findByIdAndRequester(@Param("requestId") long requestId, @Param("userId") long userId);

    @Query("SELECT r FROM Request r WHERE r.requester.id = :userId")
    List<Request> findByRequesterId(@Param("userId") Long userId);
}
