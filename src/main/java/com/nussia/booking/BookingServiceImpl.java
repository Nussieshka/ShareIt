package com.nussia.booking;

import com.nussia.Util;
import com.nussia.booking.dto.BookingDTO;
import com.nussia.booking.dto.BookingMapper;
import com.nussia.booking.dto.BookingShort;
import com.nussia.booking.dto.UserBooking;
import com.nussia.booking.model.Booking;
import com.nussia.booking.model.BookingState;
import com.nussia.booking.model.BookingStatus;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.item.Item;
import com.nussia.item.ItemRepository;
import com.nussia.user.User;
import com.nussia.user.dto.UserMapper;
import com.nussia.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;
    @Transactional
    @Override
    public BookingDTO addBooking(BookingShort bookingShort, Long userId) {
        if (bookingShort == null || userId == null) {
            throw new BadRequestException("Invalid parameters: bookingShort or userId is null");
        }

        Long itemId = bookingShort.getItemId();
        BookingDTO bookingDTO = new BookingDTO();
        Item item = getItemById(itemId);
        User user = getUser(userId);
        bookingDTO.setStart(bookingShort.getStart());
        bookingDTO.setEnd(bookingShort.getEnd());
        bookingDTO.setStatus(BookingStatus.WAITING);
        bookingDTO.setItem(item.toSimpleItemDTO());
        bookingDTO.setBooker(UserMapper.INSTANCE.toUserDTO(user));

        Util.validateBookingDTO(bookingDTO);

        if (!item.getAvailable()) {
            throw new BadRequestException("Item with ID " + itemId + "is unavailable");
        }

        Booking testBooking = BookingMapper.INSTANCE.toBookingEntity(bookingDTO, itemId, item.getOwnerId());

        if (Objects.equals(testBooking.getItem().getOwnerId(), userId)) {
            throw new ObjectNotFoundException("Cannot add booking for your own item");
        }

        return BookingMapper.INSTANCE.toBookingDTO(repository.save(testBooking));
    }

    @Transactional
    @Override
    public BookingDTO approveBooking(Long bookingId, Long userId, Boolean isApproved) {
        if (bookingId == null || userId == null || isApproved == null) {
            throw new BadRequestException("Booking ID, user ID, or approval status is null");
        }

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking", bookingId));

        if (!Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new ObjectNotFoundException("User with ID " + userId + " is not authorized to approve this booking");
        } else if (booking.getBookingStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking with ID " + bookingId + " is not waiting for approval");
        }

        booking.setBookingStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.INSTANCE.toBookingDTO(repository.save(booking));
    }

    @Transactional
    @Override
    public BookingDTO getBooking(Long bookingId, Long userId) {
        if (bookingId == null || userId == null) {
            throw new BadRequestException("Booking ID or user ID is null");
        }

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking", bookingId));

        if (!booking.getBorrowingUser().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new ObjectNotFoundException("Booking with ID " + bookingId + " not found for user with ID " + userId);
        }

        return BookingMapper.INSTANCE.toBookingDTO(booking);
    }

    @Transactional
    @Override
    public List<BookingDTO> getBookingsByState(Long userId, String state) {
        if (state == null) {
            throw new BadRequestException("State is null");
        } else if (!isUserExists(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        try {
            BookingState bookingState = BookingState.valueOf(state.toUpperCase());
            List<Booking> out;
            LocalDateTime now = LocalDateTime.now();

            switch (bookingState) {
                case ALL:
                    out = repository.findAllByBorrowingUser_IdOrderByStartDateDesc(userId);
                    break;

                case CURRENT:
                    out = repository.findByBorrowingUser_IdAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(
                            userId, now, now);
                    break;

                case PAST:
                    out = repository.findByBorrowingUser_IdAndStartDateBeforeAndEndDateBeforeOrderByStartDateDesc(
                            userId, now, now);
                    break;

                case FUTURE:
                    out = repository.findByBorrowingUser_IdAndStartDateAfterAndEndDateAfterOrderByStartDateDesc(
                            userId, now, now);
                    break;

                case WAITING:
                    out = repository.findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(
                            userId, BookingStatus.WAITING);
                    break;

                case REJECTED:
                    out = repository.findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(
                            userId, BookingStatus.REJECTED);
                    break;

                default:
                    throw new BadRequestException("Unknown state: " + state);
            }

            return BookingMapper.INSTANCE.toBookingDTO(out);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }

    @Transactional
    @Override
    public List<BookingDTO> getBookingsByStateFromOwner(Long userId, String state) {
        if (userId == null || state == null) {
            throw new BadRequestException("State is null");
        } else if (!isUserExists(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        try {
            BookingState bookingState = BookingState.valueOf(state.toUpperCase());
            List<Booking> out;
            LocalDateTime now = LocalDateTime.now();

            switch (bookingState) {
                case ALL:
                    out = repository.findAllByItem_OwnerIdOrderByStartDateDesc(userId);
                    break;

                case CURRENT:
                    out = repository.findByStartDateBeforeAndEndDateAfterAndItem_OwnerIdOrderByStartDateAsc(now, now,
                            userId);
                    break;

                case PAST:
                    out = repository.findByStartDateBeforeAndEndDateBeforeAndItem_OwnerIdOrderByStartDateDesc(now, now,
                            userId);
                    break;

                case FUTURE:
                    out = repository.findByStartDateAfterAndEndDateAfterAndItem_OwnerIdOrderByStartDateDesc(now, now,
                            userId);
                    break;

                case WAITING:
                    out = repository.findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(BookingStatus.WAITING,
                            userId);
                    break;

                case REJECTED:
                    out = repository.findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(BookingStatus.REJECTED,
                            userId);
                    break;

                default:
                    throw new BadRequestException("Unknown state: " + state);
            }

            return BookingMapper.INSTANCE.toBookingDTO(out);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }

    @Transactional
    @Override
    public UserBooking getLastBooking(Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("Item", itemId);
        }

        LocalDateTime now = LocalDateTime.now();

        return repository.findFirstByItem_ItemIdAndStartDateBeforeAndBookingStatusEqualsOrderByEndDateDesc(itemId, now,
                BookingStatus.APPROVED).map(BookingMapper.INSTANCE::toUserBooking).orElse(null);
    }

    @Transactional
    @Override
    public UserBooking getNextBooking(Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("Item", itemId);
        }

        LocalDateTime now = LocalDateTime.now();

        return repository.findFirstByItem_ItemIdAndStartDateAfterAndBookingStatusEqualsOrderByStartDateAsc(itemId, now,
                BookingStatus.APPROVED).map(BookingMapper.INSTANCE::toUserBooking).orElse(null);
    }

    @Override
    public boolean isBorrowedByUser(Long userId, Long itemId) {
        return repository.existsByBorrowingUser_IdAndItem_ItemIdAndBookingStatusEqualsAndEndDateBefore(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now());
    }

    private Item getItemById(Long itemId) {
        if (itemId == null) {
            throw new BadRequestException("Invalid parameter: itemId is null");
        }
        return itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item", itemId));
    }

    private User getUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        return userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));
    }

    public Boolean isUserExists(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        return userRepository.existsById(userId);
    }
}
