package util;

import com.nussia.Util;
import com.nussia.booking.dto.BookingDTO;
import com.nussia.booking.dto.BookingMapper;
import com.nussia.booking.dto.BookingShort;
import com.nussia.booking.dto.UserBooking;
import com.nussia.booking.model.Booking;
import com.nussia.booking.model.BookingStatus;
import com.nussia.item.Item;
import com.nussia.item.comment.Comment;
import com.nussia.item.comment.dto.CommentDTO;
import com.nussia.item.dto.ItemDTO;
import com.nussia.item.dto.ItemMapper;
import com.nussia.item.dto.SimpleItemDTO;
import com.nussia.request.Request;
import com.nussia.request.dto.RequestDTO;
import com.nussia.user.User;
import com.nussia.user.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static SimpleItemDTO getTestItemDTO(Long id) {
        SimpleItemDTO itemDTO = getTestItemDTO();
        itemDTO.setId(id);
        return itemDTO;
    }

    public static SimpleItemDTO getTestItemDTOWithRequest(Long requestId) {
        SimpleItemDTO itemDTO = getTestItemDTO();
        itemDTO.setRequestId(requestId);
        return itemDTO;
    }

    public static SimpleItemDTO getTestItemDTO() {
        SimpleItemDTO itemDTO = new ItemDTO();
        itemDTO.setName("Headphones");
        itemDTO.setDescription("Best headphones ever");
        itemDTO.setAvailable(true);
        return itemDTO;
    }

    public static Request getTestRequest(Long id) {
        Request request = new Request();
        request.setDescription("I need headphones for work");
        request.setCreatedAt(LocalDateTime.now().minusMinutes(123));
        request.setItems(new ArrayList<>());
        request.setUser(getTestUser(id));
        return request;
    }

    public static Request getTestRequest(Long id, Long userId) {
        Request request = getTestRequest(userId);
        request.setId(id);
        return request;
    }

    public static RequestDTO getTestRequestDTO() {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setDescription("I need headphones for work");
        return requestDTO;
    }

    public static RequestDTO getTestRequestDTO(Long id) {
        RequestDTO requestDTO = getTestRequestDTO();
        requestDTO.setId(id);
        return requestDTO;
    }

    public static User getTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("George Moore");
        user.setEmail("george@moore.com");
        return user;
    }

    public static UserDTO getTestUserDTO(Long id) {
        UserDTO user = getTestUserDTO();
        user.setId(id);
        return user;
    }

    public static UserDTO getTestUserDTO() {
        return getTestUserDTO("george@moore.com");
    }

    public static UserDTO getTestUserDTO(String email) {
        UserDTO user = new UserDTO();
        user.setName("George Moore");
        user.setEmail(email);
        return user;
    }

    public static Item getItemWithId(SimpleItemDTO itemDTO, Long userId) {
        Item item = ItemMapper.INSTANCE.toItemEntity(itemDTO, userId);
        item.setItemId(itemDTO.getId());
        return item;
    }

    public static Item getItemWithId(SimpleItemDTO itemDTO, Request request, Long userId) {
        Item item = getItemWithId(itemDTO, userId);
        item.setRequest(request);
        return item;
    }

    public static Comment getTestComment(Long commentId, Long userId, Item item) {
        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now().minusMinutes(123));
        comment.setUser(getTestUser(userId));
        comment.setItem(item);
        comment.setText("This is the best headphones I've ever used!");
        comment.setId(commentId);
        return comment;
    }

    public static CommentDTO getTestCommentDTO() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText("This is the best headphones I've ever used!");
        return commentDTO;
    }

    public static Booking getTestBooking(Long bookingId, Long borrowingUserId, Item item) {
        return getTestBooking(bookingId, borrowingUserId, item, BookingStatus.APPROVED, 122, 123);
    }

    public static Booking getTestBooking(Long bookingId, Long borrowingUserId, Item item,
                                         BookingStatus status, Integer startMinutes, Integer endMinutes) {
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();
        booking.setBookingId(bookingId);
        booking.setStartDate(now.plusMinutes(startMinutes));
        booking.setEndDate(now.plusMinutes(endMinutes));
        booking.setItem(item);
        booking.setBorrowingUser(getTestUser(borrowingUserId));
        booking.setBookingStatus(status);
        return booking;
    }

    public static List<Booking> getTestBookingsList() {
        List<Booking> bookings = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            bookings.add(getTestBooking((long) i, (long) (Math.random() * 3),
                    getItemWithId(getTestItemDTO((long) i), (long) i)));
        }
        return bookings;
    }

    public static UserBooking getTestUserBooking(Long id, Long bookerId) {
        UserBooking userBooking = new UserBooking();
        userBooking.setBookerId(id);
        userBooking.setBookerId(bookerId);
        return userBooking;
    }

    public static BookingShort getTestBookingShort(Long itemId, Integer startMinutes, Integer endMinutes) {
        BookingShort bookingShort = new BookingShort();
        bookingShort.setItemId(itemId);
        LocalDateTime now = LocalDateTime.now();

        if (startMinutes != null) {
            bookingShort.setStart(Util.localDataTimeToString(now.plusSeconds(startMinutes)));
        }
        if (endMinutes != null) {
            bookingShort.setEnd(Util.localDataTimeToString(now.plusSeconds(endMinutes)));
        }

        return bookingShort;
    }

    public static BookingShort getTestBookingShort(Long itemId) {
        return getTestBookingShort(itemId, 122, 123);
    }
    public static BookingDTO getTestBookingDTO(Long itemId, Long ownerId, Long userId, Integer startMinutes,
                                               Integer endMinutes) {
        BookingDTO bookingDTO =
                BookingMapper.INSTANCE.toBookingDTO(getTestBookingShort(null,
                                startMinutes, endMinutes),
                getItemWithId(getTestItemDTO(itemId), ownerId), getTestUser(userId));

        bookingDTO.setStatus(BookingStatus.WAITING);
        return bookingDTO;
    }

    public static BookingDTO getTestBookingDTO(Long itemId, Long ownerId, Long userId) {
        return getTestBookingDTO(itemId, ownerId, userId, 122, 123);
    }
}
