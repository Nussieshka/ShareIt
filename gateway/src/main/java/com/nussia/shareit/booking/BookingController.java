package com.nussia.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.nussia.shareit.booking.dto.BookItemRequestDto;
import com.nussia.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
										   @RequestBody @Valid BookItemRequestDto requestDto) {
		return bookingClient.bookItem(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> patchBooking(@PathVariable Long bookingId,
												   @RequestParam Boolean approved,
												   @RequestHeader("X-Sharer-User-Id") long userId) {
		return bookingClient.approveBooking(bookingId, approved, userId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long bookingId) {
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
											  @RequestParam(name = "state", defaultValue = "all") String stateParam,
											  @PositiveOrZero @RequestParam(required = false) Integer from,
											  @Positive @RequestParam(required = false) Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsFromUser(@PositiveOrZero @RequestParam(required = false) Integer from,
													  @Positive @RequestParam(required = false) Integer size,
																@RequestParam(name = "state", defaultValue = "all")
														  			String stateParam,
																@RequestHeader("X-Sharer-User-Id") long userId) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

		return bookingClient.getBookingsFromUser(from, size, state, userId);
	}
}
