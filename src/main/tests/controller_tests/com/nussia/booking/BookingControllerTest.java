package controller_tests.com.nussia.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nussia.booking.BookingController;
import com.nussia.booking.BookingService;
import com.nussia.booking.dto.BookingDTO;
import com.nussia.booking.dto.BookingShort;
import com.nussia.booking.model.BookingStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService service;

    @InjectMocks
    private BookingController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldPostBooking() throws Exception {
        Long ownerId = 2L;
        Long userId = 1L;
        Long itemId = 0L;
        BookingShort bookingShort = TestUtil.getTestBookingShort(itemId);
        BookingDTO outBookingDTO = TestUtil.getTestBookingDTO(itemId, ownerId, userId);
        outBookingDTO.setId(3L);

        Mockito.when(service.addBooking(bookingShort, userId)).thenReturn(outBookingDTO);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingShort))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue(Long.class)))
                .andExpect(jsonPath("$.item", Matchers.notNullValue()))
                .andExpect(jsonPath("$.item.id", Matchers.is(itemId), Long.class))
                .andExpect(jsonPath("$.item", Matchers.notNullValue()))
                .andExpect(jsonPath("$.start", Matchers.is(outBookingDTO.getStart())))
                .andExpect(jsonPath("$.end", Matchers.is(outBookingDTO.getEnd())))
                .andExpect(jsonPath("$.status", Matchers.is(outBookingDTO.getStatus().toString())))
                .andExpect(jsonPath("$.booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$.booker.id", Matchers.is(userId), Long.class));
    }

    @Test
    void shouldPatchBooking() throws Exception {
        Long bookingId = 0L;
        Long userId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        Boolean approved = true;
        BookingDTO outBookingDTO = TestUtil.getTestBookingDTO(itemId, ownerId, userId);
        outBookingDTO.setId(bookingId);
        outBookingDTO.setStatus(BookingStatus.APPROVED);

        Mockito.when(service.approveBooking(bookingId, ownerId, approved)).thenReturn(outBookingDTO);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", approved.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(bookingId), Long.class))
                .andExpect(jsonPath("$.item", Matchers.notNullValue()))
                .andExpect(jsonPath("$.item.id", Matchers.is(itemId), Long.class))
                .andExpect(jsonPath("$.start", Matchers.is(outBookingDTO.getStart())))
                .andExpect(jsonPath("$.end", Matchers.is(outBookingDTO.getEnd())))
                .andExpect(jsonPath("$.status", Matchers.is(BookingStatus.APPROVED.toString())))
                .andExpect(jsonPath("$.booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$.booker.id", Matchers.is(userId), Long.class));
    }

    @Test
    void shouldGetBookingByID() throws Exception {
        Long bookingId = 0L;
        Long userId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        BookingDTO outBookingDTO = TestUtil.getTestBookingDTO(itemId, ownerId, userId);
        outBookingDTO.setId(bookingId);

        Mockito.when(service.getBooking(bookingId, userId)).thenReturn(outBookingDTO);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(bookingId), Long.class))
                .andExpect(jsonPath("$.item", Matchers.notNullValue()))
                .andExpect(jsonPath("$.item.id", Matchers.is(itemId), Long.class))
                .andExpect(jsonPath("$.item", Matchers.notNullValue()))
                .andExpect(jsonPath("$.start", Matchers.is(outBookingDTO.getStart())))
                .andExpect(jsonPath("$.end", Matchers.is(outBookingDTO.getEnd())))
                .andExpect(jsonPath("$.status", Matchers.is(outBookingDTO.getStatus().toString())))
                .andExpect(jsonPath("$.booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$.booker.id", Matchers.is(userId), Long.class));
    }

    @Test
    void shouldGetBookings() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        List<BookingDTO> outList = new ArrayList<>();

        int arraySize = 5;
        for (int i = 0; i < arraySize; i++) {
            BookingDTO bookingDTO = TestUtil.getTestBookingDTO(itemId, ownerId, userId);
            bookingDTO.setId((long) i);
            outList.add(bookingDTO);
        }

        Mockito.when(service.getBookingsByState(null, null, userId, "ALL")).thenReturn(outList);
        BookingDTO firstBookingDTO = outList.get(0);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(arraySize)))
                .andExpect(jsonPath("$[0].id", Matchers.is(firstBookingDTO.getId()), Long.class))
                .andExpect(jsonPath("$[0].item", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(itemId), Long.class))
                .andExpect(jsonPath("$[0].start", Matchers.is(firstBookingDTO.getStart())))
                .andExpect(jsonPath("$[0].end", Matchers.is(firstBookingDTO.getEnd())))
                .andExpect(jsonPath("$[0].status", Matchers.is(firstBookingDTO.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(userId), Long.class));
    }

    @Test
    void shouldGetBookingsFromUser() throws Exception {
        Long userId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        List<BookingDTO> outList = new ArrayList<>();

        int arraySize = 5;
        for (int i = 0; i < arraySize; i++) {
            BookingDTO bookingDTO = TestUtil.getTestBookingDTO(itemId, ownerId, userId);
            bookingDTO.setId((long) i);
            outList.add(bookingDTO);
        }

        Mockito.when(service.getBookingsByStateFromOwner(null, null, ownerId, "ALL"))
                .thenReturn(outList);
        BookingDTO firstBookingDTO = outList.get(0);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(arraySize)))
                .andExpect(jsonPath("$[0].id", Matchers.is(firstBookingDTO.getId()), Long.class))
                .andExpect(jsonPath("$[0].item", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].item.id", Matchers.is(itemId), Long.class))
                .andExpect(jsonPath("$[0].start", Matchers.is(firstBookingDTO.getStart())))
                .andExpect(jsonPath("$[0].end", Matchers.is(firstBookingDTO.getEnd())))
                .andExpect(jsonPath("$[0].status", Matchers.is(firstBookingDTO.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker", Matchers.notNullValue()))
                .andExpect(jsonPath("$[0].booker.id", Matchers.is(userId), Long.class));
    }

}
