package com.nussia.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleItemDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
