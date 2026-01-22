package ru.practicum.admin_api.compilations.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compilation_events")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class CompilationEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "compilation_id")
    private Long compilationId;

    @Column(name = "event_id")
    private Long eventId;
}
