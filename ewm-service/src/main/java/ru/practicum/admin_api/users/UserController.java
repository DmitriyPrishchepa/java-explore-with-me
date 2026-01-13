package ru.practicum.admin_api.users;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin_api.users.model.NewUserRequest;
import ru.practicum.dtos.users.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(
            @RequestBody NewUserRequest request
    ) {
        return userService.addUser(request);
    }

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return userService.findAllUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(
            @PathVariable(value = "userId") long userId
    ) {
        userService.deleteUser(userId);
    }
}
