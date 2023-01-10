package com.nussia.item;

import java.util.List;

public interface ItemService {

    List<ItemDTO> getItems(Long userId);

    ItemDTO editItem(ItemDTO itemDTO, Long itemId, Long ownerId);

    ItemDTO addNewItem(ItemDTO itemDTO, Long ownerId);

    ItemDTO getItemById(Long itemId);

    List<ItemDTO> getItemsBySearchQuery(String searchQuery);
}
