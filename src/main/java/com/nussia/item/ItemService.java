package com.nussia.item;

import com.nussia.item.comment.CommentDTO;
import com.nussia.item.dto.ItemDTO;

import java.util.List;

public interface ItemService {

    List<ItemDTO> getItems(Long userId);

    ItemDTO editItem(ItemDTO itemDTO, Long itemId, Long ownerId);

    ItemDTO addNewItem(ItemDTO itemDTO, Long ownerId);

    ItemDTO getItemById(Long itemId, Long userId);

    List<ItemDTO> getItemsBySearchQuery(String searchQuery, Long userId);

    CommentDTO addNewComment(CommentDTO commentDTO, Long itemId, Long userId);
}
