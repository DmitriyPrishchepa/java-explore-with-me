package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dtos.events.EndpointHit;

import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Override
    <S extends EndpointHit> S save(S entity);

    @Query("SELECT eph FROM EndpointHit eph WHERE eph.timestamp BETWEEN :start AND :end")
    List<EndpointHit> findWithSort(@Param("start") String start, @Param("end") String end);
}
