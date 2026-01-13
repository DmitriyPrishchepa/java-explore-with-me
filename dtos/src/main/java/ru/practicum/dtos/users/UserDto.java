package ru.practicum.dtos.users;

import lombok.Data;

@Data
public class UserDto {
    private String email;
    private long id;
    private String name;
}
