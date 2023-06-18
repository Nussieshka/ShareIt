package com.nussia.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDTO {
    private Long id;
    private String text;
    private String authorName;
    private String created;
}
