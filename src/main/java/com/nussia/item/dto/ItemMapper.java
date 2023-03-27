package com.nussia.item.dto;

import com.nussia.booking.dto.BookingMapper;
import com.nussia.booking.dto.UserBooking;
import com.nussia.item.Item;
import com.nussia.item.comment.Comment;
import com.nussia.item.comment.CommentMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper(uses = { CommentMapper.class, BookingMapper.class })
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "id", source = "itemId")
    SimpleItemDTO toSimpleItemDTO(Item item);

    @Mapping(target = "id", source = "item.itemId")
    @Mapping(target = "lastBooking", expression = "java(recentBookings != null ? recentBookings.getKey() : null)")
    @Mapping(target = "nextBooking", expression = "java(recentBookings != null ? recentBookings.getValue() : null)")
    ItemDTO toItemDTO(Item item, List<Comment> comments, @Context Map.Entry<UserBooking, UserBooking> recentBookings);

    default ItemDTO toItemDTO(Item item, List<Comment> comments) {
        return INSTANCE.toItemDTO(item, comments, null);
    }

    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "ownerId", source = "userId")
    @Mapping(target = "comments", expression = "java(new ArrayList<>())")
    Item toItemEntity(SimpleItemDTO itemDTO, Long userId, Long id);

    default Item toItemEntity(SimpleItemDTO itemDTO, Long userId) {
        return INSTANCE.toItemEntity(itemDTO, userId, null);
    }
}
