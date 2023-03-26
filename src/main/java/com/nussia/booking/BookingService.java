package com.nussia.booking;

import java.util.List;

public interface BookingService {
    BookingDTO addBooking(BookingShort bookingShort, Long userId);
    BookingDTO approveBooking(Long bookingId, Long userId, Boolean isApproved);
    BookingDTO getBooking(Long bookingId, Long userId);
    List<BookingDTO> getBookingsByStateFromOwner(Long userId, String state);
    List<BookingDTO> getBookingsByState(Long userId, String state);

    UserBooking getLastBooking(Long itemId);

    UserBooking getNextBooking(Long itemId);

    boolean isBorrowedByUser(Long userId, Long itemId);

}
