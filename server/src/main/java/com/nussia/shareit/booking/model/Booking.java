package com.nussia.shareit.booking.model;

import com.nussia.shareit.item.Item;
import com.nussia.shareit.user.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long bookingId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrowing_user_id")
    private User borrowingUser;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

}
