package ru.practicum.dtos.events;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//для агрегации данных и формирования статистики о количестве просмотров для определённых сервисов и URIs
//позволяет получить сводную информацию о том, сколько раз были запрошены определённые ресурсы
@Entity
@Table(name = "view_stats")
@Getter
@Setter
@ToString
public class ViewStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String app; // Название сервиса
    private String uri; // URI сервиса
    private long hits; // Количество просмотров
}
