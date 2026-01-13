package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.admin_api.users.UserService;
import ru.practicum.admin_api.users.model.NewUserRequest;
import ru.practicum.admin_api.users.model.User;
import ru.practicum.dtos.users.UserDto;
import ru.practicum.exception.exceptions.ApiError;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @Mock
    UserService service;

    UserDto userDto;
    User mockedUser;
    NewUserRequest request;

    @BeforeEach
    void setUp() {

        Mockito.when(service.addUser(Mockito.any(NewUserRequest.class)))
                .thenReturn(userDto);

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

        Mockito.when(service.addUser(Mockito.any(NewUserRequest.class)))
                .thenReturn(userDto);

        UserDto returnedUser = service.addUser(request);

        assertThat(returnedUser.getName(), is("User"));
        assertThat(returnedUser.getEmail(), is("Email"));
    }

    @Test
    void createUserTest_Error() {
        request = new NewUserRequest();
        request.setName("");
        request.setEmail("Email");

        Mockito.when(service.addUser(Mockito.any(NewUserRequest.class)))
                .thenThrow(new ApiError("Incorrectly made request.", "Field: name. Error: must not be blank. Value: null"));

        try {
            service.addUser(request);
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("Field: name. Error: must not be blank. Value: null"));
        }
    }

    @Test
    void deleteByIdTest() {
        service.deleteUser(1L);

        Mockito.verify(service, Mockito.times(1)).deleteUser(Mockito.anyLong());
    }

    @Test
    void findAllTest() {


        NewUserRequest request2 = new NewUserRequest();
        request2.setName("Name2");
        request2.setEmail("Email2");

        NewUserRequest request3 = new NewUserRequest();
        request3.setName("Name3");
        request3.setEmail("Email3");

        service.addUser(request);
        service.addUser(request2);
        service.addUser(request3);

        UserDto dto2 = new UserDto();
        dto2.setName(request2.getName());
        dto2.setEmail(request2.getEmail());

        UserDto dto3 = new UserDto();
        dto3.setName(request3.getName());
        dto3.setEmail(request3.getEmail());

        List<UserDto> dtos = new ArrayList<>();
        dtos.add(dto2);
        dtos.add(dto3);

        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);

        Mockito.when(service.findAllUsers(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(dtos);

        List<UserDto> users = service.findAllUsers(ids, 0, 10);

        assertThat(users.size(), is(2));
    }
}
