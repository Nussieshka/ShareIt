package com.nussia.shareit.item.dto;

import com.nussia.shareit.booking.dto.UserBooking;
import com.nussia.shareit.item.comment.dto.CommentDTO;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class ItemDTO extends SimpleItemDTO {
    private UserBooking lastBooking;
    private UserBooking nextBooking;
    private List<CommentDTO> comments;
}
