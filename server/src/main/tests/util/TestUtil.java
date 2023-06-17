package util;

import com.nussia.shareit.Util;
import com.nussia.shareit.booking.dto.BookingDTO;
import com.nussia.shareit.booking.dto.BookingMapper;
import com.nussia.shareit.booking.dto.BookingShort;
import com.nussia.shareit.booking.dto.UserBooking;
import com.nussia.shareit.booking.model.Booking;
import com.nussia.shareit.booking.model.BookingStatus;
import com.nussia.shareit.item.Item;
import com.nussia.shareit.item.comment.Comment;
import com.nussia.shareit.item.comment.dto.CommentDTO;
import com.nussia.shareit.item.dto.ItemDTO;
import com.nussia.shareit.item.dto.ItemMapper;
import com.nussia.shareit.item.dto.SimpleItemDTO;
import com.nussia.shareit.request.Request;
import com.nussia.shareit.request.dto.RequestDTO;
import com.nussia.shareit.user.User;
import com.nussia.shareit.user.dto.UserDTO;

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

    public static Item getItemWithId(SimpleItemDTO itemDTO, User user) {
        Item item = ItemMapper.INSTANCE.toItemEntity(itemDTO, user);
        item.setItemId(itemDTO.getId());
        return item;
    }

    public static Item getItemWithId(SimpleItemDTO itemDTO, Long userId) {
        return getItemWithId(itemDTO, TestUtil.getTestUser(userId));
    }

    public static Item getItemWithId(SimpleItemDTO itemDTO, Request request, User user) {
        Item item = getItemWithId(itemDTO, user);
        item.setRequest(request);
        return item;
    }

    public static Item getItemWithId(SimpleItemDTO itemDTO, Request request, Long userId) {
        return getItemWithId(itemDTO, request, TestUtil.getTestUser(userId));
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
                    getItemWithId(getTestItemDTO((long) i), getTestUser((long) i))));
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

    public static BookingDTO getTestBookingDTO(Long itemId, User owner, Long userId, Integer startMinutes,
                                               Integer endMinutes) {
        BookingDTO bookingDTO =
                BookingMapper.INSTANCE.toBookingDTO(getTestBookingShort(null,
                                startMinutes, endMinutes),
                getItemWithId(getTestItemDTO(itemId), owner), getTestUser(userId));

        bookingDTO.setStatus(BookingStatus.WAITING);
        return bookingDTO;
    }

    public static BookingDTO getTestBookingDTO(Long itemId, Long ownerId, Long userId, Integer startMinutes,
                                               Integer endMinutes) {
        return getTestBookingDTO(itemId, getTestUser(ownerId), userId, startMinutes, endMinutes);
    }

    public static BookingDTO getTestBookingDTO(Long itemId, Long ownerId, Long userId) {
        return getTestBookingDTO(itemId, TestUtil.getTestUser(ownerId), userId, 122, 123);
    }
}
