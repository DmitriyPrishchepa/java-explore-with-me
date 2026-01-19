package ru.practicum.admin_api.compilations;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.admin_api.compilations.entities.CompilationEvents;

import java.util.List;

public interface CompilationEventsRepository extends JpaRepository<CompilationEvents, Long> {
    void deleteByCompilationId(long id);

    List<CompilationEvents> findByCompilationId(long compilationId);
}
