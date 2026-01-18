package ru.practicum.admin_api.compilations.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "compilations")
@Data
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean pinned;
    private String title;
}
