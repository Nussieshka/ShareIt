package com.nussia.booking;

import com.nussia.Util;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.item.Item;
import com.nussia.item.jpa.JpaItemRepository;
import com.nussia.user.User;
import com.nussia.user.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JpaBookingService implements BookingService {

    private final JpaBookingRepository repository;

    private final JpaItemRepository itemRepository;

    private final JpaUserRepository userRepository;
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
        bookingDTO.setBooker(user.toUserDTO());

        Util.validateBookingDTO(bookingDTO);

        if (!item.getAvailable()) {
            throw new BadRequestException("Item with ID " + itemId + "is unavailable");
        }

        Booking testBooking = Util.getBookingFromBookingDTO(bookingDTO, itemId, item.getOwnerId(), userId);

        if (Objects.equals(testBooking.getItem().getOwnerId(), userId)) {
            throw new ObjectNotFoundException("Cannot add booking for your own item");
        }

        return repository.save(testBooking).toBookingDTO();
    }

    @Transactional
    @Override
    public BookingDTO approveBooking(Long bookingId, Long userId, Boolean isApproved) {
        if (bookingId == null || userId == null || isApproved == null) {
            throw new BadRequestException("Booking ID, user ID, or approval status is null");
        }

        Booking booking;

        try {
            booking = repository.getById(bookingId);
        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException("Booking", bookingId);
        }

        if (!Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new ObjectNotFoundException("User with ID " + userId + " is not authorized to approve this booking");
        } else if (booking.getBookingStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking with ID " + bookingId + " is not waiting for approval");
        }

        booking.setBookingStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return repository.save(booking).toBookingDTO();
    }

    @Transactional
    @Override
    public BookingDTO getBooking(Long bookingId, Long userId) {
        if (bookingId == null || userId == null) {
            throw new BadRequestException("Booking ID or user ID is null");
        }

        try {
            Booking booking = repository.getById(bookingId);
            if (!Objects.equals(booking.getBorrowingUser().getId(), userId) &&
                    !Objects.equals(booking.getItem().getOwnerId(), userId)) {
                throw new ObjectNotFoundException("Booking with ID " + bookingId + " not found for user with ID "
                        + userId);
            }
            return booking.toBookingDTO();
        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException("Booking", bookingId);
        }
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
            Timestamp now = new Timestamp(System.currentTimeMillis());

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

            return Util.getBookingDTOFromBooking(out);
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
            Timestamp now = new Timestamp(System.currentTimeMillis());

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

            return Util.getBookingDTOFromBooking(out);
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

        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            Booking booking = repository.findFirstByItem_ItemIdAndStartDateBeforeAndBookingStatusEqualsOrderByEndDateDesc(itemId, now, BookingStatus.APPROVED);

            if (booking == null) {
                return null;
            } else {
                return booking.toUserBooking();
            }
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Transactional
    @Override
    public UserBooking getNextBooking(Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("Item", itemId);
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            Booking booking = repository.findFirstByItem_ItemIdAndStartDateAfterAndBookingStatusEqualsOrderByStartDateAsc(itemId, now, BookingStatus.APPROVED);
            if (booking == null) {
                return null;
            } else {
                return booking.toUserBooking();
            }
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean isBorrowedByUser(Long userId, Long itemId) {
        return repository.existsByBorrowingUser_IdAndItem_ItemIdAndBookingStatusEqualsAndEndDateBefore(userId, itemId,
                BookingStatus.APPROVED, new Timestamp(System.currentTimeMillis()));
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
