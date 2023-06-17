package unit_tests.com.nussia.shareit;

import com.nussia.shareit.Util;
import com.nussia.shareit.booking.dto.BookingDTO;
import com.nussia.shareit.exception.BadRequestException;
import com.nussia.shareit.item.Item;
import com.nussia.shareit.item.comment.dto.CommentDTO;
import com.nussia.shareit.item.dto.ItemDTO;
import com.nussia.shareit.item.dto.ItemMapper;
import com.nussia.shareit.request.dto.RequestDTO;
import com.nussia.shareit.user.User;
import com.nussia.shareit.user.dto.UserDTO;
import com.nussia.shareit.user.dto.UserMapper;
import util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UtilTest {

    @Test
    void shouldNotUpdateItemWithBlankName() {
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        itemDTO.setName("");
        Item item = TestUtil.getItemWithId(itemDTO, 1L);
        assertThrows(BadRequestException.class, () -> Util.updateItemEntityFromDTO(item, itemDTO));
    }

    @Test
    void shouldNotUpdateItemWithBlankDescription() {
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        itemDTO.setDescription("");
        Item item = TestUtil.getItemWithId(itemDTO, 1L);
        assertThrows(BadRequestException.class, () -> Util.updateItemEntityFromDTO(item, itemDTO));
    }

    @Test
    void shouldUpdateItemFromItemDTO() {
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        String name = "Cool Headphones";
        String description = "Another description of cool headphones";

        itemDTO.setName(name);
        itemDTO.setDescription(description);
        itemDTO.setAvailable(false);

        Item item = TestUtil.getItemWithId(itemDTO, 1L);

        assertDoesNotThrow(() -> Util.updateItemEntityFromDTO(item, itemDTO));

        assertThat(item.getName(), equalTo(name));
        assertThat(item.getDescription(), equalTo(description));
        assertThat(item.getAvailable(), equalTo(false));
    }
    @Test
    void shouldNotUpdateUserWithBlankName() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        userDTO.setName("");
        User user = UserMapper.INSTANCE.toUserEntity(userDTO);
        assertThrows(BadRequestException.class, () -> Util.updateUserEntityFromDTO(user, userDTO));
    }

    @Test
    void shouldNotUpdateUserWithBlankEmail() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        userDTO.setEmail("");
        User user = UserMapper.INSTANCE.toUserEntity(userDTO);
        assertThrows(BadRequestException.class, () -> Util.updateUserEntityFromDTO(user, userDTO));
    }

    @Test
    void shouldNotUpdateUserWithIncorrectEmail() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        userDTO.setEmail("ThisIsIncorrectEmailAddress");
        User user = UserMapper.INSTANCE.toUserEntity(userDTO);
        assertThrows(BadRequestException.class, () -> Util.updateUserEntityFromDTO(user, userDTO));
    }

    @Test
    void shouldUpdateUserFromUserDTO() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        String name = "Keanu Reeves";
        String email = "keanu@reeves.com";

        userDTO.setName(name);
        userDTO.setEmail(email);

        User user = UserMapper.INSTANCE.toUserEntity(userDTO);

        assertDoesNotThrow(() -> Util.updateUserEntityFromDTO(user, userDTO));

        assertThat(user.getName(), equalTo(name));
        assertThat(user.getEmail(), equalTo(email));
    }

    @Test
    void shouldNotReturnPaginatedResultIfStartIndexIsNull() {
        assertThrows(BadRequestException.class, () -> Util.getPaginatedResult(null, 4, () -> "NotPaginated",
                () -> "Paginated"));
    }

    @Test
    void shouldNotReturnPaginatedResultIfSizeNull() {
        assertThrows(BadRequestException.class, () -> Util.getPaginatedResult(0, null, () -> "NotPaginated",
                () -> "Paginated"));
    }

    @Test
    void shouldNotReturnPaginatedResultIfStartIndexIsLessThanZero() {
        assertThrows(BadRequestException.class, () -> Util.getPaginatedResult(-1, 4, () -> "NotPaginated",
                () -> "Paginated"));
    }

    @Test
    void shouldNotReturnPaginatedResultIfSizeIsLessThanOne() {
        assertThrows(BadRequestException.class, () -> Util.getPaginatedResult(-1, 4, () -> "NotPaginated",
                () -> "Paginated"));
    }

    @Test
    void shouldReturnNotPaginatedResultIfStartIndexAndSizeAreNull() {
        assertThat(Util.getPaginatedResult(null, null, () -> "NotPaginated",
                () -> "Paginated"), equalTo("NotPaginated"));
    }

    @Test
    void shouldReturnPaginatedResult() {
        assertThat(Util.getPaginatedResult(1, 4, () -> "NotPaginated",
                () -> "Paginated"), equalTo("Paginated"));
    }
}