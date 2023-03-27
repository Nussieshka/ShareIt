package com.nussia.booking.dto;

import com.nussia.booking.model.Booking;
import com.nussia.item.dto.ItemMapper;
import com.nussia.user.dto.UserMapper;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = { ItemMapper.class, UserMapper.class })
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(source = "bookingId", target = "id")
    @Mapping(source = "startDate", target = "start", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "endDate", target = "end", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "borrowingUser", target = "booker")
    @Mapping(source = "bookingStatus", target = "status")
    BookingDTO toBookingDTO(Booking booking);

    @Mapping(source = "bookingId", target = "id")
    @Mapping(target = "bookerId", expression = "java(booking.getBorrowingUser().getId())")
    UserBooking toUserBooking(Booking booking);

    @IterableMapping(elementTargetType = BookingDTO.class)
    List<BookingDTO> toBookingDTO(Iterable<Booking> bookings);

    @Mapping(source = "bookingDTO.id", target = "bookingId")
    @Mapping(source = "bookingDTO.start", target = "startDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "bookingDTO.end", target = "endDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "bookingDTO.booker", target = "borrowingUser")
    @Mapping(source = "bookingDTO.status", target = "bookingStatus")
    @Mapping(target = "item", expression = "java(itemMapper.toItemEntity(bookingDTO.getItem(), ownerId, itemId))")
    Booking toBookingEntity(BookingDTO bookingDTO, Long itemId, Long ownerId);
}
