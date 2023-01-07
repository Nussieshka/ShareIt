package com.nussia.booking;

import lombok.Data;

import java.util.Date;

@Data
public class Booking {
    private long bookingId;
    private Date startDate;
    private Date endDate;
    private long itemId;
    private long borrowingUserId;
    private BookingStatus bookingStatus;
}
