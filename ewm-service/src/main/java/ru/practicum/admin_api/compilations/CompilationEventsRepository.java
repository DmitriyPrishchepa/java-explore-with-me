package ru.practicum.admin_api.compilations;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.admin_api.compilations.entities.CompilationEvents;

public interface CompilationEventsRepository extends JpaRepository<CompilationEvents, Long> {
    void deleteByCompilationId(long id);
}
