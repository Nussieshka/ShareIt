package com.nussia.shareit;

import com.nussia.shareit.booking.dto.BookingDTO;
import com.nussia.shareit.exception.BadRequestException;
import com.nussia.shareit.item.*;
import com.nussia.shareit.item.comment.dto.CommentDTO;
import com.nussia.shareit.item.dto.ItemDTO;
import com.nussia.shareit.item.dto.SimpleItemDTO;
import com.nussia.shareit.request.dto.RequestDTO;
import com.nussia.shareit.user.User;
import com.nussia.shareit.user.dto.UserDTO;

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
