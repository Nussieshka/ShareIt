package com.nussia.item.memory;

import com.nussia.Util;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.item.CommentDTO;
import com.nussia.item.ItemDTO;
import com.nussia.item.ItemService;
import com.nussia.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InMemoryItemService implements ItemService {

    private final InMemoryItemRepository itemRepository;

    private final UserService userService;

    public InMemoryItemService(InMemoryItemRepository itemRepository,
                               @Qualifier("InMemoryService") UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDTO addNewItem(ItemDTO itemDTO, Long ownerId) {

        if (ownerId == null || itemDTO == null) {
            throw new BadRequestException("Invalid parameters: ownerId or itemDTO is null");
        }

        Long itemId = itemDTO.getId();

        if (itemId != null) {
            throw new BadRequestException("Cannot add item with itemId");
        } else if (userService.getUser(ownerId) == null) {
            throw new ObjectNotFoundException("User", ownerId);
        }

        Util.validateItemDTO(itemDTO);

        return itemRepository.addItem(itemDTO, ownerId).orElseThrow(() -> new ObjectNotFoundException("Item", itemId));
    }

    @Override
    public List<ItemDTO> getItems(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        return itemRepository.getItems(userId);
    }

    @Override
    public ItemDTO getItemById(Long itemId, Long userId) {
        if (itemId == null) {
            throw new BadRequestException("Invalid parameter: itemId is null");
        }
        return itemRepository.getItemById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item", itemId));
    }

    @Override
    public ItemDTO editItem(ItemDTO itemDTO, Long itemId, Long ownerId) {
        if (ownerId == null || itemId == null || itemDTO == null) {
            throw new BadRequestException("Invalid parameter: ownerId, itemId, or itemDTO is null");
        }
        return itemRepository.editItem(itemDTO, itemId, ownerId).orElseThrow(() ->
                new ObjectNotFoundException("Item", itemId));
    }

    @Override
    public List<ItemDTO> getItemsBySearchQuery(String searchQuery, Long userId) {
        if (searchQuery == null) {
            throw new BadRequestException("Invalid parameter: searchQuery is null");
        } else if (searchQuery.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.getItemsBySearchQuery(searchQuery.toLowerCase());
    }

    @Override
    public CommentDTO addNewComment(CommentDTO commentDTO, Long itemId, Long userId) {
        throw new RuntimeException("Not yet implemented");
    }
}
