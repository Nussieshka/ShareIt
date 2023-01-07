package com.nussia.item;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Item {
    private Long itemId;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private Map<Long, String> reviews;

    public ItemDTO toItemDTO() {
        return new ItemDTO(itemId, name, description, available);
    }
}
