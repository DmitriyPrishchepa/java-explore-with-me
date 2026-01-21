package ru.practicum.admin_api.compilations.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean pinned;
    private String title;
}
