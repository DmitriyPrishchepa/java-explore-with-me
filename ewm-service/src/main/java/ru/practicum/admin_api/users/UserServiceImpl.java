package ru.practicum.admin_api.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin_api.users.mapper.UserMapper;
import ru.practicum.admin_api.users.model.NewUserRequest;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.admin_api.users.validation.UserValidator;
import ru.practicum.dtos.users.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        UserValidator.validateUser(newUserRequest);

        User user = new User();
        user.setEmail(newUserRequest.getEmail());
        user.setName(newUserRequest.getName());

        User saved = repository.save(user);
        return userMapper.mapToDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAllUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<User> users = repository.findAll(pageRequest).getContent();

        return users.stream()
                .filter(user -> ids.contains(user.getId()))
                .map(userMapper::mapToDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        repository.deleteById(id);
    }
}
