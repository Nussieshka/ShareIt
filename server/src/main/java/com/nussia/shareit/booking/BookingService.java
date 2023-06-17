package com.nussia.shareit.booking;

import com.nussia.shareit.booking.dto.BookingDTO;
import com.nussia.shareit.booking.dto.BookingShort;

import java.util.List;

public interface BookingService {
    BookingDTO addBooking(BookingShort bookingShort, Long userId);
    BookingDTO approveBooking(Long bookingId, Long userId, Boolean isApproved);
    BookingDTO getBooking(Long bookingId, Long userId);
    List<BookingDTO> getBookingsByStateFromOwner(Integer from, Integer size, Long userId, String state);
    List<BookingDTO> getBookingsByState(Integer from, Integer size, Long userId, String state);
}
