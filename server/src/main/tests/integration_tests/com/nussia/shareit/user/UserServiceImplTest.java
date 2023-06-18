package integration_tests.com.nussia.shareit.user;

import com.nussia.shareit.booking.BookingServiceImpl;
import com.nussia.shareit.exception.ObjectNotFoundException;
import com.nussia.shareit.item.ItemServiceImpl;
import com.nussia.shareit.request.RequestServiceImpl;
import com.nussia.shareit.user.User;
import com.nussia.shareit.user.UserService;
import com.nussia.shareit.user.UserServiceImpl;
import com.nussia.shareit.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import util.IntegrationTestUtil;
import util.TestPersistenceConfig;
import util.TestUtil;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@TestPropertySource(properties = { "db.name = test_share_it" })
@SpringJUnitConfig( { TestPersistenceConfig.class, UserServiceImpl.class, ItemServiceImpl.class,
        BookingServiceImpl.class, RequestServiceImpl.class, IntegrationTestUtil.class })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserService userService;

    private final IntegrationTestUtil util;

    @Test
    void shouldCreateUsers() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        User user = util.createUser(userDTO);

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDTO.getName()));
        assertThat(user.getEmail(), equalTo(userDTO.getEmail()));
    }

    @Test
    void shouldEditUsers() {
        User user = util.createUser();
        UserDTO userDTO = TestUtil.getTestUserDTO();
        userDTO.setName("Walter White");
        userDTO.setEmail("Walter@white.com");

        Long userId = user.getId();
        userService.editUser(userDTO, userId);
        User editedUser = util.selectUserById(userId);

        assertThat(editedUser.getId(), equalTo(userId));
        assertThat(editedUser.getName(), equalTo(userDTO.getName()));
        assertThat(editedUser.getEmail(), equalTo(userDTO.getEmail()));
    }

    @Test
    void shouldGetUsers() {
        User user = util.createUser();
        UserDTO repositoryUser = userService.getUser(user.getId());

        assertThat(user.getName(), equalTo(repositoryUser.getName()));
        assertThat(user.getEmail(), equalTo(repositoryUser.getEmail()));
    }

    @Test
    void shouldDeleteUsers() {
        User user = util.createUser();
        Long userId = user.getId();
        userService.deleteUser(userId);

        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void shouldGetUserList() {
        for (int i = 0; i < 3; i++) {
            util.createUser();
        }

        assertThat(userService.getUsers().size(), equalTo(3));
    }

    @Test
    void shouldReturnTrueIfUserExists() {
        assertThat(userService.doesUserExist(util.createUser().getId()), equalTo(true));
    }

    @Test
    void shouldReturnFalseIfUserDoesNotExist() {
        User user = util.createUser();
        Long id = user.getId();
        userService.deleteUser(id);
        assertThat(userService.doesUserExist(id), equalTo(false));
    }
}
