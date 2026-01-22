package ru.practicum.dtos.events.ratings;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "event_id")
    private long eventId;

    @Column(name = "user_id")
    private long userId;

    private int rating;
}
