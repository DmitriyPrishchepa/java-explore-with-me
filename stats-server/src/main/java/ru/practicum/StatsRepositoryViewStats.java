package ru.practicum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dtos.events.ViewStats;

public interface StatsRepositoryViewStats extends JpaRepository<ViewStats, Long> {
    @Override
    Page<ViewStats> findAll(Pageable pageable);
}
