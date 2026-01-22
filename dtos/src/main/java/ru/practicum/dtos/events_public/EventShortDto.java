package ru.practicum.dtos.events_public;

import lombok.Data;
import ru.practicum.dtos.categories.CategoryDto;
import ru.practicum.dtos.users.UserShortDto;

@Data
public class EventShortDto {
    private long id;
    private String annotation; // краткое описание
    private CategoryDto category; //категория
    private int confirmedRequests; //Количество одобренных заявок на участие в данном событии
    private String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private UserShortDto initiator;
    private boolean paid; //Нужно ли оплачивать участие
    private String title; //заголовок
    private int views;
}
