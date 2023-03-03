package com.nussia.item;

import com.nussia.exception.BadRequestException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InMemoryItemService implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public ItemDTO addNewItem(ItemDTO itemDTO, Long ownerId) {

        if (ownerId == null || itemDTO == null) {
            throw new BadRequestException();
        } else if (itemDTO.getId() != null) {
            throw new BadRequestException("Cannot add item with itemId");
        } else if (userService.getUser(ownerId) == null) {
            throw new ObjectNotFoundException();
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

        return itemRepository.addItem(itemDTO, ownerId).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public List<ItemDTO> getItems(Long userId) {
        if (userId == null) {
            throw new BadRequestException();
        }
        return itemRepository.getItems(userId);
    }

    @Override
    public ItemDTO getItemById(Long itemId) {
        if (itemId == null) {
            throw new BadRequestException();
        }
        return itemRepository.getItemById(itemId).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public ItemDTO editItem(ItemDTO itemDTO, Long itemId, Long ownerId) {
        if (ownerId == null || itemId == null || itemDTO == null) {
            throw new BadRequestException();
        }
        return itemRepository.editItem(itemDTO, itemId, ownerId).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public List<ItemDTO> getItemsBySearchQuery(String searchQuery) {
        if (searchQuery == null) {
            throw new BadRequestException();
        } else if (searchQuery.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.getItemsBySearchQuery(searchQuery.toLowerCase());
    }
}
