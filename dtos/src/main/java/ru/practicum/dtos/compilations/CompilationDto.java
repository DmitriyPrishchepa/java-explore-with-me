package ru.practicum.dtos.compilations;

import lombok.Data;
import ru.practicum.dtos.events_public.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {
    private long id;
    List<EventShortDto> events;
    private boolean pinned;
    private String title;
}
