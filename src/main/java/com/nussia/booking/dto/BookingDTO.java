package com.nussia.booking.dto;

import com.nussia.booking.model.BookingStatus;
import com.nussia.item.dto.SimpleItemDTO;
import com.nussia.user.dto.UserDTO;
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
