package ru.practicum.private_api.events.model;

import lombok.Data;
import ru.practicum.dtos.Location;

@Data
public class NewEventDto {
    private String annotation; //Краткое описание события
    private Integer category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration; //Нужна ли пре-модерация заявок на участие. Если true, то все заявки
    // будут ожидать подтверждения инициатором события. Если false - то будут подтверждаться автоматически.
    private String title;
}
