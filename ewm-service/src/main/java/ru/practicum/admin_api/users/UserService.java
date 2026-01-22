package ru.practicum.admin_api.users;

import ru.practicum.admin_api.users.model.NewUserRequest;
import ru.practicum.dtos.users.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> findAllUsers(List<Long> ids, int from, int size);

    void deleteUser(long id);
}
