package com.nussia.item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDTO> getItems(Long userId);

    Optional<ItemDTO> editItem(ItemDTO itemDTO, Long itemId, Long ownerId);

    Optional<ItemDTO> addNewItem(ItemDTO itemDTO, Long ownerId);

    Optional<ItemDTO> getItemById(Long itemId);

    List<ItemDTO> getItemsBySearchQuery(String searchQuery);
}
