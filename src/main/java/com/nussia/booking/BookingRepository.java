package com.nussia.booking;

import com.nussia.booking.model.Booking;
import com.nussia.booking.model.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBorrowingUser_IdOrderByStartDateDesc(Long borrowingUserId);

    List<Booking> findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(Long borrowingUserId,
                                                                             BookingStatus bookingStatus);

    List<Booking> findByBorrowingUser_IdAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(Long borrowingUserId,
                                                                                             LocalDateTime startDate,
                                                                                             LocalDateTime endDate);

    List<Booking> findByBorrowingUser_IdAndStartDateBeforeAndEndDateBeforeOrderByStartDateDesc(Long borrowingUserId,
                                                                                               LocalDateTime startDate,
                                                                                               LocalDateTime endDate);

    List<Booking> findByBorrowingUser_IdAndStartDateAfterAndEndDateAfterOrderByStartDateDesc(Long borrowingUserId,
                                                                                             LocalDateTime startDate,
                                                                                             LocalDateTime endDate);

    Page<Booking> findAllByBorrowingUser_IdOrderByStartDateDesc(Long borrowingUserId, Pageable pageable);

    Page<Booking> findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(Long borrowingUserId,
                                                                             BookingStatus bookingStatus,
                                                                             Pageable pageable);

    Page<Booking> findByBorrowingUser_IdAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(Long borrowingUserId,
                                                                                             LocalDateTime startDate,
                                                                                             LocalDateTime endDate,
                                                                                             Pageable pageable);

    Page<Booking> findByBorrowingUser_IdAndStartDateBeforeAndEndDateBeforeOrderByStartDateDesc(Long borrowingUserId,
                                                                                               LocalDateTime startDate,
                                                                                               LocalDateTime endDate,
                                                                                               Pageable pageable);

    Page<Booking> findByBorrowingUser_IdAndStartDateAfterAndEndDateAfterOrderByStartDateDesc(Long borrowingUserId,
                                                                                             LocalDateTime startDate,
                                                                                             LocalDateTime endDate,
                                                                                             Pageable pageable);

    List<Booking> findAllByItem_OwnerIdOrderByStartDateDesc(Long itemOwnerId);

    List<Booking> findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(BookingStatus bookingStatus, Long itemOwnerId);

    List<Booking> findByStartDateBeforeAndEndDateAfterAndItem_OwnerIdOrderByStartDateAsc(LocalDateTime startDate,
                                                                                         LocalDateTime endDate,
                                                                                         Long itemOwnerId);
    List<Booking> findByStartDateBeforeAndEndDateBeforeAndItem_OwnerIdOrderByStartDateDesc(LocalDateTime startDate,
                                                                                           LocalDateTime endDate,
                                                                                           Long itemOwnerId);
    List<Booking> findByStartDateAfterAndEndDateAfterAndItem_OwnerIdOrderByStartDateDesc(LocalDateTime startDate,
                                                                                         LocalDateTime endDate,
                                                                                         Long itemOwnerId);

    Page<Booking> findAllByItem_OwnerIdOrderByStartDateDesc(Long itemOwnerId,
                                                            Pageable pageable);

    Page<Booking> findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(BookingStatus bookingStatus, Long itemOwnerId,
                                                                         Pageable pageable);

    Page<Booking> findByStartDateBeforeAndEndDateAfterAndItem_OwnerIdOrderByStartDateAsc(LocalDateTime startDate,
                                                                                         LocalDateTime endDate,
                                                                                         Long itemOwnerId,
                                                                                         Pageable pageable);
    Page<Booking> findByStartDateBeforeAndEndDateBeforeAndItem_OwnerIdOrderByStartDateDesc(LocalDateTime startDate,
                                                                                           LocalDateTime endDate,
                                                                                           Long itemOwnerId,
                                                                                           Pageable pageable);
    Page<Booking> findByStartDateAfterAndEndDateAfterAndItem_OwnerIdOrderByStartDateDesc(LocalDateTime startDate,
                                                                                         LocalDateTime endDate,
                                                                                         Long itemOwnerId,
                                                                                         Pageable pageable);

    Optional<Booking> findFirstByItem_ItemIdAndStartDateBeforeAndBookingStatusEqualsOrderByEndDateDesc(Long itemId,
                                                                                                       LocalDateTime endDate,
                                                                                                       BookingStatus bookingStatus);

    Optional<Booking> findFirstByItem_ItemIdAndStartDateAfterAndBookingStatusEqualsOrderByStartDateAsc(Long itemId,
                                                                                                       LocalDateTime startDate,
                                                                                                       BookingStatus bookingStatus);

    boolean existsByBorrowingUser_IdAndItem_ItemIdAndBookingStatusEqualsAndEndDateBefore(Long userId, Long itemId,
                                                                                         BookingStatus bookingStatus,
                                                                                         LocalDateTime endDate);
}
