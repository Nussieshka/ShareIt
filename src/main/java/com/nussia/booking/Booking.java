package com.nussia.booking;

import com.nussia.Util;
import com.nussia.item.Item;
import com.nussia.user.User;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long bookingId;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowing_user_id")
    private User borrowingUser;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    public BookingDTO toBookingDTO() {
        return new BookingDTO(bookingId, item.toSimpleItemDTO(),
                Util.timestampToString(startDate), Util.timestampToString(endDate),
                bookingStatus, borrowingUser.toUserDTO());
    }

    public UserBooking toUserBooking() {
        return new UserBooking(bookingId, borrowingUser.getId());
    }
}
