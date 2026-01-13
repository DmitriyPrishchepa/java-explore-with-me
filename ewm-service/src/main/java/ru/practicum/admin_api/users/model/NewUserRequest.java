package ru.practicum.admin_api.users.model;

import lombok.Data;

@Data
public class NewUserRequest {
    private String email;
    private String name;
}
