package com.nussia.item;

import com.nussia.booking.UserBooking;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
public class ItemDTO extends SimpleItemDTO {
    private UserBooking lastBooking;
    private UserBooking nextBooking;

    private List<CommentDTO> comments;

    public ItemDTO(Long itemId, String name, String description, Boolean available, UserBooking last, UserBooking next,
                   List<CommentDTO> comments) {
        super(itemId, name, description, available);
        this.lastBooking = last;
        this.nextBooking = next;
        this.comments = comments;
    }
}
