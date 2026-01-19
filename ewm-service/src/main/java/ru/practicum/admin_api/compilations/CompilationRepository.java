package ru.practicum.admin_api.compilations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.admin_api.compilations.entities.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Compilation findByTitle(String title);

    @Query("select c from Compilation c where c.pinned = :pinned")
    List<Compilation> findByPinned(@Param("pinned") boolean pinned);
}
