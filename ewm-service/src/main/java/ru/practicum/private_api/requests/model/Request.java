package ru.practicum.private_api.requests.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.dtos.requests.RequestStatus;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String created;

    @Column(name = "event_id")
    private long event;
    @Column(name = "requester_id")
    private long requester;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
