package com.nussia.item.dto;

import com.nussia.booking.dto.BookingMapper;
import com.nussia.booking.dto.UserBooking;
import com.nussia.item.Item;
import com.nussia.item.comment.Comment;
import com.nussia.item.comment.dto.CommentMapper;
import com.nussia.request.Request;
import com.nussia.user.User;
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
    @Mapping(target = "requestId", expression = "java(item.getRequest() != null ? item.getRequest().getId() : null)")
    SimpleItemDTO toSimpleItemDTO(Item item);

    @Mapping(target = "id", source = "item.itemId")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "requestId", expression = "java(item.getRequest() != null ? item.getRequest().getId() : null)")
    @Mapping(target = "lastBooking", expression = "java(recentBookings != null ? recentBookings.getKey() : null)")
    @Mapping(target = "nextBooking", expression = "java(recentBookings != null ? recentBookings.getValue() : null)")
    ItemDTO toItemDTO(Item item, List<Comment> comments, @Context Map.Entry<UserBooking, UserBooking> recentBookings);

    ItemDTO toItemDTO(SimpleItemDTO simpleItemDTO);

    default ItemDTO toItemDTO(Item item, List<Comment> comments) {
        return INSTANCE.toItemDTO(item, comments, null);
    }

    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "comments", expression = "java(new ArrayList<>())")
    @Mapping(target = "description", source = "itemDTO.description")
    @Mapping(target = "name", source = "itemDTO.name")
    Item toItemEntity(SimpleItemDTO itemDTO, User user, Request request, Long id);

    default Item toItemEntity(SimpleItemDTO itemDTO, Request request, User owner) {
        return INSTANCE.toItemEntity(itemDTO, owner, request, null);
    }

    default Item toItemEntity(SimpleItemDTO itemDTO, User owner) {
        return INSTANCE.toItemEntity(itemDTO, owner, null, null);
    }
}
