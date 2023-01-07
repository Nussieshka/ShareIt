package com.nussia.item;

import com.nussia.exception.BadRequestException;
import com.nussia.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class InMemoryItemService implements ItemService {

    private final ItemRepository ITEM_REPOSITORY;

    private final UserService USER_SERVICE;

    @Override
    public Optional<ItemDTO> addNewItem(ItemDTO itemDTO, Long ownerId) {

        if (ownerId == null || itemDTO == null) {
            throw new BadRequestException();
        } else if (itemDTO.getId() != null) {
            throw new BadRequestException("Cannot add item with itemId");
        } else if (USER_SERVICE.getUser(ownerId).isEmpty()) {
            return Optional.empty();
        }

        String name = itemDTO.getName();
        String description = itemDTO.getDescription();
        if (name == null || description == null || itemDTO.getAvailable() == null) {
            throw new BadRequestException("Item should have these non-null fields: name, description, available");
        } else if (name.isBlank()) {
            throw new BadRequestException("Cannot add item with blank name");
        } else if (description.isBlank()) {
            throw new BadRequestException("Cannot add item with blank description");
        }

        return ITEM_REPOSITORY.addItem(itemDTO, ownerId);
    }

    @Override
    public List<ItemDTO> getItems(Long userId) {
        if (userId == null) {
            throw new BadRequestException();
        }
        return ITEM_REPOSITORY.getItems(userId);
    }

    @Override
    public Optional<ItemDTO> getItemById(Long itemId) {
        if (itemId == null) {
            throw new BadRequestException();
        }
        return ITEM_REPOSITORY.getItemById(itemId);
    }

    @Override
    public Optional<ItemDTO> editItem(ItemDTO itemDTO, Long itemId, Long ownerId) {
        if (ownerId == null || itemId == null || itemDTO == null) {
            throw new BadRequestException();
        }
        return ITEM_REPOSITORY.editItem(itemDTO, itemId, ownerId);
    }

    @Override
    public List<ItemDTO> getItemsBySearchQuery(String searchQuery) {
        if (searchQuery == null) {
            throw new BadRequestException();
        } else if (searchQuery.isBlank()) {
            return Collections.emptyList();
        }

        return ITEM_REPOSITORY.getItemsBySearchQuery(searchQuery.toLowerCase());
    }
}
