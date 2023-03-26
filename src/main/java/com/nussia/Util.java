package com.nussia;

import com.nussia.booking.Booking;
import com.nussia.booking.BookingDTO;
import com.nussia.booking.UserBooking;
import com.nussia.exception.BadRequestException;
import com.nussia.item.Comment;
import com.nussia.item.Item;
import com.nussia.item.ItemDTO;
import com.nussia.item.SimpleItemDTO;
import com.nussia.item.CommentDTO;
import com.nussia.user.User;
import com.nussia.user.UserDTO;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Util {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static User getUserFromUserDTO(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }

    public static Item getItemFromItemDTO(ItemDTO itemDTO, Long userId) {
        Item item = new Item();
        item.setOwnerId(userId);
        item.setName(itemDTO.getName());
        item.setAvailable(itemDTO.getAvailable());
        item.setDescription(itemDTO.getDescription());
        return item;
    }

    public static Booking getBookingFromBookingDTO(BookingDTO bookingDTO, Long itemId, Long ownerId, Long userId) {
        Booking booking = new Booking();
        booking.setStartDate(stringToTimestamp(bookingDTO.getStart()));
        booking.setEndDate(stringToTimestamp(bookingDTO.getEnd()));
        booking.setItem(getItemFromItemDTO(itemId, bookingDTO.getItem(), ownerId));
        booking.setBorrowingUser(getUserFromUserDTO(userId, bookingDTO.getBooker()));
        booking.setBookingStatus(bookingDTO.getStatus());
        return booking;
    }

    public static Comment getCommentFromCommentDTO(CommentDTO commentDTO, User user, Item item) {
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setUser(user);
        comment.setItem(item);
        String createdAt = commentDTO.getCreated();
        comment.setCreatedAt(createdAt == null ? new Timestamp(System.currentTimeMillis() + 1000)
                : stringToTimestamp(createdAt));
        return comment;
    }

    public static List<UserDTO> getUserDTOFromUser(Iterable<User> users) {
        return StreamSupport.stream(users.spliterator(), false)
                .map(User::toUserDTO).collect(Collectors.toList());
    }

    public static List<BookingDTO> getBookingDTOFromBooking(Iterable<Booking> bookings) {
        return StreamSupport.stream(bookings.spliterator(), false)
                .map(Booking::toBookingDTO).collect(Collectors.toList());
    }

    public static User getUserFromUserDTO(Long id, UserDTO userDTO) {
        return new User(id, userDTO.getName(), userDTO.getEmail());
    }

    public static Item getItemFromItemDTO(Long id, SimpleItemDTO itemDTO, Long userId) {
        return new Item(id, userId, itemDTO.getName(), itemDTO.getDescription(), itemDTO.getAvailable(),
                new ArrayList<>());
    }

    public static ItemDTO toItemDTO(Item item, List<Comment> comments, Map.Entry<UserBooking,
            UserBooking> recentBookings) {
        return new ItemDTO(item.getItemId(), item.getName(), item.getDescription(), item.getAvailable(),
                recentBookings.getKey(), recentBookings.getValue(),
                comments.stream().map(Comment::toCommentDTO).collect(Collectors.toList()));
    }

    public static ItemDTO toItemDTO(Item item, List<Comment> comments) {
        return new ItemDTO(item.getItemId(), item.getName(), item.getDescription(), item.getAvailable(),
                null, null, comments.stream().map(Comment::toCommentDTO).collect(Collectors.toList()));
    }

    public static void validateUserDTO(UserDTO userDTO) {
        String name = userDTO.getName();
        String email = userDTO.getEmail();
        if (name == null || email == null) {
            throw new BadRequestException("Invalid parameters: name or email is null");
        } else if (name.isBlank()) {
            throw new BadRequestException("Cannot add user with blank name");
        } else if (email.isBlank() || !email.matches("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b")) {
            throw new BadRequestException("Cannot add user with invalid email address");
        }
    }

    public static void validateItemDTO(ItemDTO itemDTO) {
        String name = itemDTO.getName();
        String description = itemDTO.getDescription();
        if (name == null || description == null || itemDTO.getAvailable() == null) {
            throw new BadRequestException("Invalid parameters: name, description, or available is null");
        } else if (name.isBlank()) {
            throw new BadRequestException("Cannot add item with blank name");
        } else if (description.isBlank()) {
            throw new BadRequestException("Cannot add item with blank description");
        }
    }

    public static void validateBookingDTO(BookingDTO bookingDTO) {
        if (bookingDTO.getId() != null) {
            throw new BadRequestException("Booking ID must be null");
        }

        String start = bookingDTO.getStart();
        String end = bookingDTO.getEnd();

        if (start == null || end == null) {
            throw new BadRequestException("Start date or end date cannot be equal to null");
        }

        Timestamp startDate = stringToTimestamp(bookingDTO.getStart());
        Timestamp endDate = stringToTimestamp(bookingDTO.getEnd());

        Date now = new Date();
        if (startDate.after(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        } else if (endDate.before(now)) {
            throw new BadRequestException("End date cannot be before current date");
        } else if (startDate.before(now)) {
            throw new BadRequestException("Start date cannot be before current date");
        } else if (startDate.equals(endDate)) {
            throw new BadRequestException("Start date cannot be equal to end date");
        }
    }

    public static void validateCommentDTO(CommentDTO commentDTO) {
        String text = commentDTO.getText();
        if (text == null) {
            throw new BadRequestException("Text cannot be null");
        } else if (commentDTO.getCreated() != null) {
            throw new BadRequestException("Cannot add comment with date");
        } else if (commentDTO.getAuthorName() != null) {
            throw new BadRequestException("Cannot add comment with author name");
        } else if (text.isBlank()) {
            throw new BadRequestException("Cannot add comment with blank text");
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return DATE_FORMAT.format(new Date(timestamp.getTime()));
    }

    public static Timestamp stringToTimestamp(String date) {
        try {
            Date parsedDate = DATE_FORMAT.parse(date);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            throw new BadRequestException("Cannot parse date");
        }
    }
}
