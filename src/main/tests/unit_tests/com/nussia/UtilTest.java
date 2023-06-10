package unit_tests.com.nussia;

import com.nussia.Util;
import com.nussia.booking.dto.BookingDTO;
import com.nussia.exception.BadRequestException;
import com.nussia.item.Item;
import com.nussia.item.comment.dto.CommentDTO;
import com.nussia.item.dto.ItemDTO;
import com.nussia.item.dto.ItemMapper;
import com.nussia.request.dto.RequestDTO;
import com.nussia.user.User;
import com.nussia.user.dto.UserDTO;
import com.nussia.user.dto.UserMapper;
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
    void shouldNotValidateUserDTOWithNullData() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        userDTO.setEmail(null);
        userDTO.setName(null);
        assertThrows(BadRequestException.class, () -> Util.validateUserDTO(userDTO));
    }

    @Test
    void shouldNotValidateUserDTOWithBlankName() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        userDTO.setName("");
        assertThrows(BadRequestException.class, () -> Util.validateUserDTO(userDTO));
    }

    @Test
    void shouldNotValidateUserDTOWithBlankEmail() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        userDTO.setEmail("");
        assertThrows(BadRequestException.class, () -> Util.validateUserDTO(userDTO));
    }

    @Test
    void shouldNotValidateUserDTOWithIncorrectEmail() {
        UserDTO userDTO = TestUtil.getTestUserDTO();
        userDTO.setEmail("ThisIsIncorrectEmailAddress");
        assertThrows(BadRequestException.class, () -> Util.validateUserDTO(userDTO));
    }

    @Test
    void shouldValidateValidUserDTO() {
        assertDoesNotThrow(() -> Util.validateUserDTO(TestUtil.getTestUserDTO()));
    }

    @Test
    void shouldNotValidateItemDTOWithNullData() {
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        itemDTO.setAvailable(null);
        itemDTO.setName(null);
        itemDTO.setDescription(null);
        assertThrows(BadRequestException.class, () -> Util.validateItemDTO(itemDTO));
    }

    @Test
    void shouldNotValidateItemDTOWithBlankName() {
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        itemDTO.setName("");
        assertThrows(BadRequestException.class, () -> Util.validateItemDTO(itemDTO));
    }

    @Test
    void shouldNotValidateItemDTOWithBlankDescription() {
        ItemDTO itemDTO = ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO());
        itemDTO.setDescription("");
        assertThrows(BadRequestException.class, () -> Util.validateItemDTO(itemDTO));
    }

    @Test
    void shouldValidateValidItemDTO() {
        assertDoesNotThrow(() -> Util.validateItemDTO(ItemMapper.INSTANCE.toItemDTO(TestUtil.getTestItemDTO())));
    }

    @Test
    void shouldNotValidateBookingDTOWithId() {
        BookingDTO bookingDTO = TestUtil.getTestBookingDTO(1L, 2L, 3L);
        bookingDTO.setId(0L);
        assertThrows(BadRequestException.class, () -> Util.validateBookingDTO(bookingDTO));
    }

    @Test
    void shouldNotValidateBookingDTOWithNullDates() {
        assertThrows(BadRequestException.class, () -> Util.validateBookingDTO(
                TestUtil.getTestBookingDTO(1L, 2L, 3L, null, null)));
    }

    @Test
    void shouldNotValidateBookingDTOIfStartDateIsAfterEndDate() {
        assertThrows(BadRequestException.class, () -> Util.validateBookingDTO(
                TestUtil.getTestBookingDTO(1L, 2L, 3L, 123, 122)));
    }

    @Test
    void shouldNotValidateBookingDTOIfEndDateIsBeforeCurrentDate() {
        assertThrows(BadRequestException.class, () -> Util.validateBookingDTO(
                TestUtil.getTestBookingDTO(1L, 2L, 3L, -10, -5)));
    }

    @Test
    void shouldNotValidateBookingDTOIfStartDateIsBeforeCurrentDate() {
        assertThrows(BadRequestException.class, () -> Util.validateBookingDTO(
                TestUtil.getTestBookingDTO(1L, 2L, 3L, -10, 123)));
    }

    @Test
    void shouldNotValidateBookingDTOIfStartDateIsEqualToEndDate() {
        assertThrows(BadRequestException.class, () -> Util.validateBookingDTO(
                TestUtil.getTestBookingDTO(1L, 2L, 3L, 123, 123)));
    }

    @Test
    void shouldValidateValidBookingDTO() {
        assertDoesNotThrow(() -> Util.validateBookingDTO(TestUtil.getTestBookingDTO(1L, 2L, 3L)));
    }

    @Test
    void shouldNotValidateCommentDTOWithNullText() {
        CommentDTO commentDTO = TestUtil.getTestCommentDTO();
        commentDTO.setText(null);
        assertThrows(BadRequestException.class, () -> Util.validateCommentDTO(commentDTO));
    }

    @Test
    void shouldNotValidateCommentDTOWithDate() {
        CommentDTO commentDTO = TestUtil.getTestCommentDTO();
        commentDTO.setCreated(Util.localDataTimeToString(LocalDateTime.now().minusMinutes(1)));
        assertThrows(BadRequestException.class, () -> Util.validateCommentDTO(commentDTO));
    }

    @Test
    void shouldNotValidateCommentDTOWithAuthorName() {
        CommentDTO commentDTO = TestUtil.getTestCommentDTO();
        commentDTO.setAuthorName("Daniel Rosenfeld");
        assertThrows(BadRequestException.class, () -> Util.validateCommentDTO(commentDTO));
    }

    @Test
    void shouldNotValidateCommentDTOWithBlankText() {
        CommentDTO commentDTO = TestUtil.getTestCommentDTO();
        commentDTO.setText("");
        assertThrows(BadRequestException.class, () -> Util.validateCommentDTO(commentDTO));
    }

    @Test
    void shouldValidateValidCommentDTO() {
        assertDoesNotThrow(() -> Util.validateCommentDTO(TestUtil.getTestCommentDTO()));
    }

    @Test
    void shouldNotValidateRequestDTOWithNullDescription() {
        RequestDTO requestDTO = TestUtil.getTestRequestDTO();
        requestDTO.setDescription(null);
        assertThrows(BadRequestException.class, () -> Util.validateRequestDTO(requestDTO));
    }

    @Test
    void shouldNotValidateRequestDTOWithBlankDescription() {
        RequestDTO requestDTO = TestUtil.getTestRequestDTO();
        requestDTO.setDescription("");
        assertThrows(BadRequestException.class, () -> Util.validateRequestDTO(requestDTO));
    }

    @Test
    void shouldNotValidateRequestDTOWithDate() {
        RequestDTO requestDTO = TestUtil.getTestRequestDTO();
        requestDTO.setCreated(Util.localDataTimeToString(LocalDateTime.now().minusMinutes(1)));
        assertThrows(BadRequestException.class, () -> Util.validateRequestDTO(requestDTO));
    }

    @Test
    void shouldNotValidateRequestDTOWithId() {
        RequestDTO requestDTO = TestUtil.getTestRequestDTO();
        requestDTO.setId(0L);
        assertThrows(BadRequestException.class, () -> Util.validateRequestDTO(requestDTO));
    }

    @Test
    void shouldNotValidateRequestDTOWithItems() {
        RequestDTO requestDTO = TestUtil.getTestRequestDTO();
        requestDTO.setItems(Collections.singletonList(TestUtil.getTestItemDTO()));
        assertThrows(BadRequestException.class, () -> Util.validateRequestDTO(requestDTO));
    }

    @Test
    void shouldValidateValidRequestDTO() {
        assertDoesNotThrow(() -> Util.validateRequestDTO(TestUtil.getTestRequestDTO()));
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