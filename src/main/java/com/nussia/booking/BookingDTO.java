package com.nussia.booking;

import com.nussia.item.SimpleItemDTO;
import com.nussia.user.UserDTO;
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
