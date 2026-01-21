package ru.practicum.dtos.compilations;

import lombok.Data;

import java.util.List;

@Data
public class NewCompilationDto {
    private List<Integer> events; //Список идентификаторов событий входящих в подборку
    private boolean pinned; //закреплена ли подборка на главной странице сайта
    private String title;
}
