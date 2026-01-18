package ru.practicum.admin_api.compilations.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.admin_api.categories.mapper.CategoryMapper;
import ru.practicum.admin_api.users.mapper.UserMapper;
import ru.practicum.dtos.events_public.EventShortDto;
import ru.practicum.private_api.events.model.Event;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationsEventsMapper {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    public EventShortDto toDto(Event event) {
        EventShortDto shortDto = new EventShortDto();
        shortDto.setId(event.getId());
        shortDto.setAnnotation(event.getAnnotation());
        shortDto.setCategory(categoryMapper.mapToDto(event.getCategory())); // устанавливаем категорию
        shortDto.setEventDate(event.getEventDate());
        shortDto.setConfirmedRequests(event.getConfirmedRequests());
        shortDto.setInitiator(userMapper.mapToShortDto(event.getInitiator())); // устанавливаем инициатора
        shortDto.setPaid(event.isPaid());
        shortDto.setTitle(event.getTitle());
        shortDto.setViews(event.getViews());
        return shortDto;
    }
}
