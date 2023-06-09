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
import com.nussia.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

        Item item = getItemById(itemId);
        BookingDTO bookingDTO = BookingMapper.INSTANCE.toBookingDTO(bookingShort, item, getUser(userId));
        Util.validateBookingDTO(bookingDTO);

        if (!item.getAvailable()) {
            throw new BadRequestException("Item with ID " + itemId + "is unavailable");
        }

        Booking testBooking = BookingMapper.INSTANCE.toBookingEntity(bookingDTO, itemId, item.getRequest(),
                item.getOwnerId());

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
    public List<BookingDTO> getBookingsByState(Integer from, Integer size, Long userId, String state) {
        if (state == null) {
            throw new BadRequestException("State is null");
        } else if (!isUserExists(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        try {
            BookingState bookingState = BookingState.valueOf(state.toUpperCase());
            Iterable<Booking> out = null;
            LocalDateTime now = LocalDateTime.now();

            switch (bookingState) {
                case ALL:
                    out = Util.getPaginatedResult(from, size,
                            () -> repository.findAllByBorrowingUser_IdOrderByStartDateDesc(userId),
                            () -> repository.findAllByBorrowingUser_IdOrderByStartDateDesc(userId,
                                    PageRequest.of(from / size, size)));
                    break;

                case CURRENT:
                    out = Util.getPaginatedResult(from, size,
                            () -> repository.findByBorrowingUser_IdAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(
                                    userId, now, now),
                            () -> repository.findByBorrowingUser_IdAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(
                                    userId, now, now, PageRequest.of(from / size, size)));
                    break;

                case PAST:
                    out = Util.getPaginatedResult(from, size,
                            () -> repository.findByBorrowingUser_IdAndStartDateBeforeAndEndDateBeforeOrderByStartDateDesc(
                                    userId, now, now),
                            () -> repository.findByBorrowingUser_IdAndStartDateBeforeAndEndDateBeforeOrderByStartDateDesc(
                                    userId, now, now, PageRequest.of(from / size, size)));
                    break;

                case FUTURE:
                    out = Util.getPaginatedResult(from, size,
                            () -> repository.findByBorrowingUser_IdAndStartDateAfterAndEndDateAfterOrderByStartDateDesc(
                                    userId, now, now),
                            () -> repository.findByBorrowingUser_IdAndStartDateAfterAndEndDateAfterOrderByStartDateDesc(
                                    userId, now, now, PageRequest.of(from / size, size)));
                    break;

                case WAITING:
                    out = Util.getPaginatedResult(from, size,
                            () -> repository.findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(
                                    userId, BookingStatus.WAITING),
                            () -> repository.findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(
                                    userId, BookingStatus.WAITING, PageRequest.of(from / size, size)));
                    break;

                case REJECTED:
                    out = Util.getPaginatedResult(from, size,
                            () -> repository.findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(
                                    userId, BookingStatus.REJECTED),
                            () -> repository.findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(
                                    userId, BookingStatus.REJECTED, PageRequest.of(from / size, size)));
                    break;
            }

            return BookingMapper.INSTANCE.toBookingDTO(out);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }

    @Transactional
    @Override
    public List<BookingDTO> getBookingsByStateFromOwner(Integer from, Integer size, Long userId, String state) {
        if (userId == null || state == null) {
            throw new BadRequestException("State is null");
        } else if (!isUserExists(userId)) {
            throw new ObjectNotFoundException("User", userId);
        }

        try {
            BookingState bookingState = BookingState.valueOf(state.toUpperCase());
            Iterable<Booking> out = null;
            LocalDateTime now = LocalDateTime.now();

            switch (bookingState) {
                case ALL:
                    out = Util.getPaginatedResult(from, size,
                            () -> repository.findAllByItem_OwnerIdOrderByStartDateDesc(userId),
                            () -> repository.findAllByItem_OwnerIdOrderByStartDateDesc(userId,
                                    PageRequest.of(from / size, size)));
                    break;

                case CURRENT:
                    out = Util.getPaginatedResult(from, size,
                            () -> repository.findByStartDateBeforeAndEndDateAfterAndItem_OwnerIdOrderByStartDateAsc(now,
                                    now, userId),
                            () -> repository.findByStartDateBeforeAndEndDateAfterAndItem_OwnerIdOrderByStartDateAsc(now,
                                    now, userId, PageRequest.of(from / size, size)));
                    break;

                case PAST:
                    out = Util.getPaginatedResult(from, size, () ->
                            repository.findByStartDateBeforeAndEndDateBeforeAndItem_OwnerIdOrderByStartDateDesc(now,
                                    now, userId), () ->
                            repository.findByStartDateBeforeAndEndDateBeforeAndItem_OwnerIdOrderByStartDateDesc(now,
                                    now, userId, PageRequest.of(from / size, size)));
                    break;

                case FUTURE:
                    out = Util.getPaginatedResult(from, size, () ->
                            repository.findByStartDateAfterAndEndDateAfterAndItem_OwnerIdOrderByStartDateDesc(now,
                                    now, userId), () ->
                            repository.findByStartDateAfterAndEndDateAfterAndItem_OwnerIdOrderByStartDateDesc(now,
                                    now, userId, PageRequest.of(from / size, size)));
                    break;

                case WAITING:
                    out = Util.getPaginatedResult(from, size, () ->
                            repository.findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(BookingStatus.WAITING,
                                    userId), () ->
                            repository.findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(BookingStatus.WAITING,
                                    userId, PageRequest.of(from / size, size)));
                    break;

                case REJECTED:
                    out = Util.getPaginatedResult(from, size, () ->
                            repository.findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(BookingStatus.REJECTED,
                                    userId), () ->
                            repository.findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(BookingStatus.REJECTED,
                                    userId, PageRequest.of(from / size, size)));
                    break;
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
            throw new ObjectNotFoundException("Item ID is null");
        }

        LocalDateTime now = LocalDateTime.now();

        return repository.findFirstByItem_ItemIdAndStartDateBeforeAndBookingStatusEqualsOrderByEndDateDesc(itemId, now,
                BookingStatus.APPROVED).map(BookingMapper.INSTANCE::toUserBooking).orElse(null);
    }

    @Transactional
    @Override
    public UserBooking getNextBooking(Long itemId) {
        if (itemId == null) {
            throw new ObjectNotFoundException("Item ID is null");
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
