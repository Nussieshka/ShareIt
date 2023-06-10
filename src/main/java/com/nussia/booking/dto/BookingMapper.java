package com.nussia.booking.dto;

import com.nussia.booking.model.Booking;
import com.nussia.booking.model.BookingStatus;
import com.nussia.item.Item;
import com.nussia.item.dto.ItemMapper;
import com.nussia.request.Request;
import com.nussia.user.User;
import com.nussia.user.dto.UserMapper;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = { ItemMapper.class, UserMapper.class }, imports = BookingStatus.class)
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(source = "bookingId", target = "id")
    @Mapping(source = "startDate", target = "start", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "endDate", target = "end", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "borrowingUser", target = "booker")
    @Mapping(source = "bookingStatus", target = "status")
    BookingDTO toBookingDTO(Booking booking);

    @Mapping(target = "status", expression = "java(BookingStatus.WAITING)")
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "booker", source = "user")
    BookingDTO toBookingDTO(BookingShort bookingShort, Item item, User user);

    @IterableMapping(elementTargetType = BookingDTO.class)
    List<BookingDTO> toBookingDTO(Iterable<Booking> bookings);

    @Mapping(source = "bookingId", target = "id")
    @Mapping(target = "bookerId", expression = "java(booking.getBorrowingUser().getId())")
    UserBooking toUserBooking(Booking booking);

    @Mapping(source = "bookingDTO.id", target = "bookingId")
    @Mapping(source = "bookingDTO.start", target = "startDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "bookingDTO.end", target = "endDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "bookingDTO.booker", target = "borrowingUser")
    @Mapping(source = "bookingDTO.status", target = "bookingStatus")
    @Mapping(target = "item", expression = "java(itemMapper.toItemEntity(bookingDTO.getItem(), owner, request, itemId))")
    Booking toBookingEntity(BookingDTO bookingDTO, Long itemId, Request request, User owner);
}
