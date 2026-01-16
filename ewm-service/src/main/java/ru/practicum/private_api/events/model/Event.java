package ru.practicum.private_api.events.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import ru.practicum.admin_api.categories.model.Category;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.Location;
import ru.practicum.dtos.events.State;

@Entity
@Table(name = "events")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private int views; //Количество просмотрев события

    private String annotation; // краткое описание

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "category_id")
    private Category category; // категория

    @Column(name = "confirmed_requests") //Количество одобренных заявок на участие в данном событии
    private int confirmedRequests;
    @Column(name = "created_on")
    private String createdOn; //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    private String description; //Полное описание события
    @Column(name = "event_date")
    private String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @OneToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "location_id")
    private Location location; //Широта и долгота места проведения события
    private boolean paid = true; //Нужно ли оплачивать участие

    @Column(name = "participant_limit")
    private int participantLimit = 0;

    @Column(name = "published_on")
    private String publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "request_moderation")
    private boolean requestModeration; //Нужна ли пре-модерация заявок на участие

    @Enumerated(EnumType.STRING)
    private State state; //Список состояний жизненного цикла события
}
