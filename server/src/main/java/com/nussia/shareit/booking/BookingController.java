package com.nussia.shareit.booking;

import com.nussia.shareit.booking.dto.BookingDTO;
import com.nussia.shareit.booking.dto.BookingShort;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDTO> postBooking(@RequestBody BookingShort bookingShort,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.addBooking(bookingShort, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> patchBooking(@PathVariable Long bookingId,
                                                   @RequestParam Boolean approved,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.approveBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> getBookingByID(@PathVariable Long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getBookings(@RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer size,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByState(from, size, userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDTO>> getBookingsFromUser(@RequestParam(required = false) Integer from,
                                                                @RequestParam(required = false) Integer size,
                                                                @RequestParam(defaultValue = "ALL") String state,
                                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByStateFromOwner(from, size, userId, state));
    }

}
