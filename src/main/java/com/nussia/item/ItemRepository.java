package com.nussia.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<ItemDTO> addItem(ItemDTO itemDTO, Long ownerId);

    Optional<ItemDTO> editItem(ItemDTO itemDTO, Long itemId, Long ownerId);

    Optional<ItemDTO> getItemById(Long itemId);

    List<ItemDTO> getItems(Long ownerId);

    List<ItemDTO> getItemsBySearchQuery(String searchQuery);
}
