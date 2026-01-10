package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dtos.events.EndpointHit;

public interface StatsRepositoryEndpointHit extends JpaRepository<EndpointHit, Long> {
    @Override
    <S extends EndpointHit> S save(S entity);
}
