package ru.practicum.admin_api.users.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.users.UserDto;

import java.util.List;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public List<UserDto> mapToDto(List<User> users) {
        return users.stream().map(this::mapToDto).toList();
    }
}
