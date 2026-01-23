package ru.practicum.private_api.events.location;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "locations")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float lat;
    private float lon;
}
