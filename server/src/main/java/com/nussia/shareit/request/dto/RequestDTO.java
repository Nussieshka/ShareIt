package com.nussia.shareit.request.dto;

import com.nussia.shareit.item.dto.SimpleItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestDTO {
    private Long id;
    private String description;
    private String created;
    private List<SimpleItemDTO> items;
}
