package com.nussia.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBorrowingUser_IdOrderByStartDateDesc(Long borrowingUserId);

    List<Booking> findByBorrowingUser_IdAndBookingStatusOrderByStartDateDesc(Long borrowingUserId,
                                                                             BookingStatus bookingStatus);

    List<Booking> findByBorrowingUser_IdAndStartDateBeforeAndEndDateAfterOrderByStartDateAsc(Long borrowingUserId,
                                                                                              Timestamp startDate,
                                                                                              Timestamp endDate);

    List<Booking> findByBorrowingUser_IdAndStartDateBeforeAndEndDateBeforeOrderByStartDateDesc(Long borrowingUserId,
                                                                                               Timestamp startDate,
                                                                                               Timestamp endDate);

    List<Booking> findByBorrowingUser_IdAndStartDateAfterAndEndDateAfterOrderByStartDateDesc(Long borrowingUserId,
                                                                                             Timestamp startDate,
                                                                                             Timestamp endDate);

    List<Booking> findAllByItem_OwnerIdOrderByStartDateDesc(Long itemOwnerId);

    List<Booking> findByBookingStatusAndItem_OwnerIdOrderByStartDateDesc(BookingStatus bookingStatus, Long itemOwnerId);

    List<Booking> findByStartDateBeforeAndEndDateAfterAndItem_OwnerIdOrderByStartDateAsc(Timestamp startDate,
                                                                                          Timestamp endDate,
                                                                                          Long itemOwnerId);
    List<Booking> findByStartDateBeforeAndEndDateBeforeAndItem_OwnerIdOrderByStartDateDesc(Timestamp startDate,
                                                                                           Timestamp endDate,
                                                                                           Long itemOwnerId);
    List<Booking> findByStartDateAfterAndEndDateAfterAndItem_OwnerIdOrderByStartDateDesc(Timestamp startDate,
                                                                                         Timestamp endDate,
                                                                                         Long itemOwnerId);

    Booking findFirstByItem_ItemIdAndStartDateBeforeAndBookingStatusEqualsOrderByEndDateDesc(Long itemId,
                                                                                             Timestamp endDate,
                                                                                             BookingStatus bookingStatus);

    Booking findFirstByItem_ItemIdAndStartDateAfterAndBookingStatusEqualsOrderByStartDateAsc(Long itemId,
                                                                                             Timestamp startDate,
                                                                                             BookingStatus bookingStatus);

    boolean existsByBorrowingUser_IdAndItem_ItemIdAndBookingStatusEqualsAndEndDateBefore(Long userId, Long itemId,
                                                                                         BookingStatus bookingStatus,
                                                                                         Timestamp endDate);
}
