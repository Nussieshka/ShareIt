package json_tests.com.nussia.shareit.booking;

import com.nussia.shareit.booking.dto.BookingDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import util.TestPersistenceConfig;
import util.TestUtil;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SpringJUnitConfig( { TestPersistenceConfig.class })
@TestPropertySource(properties = { "db.name = test_share_it" })
public class BookingJSONTest {

    @Autowired
    private JacksonTester<BookingDTO> json;

    @Test
    void testBookingDTO() throws IOException {
        Long bookingId = 3L;
        Long itemId = 0L;
        Long userId = 2L;
        BookingDTO bookingDTO = TestUtil.getTestBookingDTO(itemId, 1L, userId);
        bookingDTO.setId(bookingId);
        JsonContent<BookingDTO> result = json.write(bookingDTO);

        assertThat(result).extractingJsonPathValue("$.id").asString().isEqualTo(String.valueOf(bookingId));
        assertThat(result).extractingJsonPathValue("$.item").isNotNull();
        assertThat(result).extractingJsonPathValue("$.item.id").asString().isEqualTo(String.valueOf(itemId));
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDTO.getStart());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDTO.getEnd());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDTO.getStatus().toString());
        assertThat(result).extractingJsonPathValue("$.booker").isNotNull();
        assertThat(result).extractingJsonPathValue("$.booker.id").asString().isEqualTo(String.valueOf(userId));
    }
}
