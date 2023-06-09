package com.nussia.item.dto;

import com.nussia.booking.dto.UserBooking;
import com.nussia.item.comment.dto.CommentDTO;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class ItemDTO extends SimpleItemDTO {
    private UserBooking lastBooking;
    private UserBooking nextBooking;
    private List<CommentDTO> comments;
}
