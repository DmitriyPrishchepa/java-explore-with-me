package ru.practicum.private_api.events.model;

import jakarta.persistence.*;
import lombok.Data;
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

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // категория

    @Column(name = "confirmed_requests") //Количество одобренных заявок на участие в данном событии
    private int confirmedRequests;
    @Column(name = "created_on")
    private String createdOn; //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    private String description; //Полное описание события
    @Column(name = "event_date")
    private String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location; //Широта и долгота места проведения события
    private boolean paid; //Нужно ли оплачивать участие

    @Column(name = "published_on")
    private String publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "request_moderation")
    private boolean requestModeration; //Нужна ли пре-модерация заявок на участие

    @Enumerated(EnumType.STRING)
    private State state; //Список состояний жизненного цикла события
}

