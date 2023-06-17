package com.nussia.shareit.request.dto;

import com.nussia.shareit.item.dto.SimpleItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestDTO {
    private Long id;
    @NotBlank
    private String description;
    private String created;
    private List<SimpleItemDTO> items;
}
