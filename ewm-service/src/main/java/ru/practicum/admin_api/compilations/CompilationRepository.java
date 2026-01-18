package ru.practicum.admin_api.compilations;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.admin_api.compilations.entities.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Compilation findByTitle(String title);
}
