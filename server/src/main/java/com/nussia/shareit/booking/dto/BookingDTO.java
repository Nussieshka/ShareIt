package com.nussia.shareit.booking.dto;

import com.nussia.shareit.booking.model.BookingStatus;
import com.nussia.shareit.item.dto.SimpleItemDTO;
import com.nussia.shareit.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingDTO {
    private Long id;
    private SimpleItemDTO item;
    private String start;
    private String end;
    private BookingStatus status;
    private UserDTO booker;
}
