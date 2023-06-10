package com.nussia;

import com.nussia.booking.dto.BookingDTO;
import com.nussia.exception.BadRequestException;
import com.nussia.item.*;
import com.nussia.item.comment.dto.CommentDTO;
import com.nussia.item.dto.ItemDTO;
import com.nussia.item.dto.SimpleItemDTO;
import com.nussia.request.dto.RequestDTO;
import com.nussia.user.User;
import com.nussia.user.dto.UserDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;


public class Util {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static void updateItemEntityFromDTO(Item item, ItemDTO itemDTO) {
        String name = itemDTO.getName();
        if (name != null) {
            if (!name.isBlank()) {
                item.setName(name);
            } else {
                throw new BadRequestException("Invalid parameter: name is invalid");
            }
        }

        String description = itemDTO.getDescription();
        if (description != null) {
            if (!description.isBlank()) {
                item.setDescription(description);
            } else {
                throw new BadRequestException("Invalid parameter: description is invalid");
            }
        }

        Boolean available = itemDTO.getAvailable();
        if (available != null) {
            item.setAvailable(available);
        }
    }

    public static void updateUserEntityFromDTO(User user, UserDTO userDTO) {
        String name = userDTO.getName();
        if (name != null) {
            if (!name.isBlank()) {
                user.setName(name);
            } else {
                throw new BadRequestException("Invalid parameter: name is invalid");
            }
        }

        String email = userDTO.getEmail();
        if (email != null) {
            if (!email.isBlank() && email.matches("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b")) {
                user.setEmail(email);
            } else {
                throw new BadRequestException("Invalid parameter: email is invalid");
            }
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

    public static void validateRequestDTO(RequestDTO requestDTO) {
        String description = requestDTO.getDescription();
        if (description == null) {
            throw new BadRequestException("Description cannot be null");
        } else if (description.isBlank()) {
            throw new BadRequestException("Cannot add description with blank description");
        } else if (requestDTO.getCreated() != null) {
            throw new BadRequestException("Cannot add request with date");
        } else if (requestDTO.getId() != null) {
            throw new BadRequestException("Cannot add request with id");
        }

        List<SimpleItemDTO> itemDTOS = requestDTO.getItems();
        if (itemDTOS != null && !itemDTOS.isEmpty()) {
            throw new BadRequestException("Cannot add request with items");
        }
    }

    public static <V> V getPaginatedResult(Integer from, Integer size, Supplier<V> returnValue, Supplier<V> paginatedValue) {
        if (from == null && size == null) {
            return returnValue.get();
        } else if (from == null || size == null) {
            throw new BadRequestException("Invalid parameters: from or size is null");
        } else if (from < 0) {
            throw new BadRequestException("Invalid parameters: from more or equal to 0");
        } else if (size < 1) {
            throw new BadRequestException("Invalid parameters: size should be more than 0");
        }

        return paginatedValue.get();
    }

    public static String localDataTimeToString(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMAT);
    }

    public static LocalDateTime stringToLocalDataTime(String date) {
        return LocalDateTime.parse(date, DATE_FORMAT);
    }
}
