package com.nussia.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDTO {
    private Long id;

    @NotBlank
    private String text;
    private String authorName;
    private String created;
}
