package com.nussia;

import com.nussia.booking.dto.BookingDTO;
import com.nussia.exception.BadRequestException;
import com.nussia.item.*;
import com.nussia.item.comment.CommentDTO;
import com.nussia.item.dto.ItemDTO;
import com.nussia.user.dto.UserDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Util {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static void editItemUsingDTO(Item item, ItemDTO itemDTO) {
        String name = itemDTO.getName();
        if (name != null) {
            item.setName(name);
        }

        String description = itemDTO.getDescription();
        if (description != null) {
            item.setDescription(description);
        }

        Boolean available = itemDTO.getAvailable();
        if (available != null) {
            item.setAvailable(available);
        }
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

        LocalDateTime startDate = stringToLocalDataTime(bookingDTO.getStart());
        LocalDateTime endDate = stringToLocalDataTime(bookingDTO.getEnd());

        LocalDateTime now = LocalDateTime.now();
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        } else if (endDate.isBefore(now)) {
            throw new BadRequestException("End date cannot be before current date");
        } else if (startDate.isBefore(now)) {
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

    public static String localDataTimeToString(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMAT);
    }

    public static LocalDateTime stringToLocalDataTime(String date) {
        return LocalDateTime.parse(date, DATE_FORMAT);
    }
}
