package impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.admin_api.users.UserRepository;
import ru.practicum.admin_api.users.UserService;
import ru.practicum.admin_api.users.UserServiceImpl;
import ru.practicum.admin_api.users.mapper.UserMapper;
import ru.practicum.admin_api.users.model.NewUserRequest;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.users.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceImlTest {

    @Mock
    UserRepository repository;

    @Mock
    UserService service;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserServiceImpl impl;

    UserDto userDto;
    User mockedUser;
    NewUserRequest request;

    @BeforeEach
    void setUp() {

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("User");
        userDto.setEmail("Email");

        request = new NewUserRequest();
        request.setName("User");
        request.setEmail("Email");

        mockedUser = Mockito.mock(User.class);
        Mockito.when(mockedUser.getId()).thenReturn(1L);
        Mockito.when(mockedUser.getName()).thenReturn("User");
        Mockito.when(mockedUser.getEmail()).thenReturn("Email");
    }

    @Test
    void createUserTest_Success() {
        Mockito.when(userMapper.mapToDto(Mockito.eq(mockedUser)))
                .thenReturn(userDto);

        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(null);
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(mockedUser);

        UserDto returnedUser = impl.addUser(request);

        assertThat(returnedUser.getName(), is("User"));
        assertThat(returnedUser.getEmail(), is("Email"));
    }

    @Test
    void createUserTest_Error() {
        request = new NewUserRequest();
        request.setName("");
        request.setEmail("Email");

        try {
            impl.addUser(request);
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Field: name. Error: must not be blank. Value: null"));
        }
    }

    @Test
    void deleteByIdTest() {

        impl.addUser(request);

        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);

        impl.deleteUser(1L);

        Mockito.verify(repository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void findAllTest() {

        NewUserRequest request2 = new NewUserRequest();
        request2.setName("Name2");
        request2.setEmail("Email2");

        NewUserRequest request3 = new NewUserRequest();
        request3.setName("Name3");
        request3.setEmail("Email3");

        impl.addUser(request);
        impl.addUser(request2);
        impl.addUser(request3);

        User user2 = new User();
        user2.setId(1L);
        user2.setName(request2.getName());
        user2.setEmail(request2.getEmail());

        User user3 = new User();
        user3.setId(2L);
        user3.setName(request3.getName());
        user3.setEmail(request3.getEmail());

        List<User> uss = new ArrayList<>();
        uss.add(user2);
        uss.add(user3);

        Mockito.when(repository.save(Mockito.eq(mockedUser))).thenReturn(mockedUser);
        Page<User> page = Mockito.mock(Page.class);
        Mockito.when(page.getContent()).thenReturn(uss);
        Mockito.when(repository.findAll(Mockito.any(PageRequest.class))).thenReturn(page);
        Mockito.when(userMapper.mapToDto(Mockito.any(User.class))).thenReturn(userDto);

        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);

        List<UserDto> users2 = impl.findAllUsers(ids, 0, 10);

        assertThat(users2.size(), is(2));
    }
}
