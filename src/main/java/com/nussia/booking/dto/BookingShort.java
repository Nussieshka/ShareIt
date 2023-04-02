package com.nussia.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingShort {
    private Long itemId;
    private String start;
    private String end;
}
