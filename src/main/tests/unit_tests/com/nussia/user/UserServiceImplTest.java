package unit_tests.com.nussia.user;

import util.TestUtil;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ConflictException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.user.User;
import com.nussia.user.UserRepository;
import com.nussia.user.UserServiceImpl;
import com.nussia.user.dto.UserDTO;
import com.nussia.user.dto.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void shouldNotAddUserWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.createUser(null));
    }

    @Test
    void shouldNotAddUserWithUserDTOId() {
        UserDTO userDTO = TestUtil.getTestUserDTO(0L);

        assertThrows(BadRequestException.class, () -> service.createUser(userDTO));
    }

    @Test
    void shouldNotAddUserWithUserWithTheSameEmail() {
        UserDTO userDTO = TestUtil.getTestUserDTO();

        Mockito.when(repository.save(ArgumentMatchers.any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(ConflictException.class, () -> service.createUser(userDTO));
    }

    @Test
    void shouldAddUsers() {
        UserDTO userDTO = TestUtil.getTestUserDTO();

        Mockito.when(repository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(UserMapper.INSTANCE.toUserEntity(userDTO));

        assertThat(service.createUser(userDTO), equalTo(userDTO));
    }

    @Test
    void shouldNotEditUserWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.editUser(null, null));
    }

    @Test
    void shouldNotEditUserWithIncorrectId() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        Long userId = 0L;

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.editUser(userDTO, userId));
    }

    @Test
    void shouldEditUsers() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        User user = UserMapper.INSTANCE.toUserEntity(userDTO);
        Long userId = 0L;

        userDTO.setEmail("new.email@moore.com");

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(repository.save(ArgumentMatchers.any(User.class))).thenAnswer(x -> x.getArgument(0, User.class));

        assertThat(service.editUser(userDTO, userId), equalTo(userDTO));
    }

    @Test
    void shouldNotGetUserWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.getUser(null));
    }

    @Test
    void shouldNotGetUserWithIncorrectId() {
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getUser(0L));
    }

    @Test
    void shouldGetUsers() {
        Long userId = 0L;
        UserDTO userDTO = TestUtil.getTestUserDTO(userId);

        Mockito.when(repository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(UserMapper.INSTANCE.toUserEntity(userDTO)));

        assertThat(service.getUser(userId), equalTo(userDTO));
    }

    @Test
    void shouldNotDeleteUserWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.deleteUser(null));
    }

    @Test
    void shouldNotDeleteUserWithIncorrectId() {
        Long userId = 0L;

        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.deleteUser(userId));
    }

    @Test
    void shouldDeleteUsers() {
        Long userId = 0L;
        User user = TestUtil.getTestUser(userId);

        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        assertThat(service.deleteUser(userId), equalTo(UserMapper.INSTANCE.toUserDTO(user)));

        Mockito.verify(repository, Mockito.times(1)).deleteById(ArgumentMatchers.anyLong());
    }

    @Test
    void shouldGetUserList() {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(TestUtil.getTestUser((long) i));
        }

        Mockito.when(repository.findAll()).thenReturn(list);

        assertThat(service.getUsers(), equalTo(UserMapper.INSTANCE.toUserDTO(list)));
    }

    @Test
    void shouldThrowWhenDoesUserExistMethodCalledWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.doesUserExist(null));
    }

    @Test
    void shouldReturnTrueIfUserExist() {
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);
        assertThat(service.doesUserExist(0L), equalTo(true));
    }

    @Test
    void shouldReturnTrueIfUserDoesNotExist() {
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(false);
        assertThat(service.doesUserExist(0L), equalTo(false));
    }
}