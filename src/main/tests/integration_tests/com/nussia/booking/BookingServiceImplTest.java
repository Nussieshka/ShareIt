package integration_tests.com.nussia.booking;

import com.nussia.Util;
import com.nussia.booking.BookingService;
import com.nussia.booking.BookingServiceImpl;
import com.nussia.booking.dto.BookingDTO;
import com.nussia.booking.dto.BookingShort;
import com.nussia.booking.model.Booking;
import com.nussia.booking.model.BookingState;
import com.nussia.booking.model.BookingStatus;
import com.nussia.item.Item;
import com.nussia.item.ItemServiceImpl;
import com.nussia.item.dto.ItemMapper;
import com.nussia.request.RequestServiceImpl;
import com.nussia.user.UserServiceImpl;
import com.nussia.user.dto.UserMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import util.IntegrationTestUtil;
import util.TestPersistenceConfig;
import util.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@TestPropertySource(properties = { "db.name = test_share_it" })
@SpringJUnitConfig( { TestPersistenceConfig.class, UserServiceImpl.class, ItemServiceImpl.class,
        BookingServiceImpl.class, RequestServiceImpl.class, IntegrationTestUtil.class })
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final BookingService bookingService;

    private final IntegrationTestUtil util;

    @Test
    void shouldAddBookings() {
        Long itemOwnerId = util.createUser().getId();
        Long itemId = util.createItem(itemOwnerId).getItemId();
        Long bookerId = util.createUser().getId();
        BookingShort bookingShort = TestUtil.getTestBookingShort(itemId);
        Booking booking = util.createBooking(bookingShort, bookerId);

        assertThat(booking.getBookingId(), notNullValue());
        assertThat(Util.localDataTimeToString(booking.getStartDate()), equalTo(bookingShort.getStart()));
        assertThat(Util.localDataTimeToString(booking.getEndDate()), equalTo(bookingShort.getEnd()));
        assertThat(booking.getItem().getItemId(), equalTo(itemId));
        assertThat(booking.getBookingStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void shouldApproveBookings() {
        Booking booking = util.createBooking();
        Long bookingId = booking.getBookingId();

        BookingDTO approvedBooking =
                bookingService.approveBooking(bookingId, booking.getItem().getOwner().getId(), true);

        assertThat(approvedBooking.getId(), equalTo(approvedBooking.getId()));
        assertThat(booking.getBookingStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void shouldGetBookings() {
        Booking booking = util.createBooking();
        Long bookingId = booking.getBookingId();
        BookingDTO bookingDTO = bookingService.getBooking(bookingId, booking.getBorrowingUser().getId());

        assertThat(bookingDTO.getId(), equalTo(bookingId));
        assertThat(bookingDTO.getStatus(), equalTo(booking.getBookingStatus()));
        assertThat(bookingDTO.getBooker(), equalTo(UserMapper.INSTANCE.toUserDTO(booking.getBorrowingUser())));
        assertThat(bookingDTO.getStart(), equalTo(Util.localDataTimeToString(booking.getStartDate())));
        assertThat(bookingDTO.getEnd(), equalTo(Util.localDataTimeToString(booking.getEndDate())));
        assertThat(bookingDTO.getItem(), equalTo(ItemMapper.INSTANCE.toSimpleItemDTO(booking.getItem())));
    }

    @Test
    void shouldGetBookingsByState() {
        List<Booking> bookingList = new ArrayList<>();
        Long bookerId = util.createUser().getId();
        for (int i = 0; i < 5; i++) {
            bookingList.add(util.createBooking(bookerId));
        }

        Booking approvedBooking = bookingList.get(2);
        bookingService.approveBooking(approvedBooking.getBookingId(),
                approvedBooking.getItem().getOwner().getId(), true);

        List<BookingDTO> allBookings = bookingService.getBookingsByState(null, null, bookerId,
                BookingState.WAITING.name());

        assertThat(allBookings.size(), equalTo(bookingList.size() - 1));
    }

    @Test
    void shouldGetBookingsByStateFromOwner() {
        List<Booking> bookingList = new ArrayList<>();
        Long itemOwnerId = util.createUser().getId();
        for (int i = 0; i < 5; i++) {
            bookingList.add(util.createBooking(itemOwnerId, util.createUser().getId()));
        }

        Booking approvedBooking = bookingList.get(2);
        bookingService.approveBooking(approvedBooking.getBookingId(), itemOwnerId, true);

        List<BookingDTO> allBookings = bookingService.getBookingsByStateFromOwner(null, null, itemOwnerId,
                BookingState.WAITING.name());

        assertThat(allBookings.size(), equalTo(bookingList.size() - 1));
    }

    @Test
    void shouldReturnTrueIfBorrowedByUser() {
        Long itemOwnerId = util.createUser().getId();
        Item item = util.createItem(itemOwnerId);
        Booking booking = TestUtil.getTestBooking(null, util.createUser().getId(),
                item, BookingStatus.APPROVED, -123, -122);
        util.insertBooking(booking);

        assertThat(bookingService.isBorrowedByUser(booking.getBorrowingUser().getId(),
                item.getItemId()), equalTo(true));
    }
}
