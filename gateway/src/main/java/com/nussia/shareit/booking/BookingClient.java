package com.nussia.shareit.booking;

import com.nussia.shareit.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import com.nussia.shareit.booking.dto.BookItemRequestDto;
import com.nussia.shareit.booking.dto.BookingState;
import com.nussia.shareit.BaseClient;

import java.time.LocalDateTime;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Parameters parameters = Parameters.getInstance()
                .addParameter("state", state.name())
                .addParameter("from", from)
                .addParameter("size", size);

        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        LocalDateTime startDate = requestDto.getStart();
        LocalDateTime endDate = requestDto.getEnd();
        if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
            throw new IllegalArgumentException("End date cannot be before or equal to start date");
        }
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> approveBooking(Long bookingId, Boolean approved, long userId) {
        Parameters parameters = Parameters.getInstance().addParameter("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> getBookingsFromUser(Integer from, Integer size, BookingState state, long userId) {
        Parameters parameters = Parameters.getInstance()
                .addParameter("state", state.name())
                .addParameter("from", from)
                .addParameter("size", size);

        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}
