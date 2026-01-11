package ru.practicum.dtos.events;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//отвечает за хранение информации о каждом запросе к сервису
@Entity
@Table(name = "endpoint_hits")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // Идентификатор записи

    private String app; // Идентификатор сервиса для которого записывается информация
    private String uri; // URI для которого был осуществлен запрос
    private String ip; // IP-адрес пользователя, осуществившего запрос
    private String timestamp; // Дата и время, когда был совершен запрос к эндпоинту (в формате \"yyyy-MM-dd HH:mm:ss\")
}
