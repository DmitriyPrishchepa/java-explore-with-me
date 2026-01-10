package ru.practicum.dtos.events;

import lombok.Getter;
import lombok.Setter;

//для агрегации данных и формирования статистики о количестве просмотров для определённых сервисов и URIs
//позволяет получить сводную информацию о том, сколько раз были запрошены определённые ресурсы
@Getter
@Setter
public class ViewStats {
    private String app; // Название сервиса
    private String uri; // URI сервиса
    private long hits; // Количество просмотров
}
