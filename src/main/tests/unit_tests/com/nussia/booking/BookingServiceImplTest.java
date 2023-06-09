package unit_tests.com.nussia.booking;

import util.TestUtil;
import com.nussia.booking.BookingRepository;
import com.nussia.booking.BookingServiceImpl;
import com.nussia.booking.dto.BookingDTO;
import com.nussia.booking.dto.BookingMapper;
import com.nussia.booking.dto.BookingShort;
import com.nussia.booking.model.Booking;
import com.nussia.booking.model.BookingStatus;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.item.Item;
import com.nussia.item.ItemRepository;
import com.nussia.user.User;
import com.nussia.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl service;

    @Test
    void shouldNotAddBookingsWithNullArguments() {
        assertThrows(BadRequestException.class, () -> service.addBooking(null, null));
    }

    @Test
    void shouldNotAddBookingsWithNullItemId() {
        Long userId = 0L;
        BookingShort bookingShort = TestUtil.getTestBookingShort(null);
        assertThrows(BadRequestException.class, () -> service.addBooking(bookingShort, userId));
    }

    @Test
    void shouldNotAddBookingsWithIncorrectItemId() {
        Long userId = 0L;
        Long itemId = 1L;

        BookingShort bookingShort = TestUtil.getTestBookingShort(itemId);

        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.addBooking(bookingShort, userId));
    }

    @Test
    void shouldNotAddBookingsWithIncorrectUserId() {
        Long userId = 0L;
        Long itemOwnerId = 2L;
        Long itemId = 1L;

        BookingShort bookingShort = TestUtil.getTestBookingShort(itemId);
        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), itemOwnerId);

        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.addBooking(bookingShort, userId));
    }

    @Test
    void shouldNotAddBookingsWhenItemIsUnavailable() {
        Long userId = 0L;
        Long itemOwnerId = 2L;
        Long itemId = 1L;

        BookingShort bookingShort = TestUtil.getTestBookingShort(itemId);
        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), itemOwnerId);
        item.setAvailable(false);

        User user = TestUtil.getTestUser(userId);

        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> service.addBooking(bookingShort, userId));
    }

    @Test
    void shouldNotAddBookingsWhenCurrentUserIsAnItemOwner() {
        Long userId = 2L;
        Long itemOwnerId = 2L;
        Long itemId = 1L;

        BookingShort bookingShort = TestUtil.getTestBookingShort(itemId);
        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), itemOwnerId);
        User user = TestUtil.getTestUser(userId);

        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(user));

        assertThrows(ObjectNotFoundException.class, () -> service.addBooking(bookingShort, userId));
    }

    @Test
    void shouldAddBookings() {
        Long userId = 0L;
        Long itemOwnerId = 2L;
        Long itemId = 1L;
        Long bookingId = 3L;

        BookingShort bookingShort = TestUtil.getTestBookingShort(itemId);
        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), itemOwnerId);
        User user = TestUtil.getTestUser(userId);
        BookingDTO bookingDTO = BookingMapper.INSTANCE.toBookingDTO(bookingShort, item, user);
        bookingDTO.setId(bookingId);
        Booking booking = BookingMapper.INSTANCE.toBookingEntity(bookingDTO, itemId, item.getRequest(), itemOwnerId);

        Mockito.when(itemRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(repository.save(ArgumentMatchers.any(Booking.class))).thenReturn(booking);

        assertThat(service.addBooking(bookingShort, userId), equalTo(bookingDTO));
    }

    @Test
    void shouldNotApproveBookingsWithNullArguments() {
        assertThrows(BadRequestException.class,
                () -> service.approveBooking(null, null, null));
    }

    @Test
    void shouldNotApproveBookingsWithIncorrectBookingId() {
        Long bookingId = 0L;
        Long userId = 1L;
        Boolean isApproved = true;

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.approveBooking(bookingId, userId, isApproved));
    }

    @Test
    void shouldNotApproveBookingsWhenCurrentUserIsNotAnItemOwner() {
        Long bookingId = 0L;
        Long userId = 1L;
        Long itemId = 2L;
        Long itemOwnerId = 3L;
        Long borrowerId = 4L;
        Boolean isApproved = true;

        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), itemOwnerId);
        Booking booking = TestUtil.getTestBooking(bookingId, borrowerId, item);
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class, () -> service.approveBooking(bookingId, userId, isApproved));
    }

    @Test
    void shouldNotApproveBookingsWhenBookingIsNotAwaitingForApproval() {
        Long bookingId = 0L;
        Long userId = 1L;
        Long itemId = 2L;
        Long borrowerId = 4L;
        Boolean isApproved = true;

        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), userId);
        Booking booking = TestUtil.getTestBooking(bookingId, borrowerId, item);
        booking.setBookingStatus(BookingStatus.REJECTED);
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> service.approveBooking(bookingId, userId, isApproved));
    }

    @Test
    void shouldApproveBookingsIfApproved() {
        Long bookingId = 0L;
        Long userId = 1L;
        Long itemId = 2L;
        Long borrowerId = 4L;
        Boolean isApproved = true;

        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), userId);
        Booking booking = TestUtil.getTestBooking(bookingId, borrowerId, item);
        booking.setBookingStatus(BookingStatus.WAITING);
        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(repository.save(ArgumentMatchers.any(Booking.class)))
                .thenAnswer(x -> x.getArgument(0, Booking.class));

        BookingDTO out = service.approveBooking(bookingId, userId, isApproved);

        assertThat(out.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void shouldRejectBookingsIfRejected() {
        Long bookingId = 0L;
        Long userId = 1L;
        Long itemId = 2L;
        Long borrowerId = 4L;
        Boolean isApproved = false;

        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), userId);
        Booking booking = TestUtil.getTestBooking(bookingId, borrowerId, item);
        booking.setBookingStatus(BookingStatus.WAITING);

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(repository.save(ArgumentMatchers.any(Booking.class)))
                .thenAnswer(x -> x.getArgument(0, Booking.class));

        BookingDTO out = service.approveBooking(bookingId, userId, isApproved);

        assertThat(out.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void shouldNotGetBookingWithNullArguments() {
        assertThrows(BadRequestException.class,
                () -> service.getBooking(null, null));
    }

    @Test
    void shouldNotGetBookingWithIncorrectBookingId() {
        Long bookingId = 0L;
        Long userId = 1L;

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.getBooking(bookingId, userId));
    }

    @Test
    void shouldNotGetBookingWhenNotBookingOwnerAndNotItemOwner() {
        Long bookingId = 0L;
        Long userId = 1L;
        Long itemId = 2L;
        Long itemOwnerId = 3L;
        Long borrowerId = 4L;

        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), itemOwnerId);
        Booking booking = TestUtil.getTestBooking(bookingId, borrowerId, item);

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class, () -> service.getBooking(bookingId, userId));
    }

    @Test
    void shouldGetBookingForBookingOwner() {
        Long bookingId = 0L;
        Long userId = 1L;
        Long itemId = 2L;
        Long itemOwnerId = 3L;

        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), itemOwnerId);
        Booking booking = TestUtil.getTestBooking(bookingId, userId, item);

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));

        assertThat(service.getBooking(bookingId, userId), equalTo(BookingMapper.INSTANCE.toBookingDTO(booking)));
    }

    @Test
    void shouldGetBookingForItemOwner() {
        Long bookingId = 0L;
        Long userId = 1L;
        Long itemId = 2L;
        Long borrowerId = 4L;

        Item item = TestUtil.getItemWithId(TestUtil.getTestItemDTO(itemId), userId);
        Booking booking = TestUtil.getTestBooking(bookingId, borrowerId, item);

        Mockito.when(repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(booking));

        assertThat(service.getBooking(bookingId, userId), equalTo(BookingMapper.INSTANCE.toBookingDTO(booking)));
    }

    @Test
    void shouldNotGetBookingsByStateWithNullArguments() {
        assertThrows(BadRequestException.class,
                () -> service.getBookingsByState(null, null, null, null));
    }

    @Test
    void shouldNotGetBookingsByStateWhenUserDoesNotExist() {
        Long userId = 1L;
        String state = "ALL";

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class,
                () -> service.getBookingsByState(null, null, userId, state));
    }

    @Test
    void shouldNotGetBookingsByStateWithIncorrectState() {
        Long userId = 1L;
        String state = "THIS_IS_AN_INCORRECT_STATE";

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.getBookingsByState(null, null, userId, state));
    }

    @Test
    void shouldGetBookingsByAllState() {
        Long userId = 1L;
        String state = "ALL";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findAllByBorrowingUser_IdOrderByStartDateDesc(ArgumentMatchers.anyLong()))
                .thenReturn(bookings);

        assertThat(service.getBookingsByState(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsByCurrentState() {
        Long userId = 1L;
        String state = "CURRENT";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByBorrowingUser_IdAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(
                ArgumentMatchers.anyLong(), ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(LocalDateTime.class))).thenReturn(bookings);

        assertThat(service.getBookingsByState(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsByPastState() {
        Long userId = 1L;
        String state = "PAST";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByBorrowingUser_IdAndStartDateBeforeAndEndDateBeforeOrderByStartDateDesc(
                ArgumentMatchers.anyLong(), ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(LocalDateTime.class))).thenReturn(bookings);

        assertThat(service.getBookingsByState(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsByFutureState() {
        Long userId = 1L;
        String state = "FUTURE";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByBorrowingUser_IdAndStartDateAfterAndEndDateAfterOrderByStartDateDesc(
                ArgumentMatchers.anyLong(), ArgumentMatchers.any(LocalDateTime.class),
                        ArgumentMatchers.any(LocalDateTime.class))).thenReturn(bookings);

        assertThat(service.getBookingsByState(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsByWaitingState() {
        Long userId = 1L;
        String state = "WAITING";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(
                ArgumentMatchers.anyLong(), ArgumentMatchers.eq(BookingStatus.WAITING))).thenReturn(bookings);

        assertThat(service.getBookingsByState(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsByRejectedState() {
        Long userId = 1L;
        String state = "REJECTED";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(
                ArgumentMatchers.anyLong(), ArgumentMatchers.eq(BookingStatus.REJECTED))).thenReturn(bookings);

        assertThat(service.getBookingsByState(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldNotGetBookingsByStateFromOwnerWithNullArguments() {
        assertThrows(BadRequestException.class,
                () -> service.getBookingsByStateFromOwner(null, null, null, null));
    }

    @Test
    void shouldNotGetBookingsByStateFromOwnerWhenUserDoesNotExist() {
        Long userId = 1L;
        String state = "ALL";

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(false);

        assertThrows(ObjectNotFoundException.class,
                () -> service.getBookingsByStateFromOwner(null, null, userId, state));
    }

    @Test
    void shouldNotGetBookingsByStateFromOwnerWithIncorrectState() {
        Long userId = 1L;
        String state = "THIS_IS_AN_INCORRECT_STATE";

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> service.getBookingsByStateFromOwner(null, null, userId, state));
    }

    @Test
    void shouldGetBookingsFromOwnerByAllState() {
        Long userId = 1L;
        String state = "ALL";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findAllByItem_OwnerIdOrderByStartDateDesc(ArgumentMatchers.anyLong()))
                .thenReturn(bookings);

        assertThat(service.getBookingsByStateFromOwner(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsFromOwnerByCurrentState() {
        Long userId = 1L;
        String state = "CURRENT";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByStartDateBeforeAndEndDateAfterAndItem_OwnerIdOrderByStartDateAsc(
                ArgumentMatchers.any(LocalDateTime.class), ArgumentMatchers.any(LocalDateTime.class),
                ArgumentMatchers.anyLong())).thenReturn(bookings);

        assertThat(service.getBookingsByStateFromOwner(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsFromOwnerByPastState() {
        Long userId = 1L;
        String state = "PAST";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByStartDateBeforeAndEndDateBeforeAndItem_OwnerIdOrderByStartDateDesc(
                ArgumentMatchers.any(LocalDateTime.class), ArgumentMatchers.any(LocalDateTime.class),
                ArgumentMatchers.anyLong())).thenReturn(bookings);

        assertThat(service.getBookingsByStateFromOwner(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsFromOwnerByFutureState() {
        Long userId = 1L;
        String state = "FUTURE";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByStartDateAfterAndEndDateAfterAndItem_OwnerIdOrderByStartDateDesc(
                ArgumentMatchers.any(LocalDateTime.class), ArgumentMatchers.any(LocalDateTime.class),
                ArgumentMatchers.anyLong())).thenReturn(bookings);

        assertThat(service.getBookingsByStateFromOwner(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsFromOwnerByWaitingState() {
        Long userId = 1L;
        String state = "WAITING";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(
                ArgumentMatchers.eq(BookingStatus.WAITING), ArgumentMatchers.anyLong())).thenReturn(bookings);

        assertThat(service.getBookingsByStateFromOwner(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldGetBookingsFromOwnerByRejectedState() {
        Long userId = 1L;
        String state = "REJECTED";
        List<Booking> bookings = TestUtil.getTestBookingsList();

        Mockito.when(userRepository.existsById(ArgumentMatchers.anyLong())).thenReturn(true);
        Mockito.when(repository.findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(
                ArgumentMatchers.eq(BookingStatus.REJECTED), ArgumentMatchers.anyLong())).thenReturn(bookings);

        assertThat(service.getBookingsByStateFromOwner(null, null, userId, state),
                equalTo(BookingMapper.INSTANCE.toBookingDTO(bookings)));
    }

    @Test
    void shouldNotGetLastBookingWithNullArguments() {
        assertThrows(ObjectNotFoundException.class, () -> service.getLastBooking(null));
    }

    @Test
    void shouldReturnNullIfLastBookingWasNotFound() {
        Long itemId = 0L;

        Mockito.when(repository.findFirstByItem_ItemIdAndStartDateBeforeAndBookingStatusEqualsOrderByEndDateDesc(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(BookingStatus.class)))
                        .thenReturn(Optional.empty());

        assertThat(service.getLastBooking(itemId), equalTo(null));
    }

    @Test
    void shouldGetLastBooking() {
        Long itemId = 0L;
        Booking booking = TestUtil.getTestBooking(0L, 1L,
                TestUtil.getItemWithId(TestUtil.getTestItemDTO(1L), 3L));
        Mockito.when(repository.findFirstByItem_ItemIdAndStartDateBeforeAndBookingStatusEqualsOrderByEndDateDesc(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(BookingStatus.class)))
                        .thenReturn(Optional.of(booking));

        assertThat(service.getLastBooking(itemId), equalTo(BookingMapper.INSTANCE.toUserBooking(booking)));
    }

    @Test
    void shouldNotGetNextBookingWithNullArguments() {
        assertThrows(ObjectNotFoundException.class, () -> service.getNextBooking(null));
    }

    @Test
    void shouldReturnNullIfNextBookingWasNotFound() {
        Long itemId = 0L;

        Mockito.when(repository.findFirstByItem_ItemIdAndStartDateAfterAndBookingStatusEqualsOrderByStartDateAsc(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(BookingStatus.class)))
                        .thenReturn(Optional.empty());

        assertThat(service.getNextBooking(itemId), equalTo(null));
    }

    @Test
    void shouldGetNextBooking() {
        Long itemId = 0L;
        Booking booking = TestUtil.getTestBooking(0L, 1L,
                TestUtil.getItemWithId(TestUtil.getTestItemDTO(1L), 3L));
        Mockito.when(repository.findFirstByItem_ItemIdAndStartDateAfterAndBookingStatusEqualsOrderByStartDateAsc(
                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(BookingStatus.class)))
                        .thenReturn(Optional.of(booking));

        assertThat(service.getNextBooking(itemId), equalTo(BookingMapper.INSTANCE.toUserBooking(booking)));
    }

    @Test
    void shouldReturnTrueIfBookingWasBorrowedByUser() {
        Long itemId = 0L;
        Long userId = 1L;

        Mockito.when(repository.existsByBorrowingUser_IdAndItem_ItemIdAndBookingStatusEqualsAndEndDateBefore(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.any(BookingStatus.class),
                Mockito.any(LocalDateTime.class))).thenReturn(true);

        assertThat(service.isBorrowedByUser(itemId, userId), equalTo(true));
    }

    @Test
    void shouldReturnFalseIfBookingWasNotBorrowedByUser() {
        Long itemId = 0L;
        Long userId = 1L;

        Mockito.when(repository.existsByBorrowingUser_IdAndItem_ItemIdAndBookingStatusEqualsAndEndDateBefore(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.any(BookingStatus.class),
                Mockito.any(LocalDateTime.class))).thenReturn(false);

        assertThat(service.isBorrowedByUser(itemId, userId), equalTo(false));
    }
}